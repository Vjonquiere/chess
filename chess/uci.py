import random
import numpy as np
from deap import base, creator, tools, algorithms
import chess
import chess.engine
import threading
import csv
import multiprocessing
from concurrent.futures import ThreadPoolExecutor
import pickle
import logging

# --- Constants ---
POP_SIZE = 60
GENS = 100  # Run for multiple generations
MUTATION_RATE = 0.25
CROSSOVER_RATE = 0.7
WEIGHT_RANGE = (0.0, 5.0)
STOCKFISH_ELO = 1650
CURRENT_GEN = 0
NUM_GAMES = 6
MAX_RETRIES = 3

RESULT_WEIGHT = 0.7
MOVES_WEIGHT = 0.15
CAPTURES_WEIGHT = 0.15

JAR_PATH = "target/chess-0.0.4.jar"
STOCKFISH_PATH = "/usr/games/stockfish"
CSV_FILEPATH = "./results.csv"
csv_lock = threading.Lock()

logging.basicConfig(
    filename='error.log',
    level=logging.ERROR,
    format='%(asctime)s %(levelname)s:%(message)s'
)

creator.create("FitnessMax", base.Fitness, weights=(1.0,))
creator.create("Individual", list, fitness=creator.FitnessMax)

toolbox = base.Toolbox()
toolbox.register("attr_float", random.uniform, *WEIGHT_RANGE)
toolbox.register("individual", tools.initRepeat, creator.Individual, toolbox.attr_float, 8)
toolbox.register("population", tools.initRepeat, list, toolbox.individual)

def save_checkpoint(population, current_gen, hof):
    checkpoint_data = {
        "population": population,
        "current_gen": current_gen,
        "hof": hof,
    }
    with open("checkpoint.pkl", "wb") as f:
        pickle.dump(checkpoint_data, f)
    print(f"Checkpoint saved at generation {current_gen}")

def load_checkpoint():
    try:
        with open("checkpoint.pkl", "rb") as f:
            checkpoint_data = pickle.load(f)
        print("Checkpoint loaded.")
        return checkpoint_data
    except FileNotFoundError:
        print("No checkpoint found, starting fresh.")
        return None

def log_game_result(engine2_is_white, game_result, moves, white_captures, black_captures, weights):
    weights_str = ";".join(map(str, weights))
    win = -1
    if engine2_is_white:
        win = 0 if game_result == "0-1" else 1 if game_result == "1-0" else 2
    else:
        win = 1 if game_result == "0-1" else 0 if game_result == "1-0" else 2

    with csv_lock:
        with open(CSV_FILEPATH, mode="a", newline="") as file:
            writer = csv.writer(file)
            writer.writerow([CURRENT_GEN, game_result, win, moves, white_captures, black_captures, engine2_is_white, weights_str])

