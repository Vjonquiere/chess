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
POP_SIZE = 30
GENS = 50  # Run for multiple generations
MUTATION_RATE = 0.2
CROSSOVER_RATE = 0.5
WEIGHT_RANGE = (0.0, 1.0)
STOCKFISH_ELO = 1350
CURRENT_GEN = 0

NUM_GAMES = 3  # Number of games per evaluation
MATERIAL_WEIGHT = 0.15
SPEED_WEIGHT = 0.05
OUTCOME_WEIGHT = 0.8

# --- Java Program Path ---
JAR_PATH = "target/chess-0.0.4.jar"
STOCKFISH_PATH = "/usr/games/stockfish"
CSV_FILEPATH = "./results.csv"
csv_lock = threading.Lock()

# --- Genetic Algorithm Setup ---
creator.create("FitnessMax", base.Fitness, weights=(1.0,))
creator.create("Individual", list, fitness=creator.FitnessMax)

logging.basicConfig(
    filename='error.log',
    level=logging.ERROR,
    format='%(asctime)s %(levelname)s:%(message)s'
)

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
    
def calculate_material(board):
    """Calculate material balance using standard chess piece values"""
    piece_values = {
        chess.PAWN: 1,
        chess.KNIGHT: 3,
        chess.BISHOP: 3,
        chess.ROOK: 5,
        chess.QUEEN: 9
    }
    white, black = 0, 0
    for square in chess.SQUARES:
        piece = board.piece_at(square)
        if piece:
            value = piece_values.get(piece.piece_type, 0)
            if piece.color == chess.WHITE:
                white += value
            else:
                black += value
    return white, black

def play_single_game(weights):
    """Play one game and return metrics"""
    board = chess.Board()
    engine1 = chess.engine.SimpleEngine.popen_uci(STOCKFISH_PATH)
    engine1.configure({"UCI_LimitStrength": True, "UCI_Elo": STOCKFISH_ELO})

    cmdStr = f"--ai-weight-w={','.join(map(str, weights))}"
    engine2 = chess.engine.SimpleEngine.popen_uci(
        ["java", "-jar", JAR_PATH, "-uci", "-ai-depth=4", "-a=A", cmdStr]
    )

    engines = [engine1, engine2]
    random.shuffle(engines)
    engine2_is_white = engines[0] == engine2
    
    moves = 0
    captures_white = 0
    captures_black = 0
    
    try:
        while not board.is_game_over():
            current_engine = engines[moves % 2]
            result = current_engine.play(board, chess.engine.Limit(time=0.5))
            
            if board.is_capture(result.move):
                if board.color_at(result.move.to_square) == chess.WHITE:
                    captures_black += 1
                else:
                    captures_white += 1
            
            board.push(result.move)
            moves += 1
            
    except Exception as e:
        logging.error(f"Game failed: {e}")
    
    game_result = board.result()
    if engine2_is_white:
        outcome = 1.0 if game_result == "1-0" else 0.0 if game_result == "0-1" else 0.5
    else:
        outcome = 1.0 if game_result == "0-1" else 0.0 if game_result == "1-0" else 0.5
    
    white_material, black_material = calculate_material(board)
    material_diff = (white_material - black_material) if engine2_is_white else (black_material - white_material)
    material_norm = material_diff / 39.0  # Normalize to [-1, 1]
    
    speed_bonus = 0.0
    if outcome == 1.0:
        speed_bonus = 1.0 - (moves / 100.0)  # Shorter games are better
    
    engine1.quit()
    engine2.quit()
    
    return {
        "outcome": outcome,
        "material": max(min(material_norm, 1.0), -1.0),
        "speed": speed_bonus,
        "moves": moves,
        "captures_white": captures_white,
        "captures_black": captures_black,
        "engine2_white": engine2_is_white
    }

def eval_fitness(weights):
    total_outcome = 0.0
    total_material = 0.0
    total_speed = 0.0
    total_moves = 0
    total_captures_white = 0
    total_captures_black = 0

    total_value = 0.0
    
    for _ in range(NUM_GAMES):
        game_result = play_single_game(weights)
        total_outcome += game_result["outcome"]
        total_value += game_result["outcome"] * OUTCOME_WEIGHT
        total_material += game_result["material"]
        total_value += game_result["material"] * MATERIAL_WEIGHT
        total_speed += game_result["speed"]
        total_value += game_result["speed"] * SPEED_WEIGHT
        total_moves += game_result["moves"]
        total_captures_white += game_result["captures_white"]
        total_captures_black += game_result["captures_black"]
    
    avg_outcome = total_outcome / NUM_GAMES
    avg_material = total_material / NUM_GAMES
    avg_speed = total_speed / NUM_GAMES
    avg_moves = total_moves / NUM_GAMES
    
    avg_fitness = (
        total_value / NUM_GAMES,
    )
    
    with csv_lock:
        with open(CSV_FILEPATH, "a", newline="") as f:
            writer = csv.writer(f)
            writer.writerow([
                CURRENT_GEN,
                avg_fitness[0],
                avg_outcome,
                avg_material,
                avg_speed,
                avg_moves,
                total_captures_white // NUM_GAMES,
                total_captures_black // NUM_GAMES,
                ",".join(map(str, weights))
            ])
    
    return (avg_fitness,)

toolbox = base.Toolbox()
toolbox.register("attr_float", random.uniform, *WEIGHT_RANGE)
toolbox.register("individual", tools.initRepeat, creator.Individual, toolbox.attr_float, 8)
toolbox.register("population", tools.initRepeat, list, toolbox.individual)
toolbox.register("evaluate", eval_fitness)
toolbox.register("mate", tools.cxBlend, alpha=0.5)
toolbox.register("mutate", tools.mutGaussian, mu=0, sigma=2, indpb=MUTATION_RATE)
toolbox.register("select", tools.selTournament, tournsize=3)

def clip_individual(individual, low=0.0, up=1.0):
    return [min(max(gene, low), up) for gene in individual]

def parallel_eval(population):
    with ThreadPoolExecutor(max_workers=multiprocessing.cpu_count()) as executor:
        fitness_values = list(executor.map(toolbox.evaluate, population))

    for ind, fit in zip(population, fitness_values):
        ind[:] = clip_individual(ind)
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
    print("White:", best_weights)



if __name__ == "__main__":
    main()
