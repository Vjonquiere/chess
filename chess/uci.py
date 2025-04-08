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
POP_SIZE = 25
GENS = 50  # Run for multiple generations
MUTATION_RATE = 0.2
CROSSOVER_RATE = 0.5
WEIGHT_RANGE = (0.0, 1.0)
STOCKFISH_ELO = 1350
CURRENT_GEN = 0
NUM_GAMES = 3

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

def run_single_game(weights):
    moves = 0
    captures_white = 0
    captures_black = 0
    board = chess.Board()

    engine1 = chess.engine.SimpleEngine.popen_uci(STOCKFISH_PATH)
    engine1.configure({"UCI_LimitStrength": True, "UCI_Elo": STOCKFISH_ELO})

    cmdStr = "--ai-weight-w=1000.0"
    cmdStr += "," + ",".join(map(str, weights))
    engine2 = chess.engine.SimpleEngine.popen_uci([
        "java", "-jar", JAR_PATH, "-uci", "-ai-depth=4", "-a=A", "--ai-endgame-w=STANDARD", cmdStr
    ])

    engines = [engine1, engine2]
    random.shuffle(engines)
    engine2_is_white = engines[0] == engine2

    try:
        turn = 0
        while not board.is_game_over():
            if turn == 0:
                if engines[0] == engine2:
                    result = engines[0].play(board, chess.engine.Limit(time=150))
                else:
                    result = engines[0].play(board, chess.engine.Limit(time=0.5))
            else:
                if engines[1] == engine2:
                    result = engines[1].play(board, chess.engine.Limit(time=150))
                else:
                    result = engines[1].play(board, chess.engine.Limit(time=0.5))

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
        engine1.quit()
        engine2.quit()
        return (0.0, 0.0, engine2_is_white, moves, captures_white, captures_black, board.result())

    engine1.quit()
    engine2.quit()
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

    bonus_moves = 1.0 / moves if moves > 0 else 0.0
    bonus_captures = (captures_white if engine2_is_white else captures_black) * 0.1

    bonus = bonus_moves + bonus_captures

    return (base_score, bonus, engine2_is_white, moves, captures_white, captures_black, game_result)

def eval_fitness(weights):
    total_base = 0.0
    total_bonus = 0.0

    for _ in range(NUM_GAMES):
        (base_score, bonus, engine2_is_white, moves, captures_white, captures_black, game_result) = run_single_game(weights)
        total_base += base_score
        total_bonus += bonus

    avg_base = total_base / NUM_GAMES
    avg_bonus = total_bonus / NUM_GAMES

    fitness = avg_base + avg_bonus
    return (fitness,)

toolbox.register("evaluate", eval_fitness)
toolbox.register("mate", tools.cxBlend, alpha=0.5)


def boundedMutGaussian(individual, mu, sigma, indpb, low=0.0, up=1.0):
    for i in range(len(individual)):
        if random.random() < indpb:
            individual[i] += random.gauss(mu, sigma)
            individual[i] = min(max(individual[i], low), up)
    return (individual,)

toolbox.register("mutate", boundedMutGaussian, mu=0, sigma=0.1, indpb=MUTATION_RATE, low=0.0, up=1.0)
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

    for gen in range(CURRENT_GEN, GENS):
        print(f"Generation {gen+1}")
        parallel_eval(pop)
        pop = algorithms.varAnd(pop, toolbox, cxpb=CROSSOVER_RATE, mutpb=MUTATION_RATE)
        parallel_eval(pop)
        hof.update(pop)
        record = stats.compile(pop)
        print(record)
        save_checkpoint(pop, gen + 1, hof)
        CURRENT_GEN += 1

    best_weights = hof[0]
    print("\nBest Weights Found:")
    print(best_weights)

if __name__ == "__main__":
    main()