def run_single_game(weights, first_white):
    moves = 0
    captures_white = 0
    captures_black = 0
    board = chess.Board()

    engine1 = chess.engine.SimpleEngine.popen_uci(STOCKFISH_PATH)
    engine1.configure({"UCI_LimitStrength": True, "UCI_Elo": STOCKFISH_ELO})

    cmdStr = "--ai-weight-w=10000.0"
    cmdStr += "," + ",".join(map(str, weights))
    engine2 = chess.engine.SimpleEngine.popen_uci([
        "java", "-jar", JAR_PATH, "-uci", "--ai-depth=3", "-a=A", "--ai-endgame-w=STANDARD", cmdStr
    ])

    if first_white:
        engines = [engine1, engine2]
    else:
        engines = [engine2, engine1]

    engine2_is_white = engines[0] == engine2

    try:
        turn = 0
        while not board.is_game_over():
            if turn == 0:
                if engines[0] == engine2:
                    result = engines[0].play(board, chess.engine.Limit(time=15000)) # big time to avoid timeout
                else:
                    result = engines[0].play(board, chess.engine.Limit(depth=5))
            else:
                if engines[1] == engine2:
                    result = engines[1].play(board, chess.engine.Limit(time=15000)) # big time to avoid timeout
                else:
                    result = engines[1].play(board, chess.engine.Limit(depth=5))

            if board.is_capture(result.move):
                if board.is_en_passant(result.move):
                    captured_piece_value = 1 
                else:
                    captured_piece = board.piece_at(result.move.to_square)
                    if captured_piece:
                        piece_type = captured_piece.piece_type
                        if piece_type == chess.PAWN:
                            captured_piece_value = 1
                        elif piece_type in (chess.KNIGHT, chess.BISHOP):
                            captured_piece_value = 3
                        elif piece_type == chess.ROOK:
                            captured_piece_value = 5
                        elif piece_type == chess.QUEEN:
                            captured_piece_value = 9
                        else:
                            captured_piece_value = 0
                    else:
                        captured_piece_value = 0 

                capturer_color = board.color_at(result.move.from_square)
                if capturer_color == chess.WHITE:
                    captures_white += captured_piece_value
                else:
                    captures_black += captured_piece_value

            board.push(result.move)
            moves += 1
            turn = 1 - turn

    except Exception as e:
        logging.error(f"Game failed: {e}", exc_info=True)
        print("Game failed: ", e)

        try:
            engine1.quit()
        except Exception as ex:
            logging.error("Caught in engine1.quit")
            logging.eror(ex)
            
        try:
            engine2.quit()
        except Exception as ex:
            logging.error("Caught in engine2.quit")
            logging.error(ex)

        return(0.0, engine2_is_white, moves, captures_white, captures_black, board.result())	


    try:
        engine1.quit()
    except Exception as ex:
        logging.error("Caught in engine1.quit")
        logging.eror(ex)
        
    try:
        engine2.quit()
    except Exception as ex:
        logging.error("Caught in engine2.quit")
        logging.error(ex)
    game_result = board.result()

    res_string = ""
    if engines[0] == engine2:  # engine2 played as White
        if game_result == "1-0":
            base_score = 1.0
            res_string = "Engine2 wins"
        elif game_result == "0-1":
            base_score = 0.0
            res_string = "Engine1 wins"
        else:
            base_score = 0.5
            res_string = "Draw"
    else: # engine2 played as Black
        if game_result == "1-0":
            base_score = 0.0
            res_string = "Engine1 wins"
        elif game_result == "0-1":
            base_score = 1.0
            res_string = "Engine2 wins"
        else:
            base_score = 0.5
            res_string = "Draw"

    print("Game Over!", board.result(), " / ", moves, " moves / ", res_string)

    log_game_result(engine2_is_white, game_result, moves, captures_white, captures_black, weights)

    bonus_moves = (1.0 / moves if moves > 0 else 0.0)
    bonus_captures = max(0, (captures_white-captures_black if engine2_is_white else captures_black-captures_white))/39

    bonus = bonus_moves*MOVES_WEIGHT + bonus_captures*CAPTURES_WEIGHT

    score = base_score*RESULT_WEIGHT
    if engine2_is_white and game_result == "1-0" or not engine2_is_white and game_result == "0-1":
        score += bonus

    return (score, engine2_is_white, moves, captures_white, captures_black, game_result)


def run_single_game_with_retries(weights, first_white):
    attempts = 0
    while attempts < MAX_RETRIES:
        try:
            result = run_single_game(weights, first_white)
            return result
        except Exception as e:
            attempts += 1
            logging.error(f"Attempt {attempts}: Game failed with error: {e}")
    return (0.0, None, 0, 0, 0, "Error")

def eval_fitness(weights):
    total_score = 0.0

    is_first_white = random.choice([True, False])
    for _ in range(NUM_GAMES):
        (score, engine2_is_white, moves, captures_white, captures_black, game_result) = run_single_game_with_retries(weights, is_first_white)
        print("SCORE : ", score)
        total_score += score
        is_first_white = not is_first_white

    print("TOTAL SCORE : ", total_score)

    avg_score = total_score / NUM_GAMES

    print("AVERAGE SCORE : ", avg_score)
    return (avg_score,)

toolbox.register("evaluate", eval_fitness)
toolbox.register("mate", tools.cxBlend, alpha=0.2)


