import random
import numpy as np
from deap import base, creator, tools, algorithms
import chess
import chess.engine
import threading
import csv
import multiprocessing

# --- Constants ---
POP_SIZE = 20
GENS = 1
MUTATION_RATE = 0.2
CROSSOVER_RATE = 0.5
WEIGHT_RANGE = (0.0, 1.0)
STOCKFISH_ELO = 1350

# --- Java Program Path ---
JAR_PATH = "target/chess-0.0.4.jar"
STOCKFISH_PATH = "/usr/games/stockfish"
CSV_FILEPATH = "./results.csv"
csv_lock = threading.Lock()

# --- Genetic Algorithm Setup ---
creator.create("FitnessMax", base.Fitness, weights=(1.0,))
creator.create("Individual", list, fitness=creator.FitnessMax)

def eval_fitness(weights):
    moves = 0
    board = chess.Board()

    engine1 = chess.engine.SimpleEngine.popen_uci(STOCKFISH_PATH)
    engine1.configure({"UCI_LimitStrength": True, "UCI_Elo": STOCKFISH_ELO})

    cmdStr = "--ai-weight-w=1000.0"
    cmdStr += "," + ",".join(map(str, weights))

    engine2 = chess.engine.SimpleEngine.popen_uci(["java", "-jar", JAR_PATH, "-uci", "-ai-depth=2", "-a=A", "--ai-endgame-w=STANDARD", cmdStr])


    engines = [engine1, engine2]
    random.shuffle(engines)
    turn = 0
    engine2_is_white = engines[0] == engine2

    while not board.is_game_over():
        if turn == 0:
            if engines[0] == engine2:
                result = engines[0].play(board, chess.engine.Limit(time=15000))
            else:
                result = engines[0].play(board, chess.engine.Limit(time=0.5))
        else:
            if engines[1] == engine2:
                result = engines[1].play(board, chess.engine.Limit(time=15000))
            else:
                result = engines[1].play(board, chess.engine.Limit(time=0.5))

        board.push(result.move)
        moves += 1

        turn = 1 - turn  # Switch turn

    print("Game Over!", board.result(), " / ", moves, " moves")

    engine1.quit()
    engine2.quit()

    game_result = board.result()

    log_game_result(engine2_is_white, game_result, moves, 0, weights)

    if engines[0] == engine2:  # If engine2 played as White
        if game_result == "1-0":
            return (1.0,)
        elif game_result == "0-1":
            return (0.0,)
        else:
            return (0.5,)
    else:
        if game_result == "1-0":
            return (0.0,)
        elif game_result == "0-1":
            return (1.0,)
        else:
            return (0.5,)

def log_game_result(engine2_is_white, game_result, moves, captures, weights):
    weights_str = ";".join(map(str, weights))

    if engine2_is_white:
        white_result = game_result
        black_result = "1-0" if game_result == "0-1" else "0-1" if game_result == "1-0" else "1/2-1/2"
    else:
        black_result = game_result
        white_result = "1-0" if game_result == "0-1" else "0-1" if game_result == "1-0" else "1/2-1/2"

    with csv_lock:
        with open(CSV_FILEPATH, mode="a", newline="") as file:
            writer = csv.writer(file)
            writer.writerow([white_result, black_result, moves, captures, engine2_is_white, weights_str])

toolbox = base.Toolbox()
toolbox.register("attr_float", random.uniform, *WEIGHT_RANGE)
toolbox.register("individual", tools.initRepeat, creator.Individual, toolbox.attr_float, 8)
toolbox.register("population", tools.initRepeat, list, toolbox.individual)
toolbox.register("evaluate", eval_fitness)
toolbox.register("mate", tools.cxBlend, alpha=0.5)
toolbox.register("mutate", tools.mutGaussian, mu=0, sigma=2, indpb=MUTATION_RATE)
toolbox.register("select", tools.selTournament, tournsize=3)

def parallel_eval(population):
    # Use Pool to parallelize fitness evaluations
    with multiprocessing.Pool(processes=multiprocessing.cpu_count()) as pool:
        fitness_values = pool.map(toolbox.evaluate, population)
    for ind, fit in zip(population, fitness_values):
        ind.fitness.values = fit

def main():
    with open(CSV_FILEPATH, mode="w", newline="") as file:
        writer = csv.writer(file)
        writer.writerow(["White Result", "Black Result", "Moves", "Captures", "White", "Weights"])

    pop = toolbox.population(n=POP_SIZE)
    hof = tools.HallOfFame(1)
    stats = tools.Statistics(lambda ind: ind.fitness.values)
    stats.register("max", np.max)

    # Use parallel_eval to evaluate the fitness function in parallel
    parallel_eval(pop)

    pop, _ = algorithms.eaSimple(pop, toolbox, cxpb=CROSSOVER_RATE, mutpb=MUTATION_RATE,
                                 ngen=GENS, stats=stats, halloffame=hof, verbose=True)

    best_weights = hof[0]
    print("\nBest Weights Found:")
    print("White:", best_weights[:7])
    print("Black:", best_weights[7:])

if __name__ == "__main__":
    main()