def mutGaussian(individual, mu, indpb):
    sigma = adaptive_sigma(CURRENT_GEN, GENS)
    for i in range(len(individual)):
        if random.random() < indpb:
            individual[i] += random.gauss(mu, sigma)
            individual[i] = max(WEIGHT_RANGE[0], min(WEIGHT_RANGE[1], individual[i]))
    return (individual,)


def adaptive_sigma(current_gen, total_gens):
    base_sigma = 0.5
    decay = 0.98
    return base_sigma * (decay ** current_gen)

toolbox.register("mutate", mutGaussian, mu=0, indpb=MUTATION_RATE)
toolbox.register("select", tools.selTournament, tournsize=3)

def parallel_eval(population):
    with ThreadPoolExecutor(max_workers=multiprocessing.cpu_count()) as executor:
        fitness_values = list(executor.map(toolbox.evaluate, population))
    for ind, fit in zip(population, fitness_values):
        ind.fitness.values = fit

def main():
    global CURRENT_GEN
    checkpoint_data = load_checkpoint()
    
    if checkpoint_data:
        pop = checkpoint_data["population"]
        CURRENT_GEN = checkpoint_data["current_gen"]
        hof = checkpoint_data["hof"]
    else:
        pop = toolbox.population(n=POP_SIZE)
        hof = tools.HallOfFame(1)
        CURRENT_GEN = 0
        
    stats = tools.Statistics(lambda ind: ind.fitness.values)
    stats.register("max", np.max)
    stats.register("avg", np.mean)
    
    # Number of elite preserve
    NUM_ELITES = 5
    
    for gen in range(CURRENT_GEN, GENS):
        print(f"Generation {gen+1}")
        
        parallel_eval(pop)
        hof.update(pop)
        
        elites = tools.selBest(pop, k=NUM_ELITES)
        elites = [toolbox.clone(ind) for ind in elites]

        all_elites = elites + [toolbox.clone(hof[0])]
        if len(hof) > 1:
            all_elites = all_elites + [toolbox.clone(hof[1])]
        if len(hof) > 2:
            all_elites = all_elites + [toolbox.clone(hof[2])]
        all_elites = tools.selBest(all_elites, k=NUM_ELITES)
        
        parents = toolbox.select(pop, len(pop))
        offspring = [toolbox.clone(ind) for ind in parents]
        
        offspring = algorithms.varAnd(offspring, toolbox, cxpb=CROSSOVER_RATE, mutpb=MUTATION_RATE)
        
        parallel_eval(offspring)
        
        hof.update(offspring)
        
        if NUM_ELITES > 0:
            worst_indices = sorted(range(len(offspring)), 
                                  key=lambda i: offspring[i].fitness.values[0])[:NUM_ELITES]
            
            for i, idx in enumerate(worst_indices):
                offspring[idx] = all_elites[i]
        
        pop[:] = offspring

        if (gen + 1) % 10 == 0:
            num_random = int(0.1 * len(pop))
            print(f"Injecting {num_random} random individuals to maintain diversity...")
            random_individuals = toolbox.population(n=num_random)
            pop[-num_random:] = random_individuals 
            parallel_eval(pop[-num_random:])

            elite_individuals = tools.selBest(hof, k=min(NUM_ELITES, len(hof)))
            num_elites_available = len(elite_individuals)
            worst_indices = sorted(range(len(pop)), key=lambda i: pop[i].fitness.values[0])[:num_elites_available]

            for i, idx in enumerate(worst_indices):
                pop[idx] = toolbox.clone(elite_individuals[i])
        
        record = stats.compile(pop)
        print(f"  Best fitness: {record['max']}")
        print(f"  Average fitness: {record['avg']}")
        print(f"  Best individual: {hof[0]}")
        print(f"  Best fitness value: {hof[0].fitness.values[0]}")
        
        save_checkpoint(pop, gen + 1, hof)
        CURRENT_GEN += 1

    best_weights = hof[0]
    print("\nBest Weights Found:")
    print(best_weights)
    print(f"Final fitness: {best_weights.fitness.values[0]}")

if __name__ == "__main__":
    main()