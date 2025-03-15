import subprocess
import random
import numpy as np
from deap import base, creator, tools, algorithms

# --- Constants ---
POP_SIZE = 20
GENS = 50
MUTATION_RATE = 0.2
CROSSOVER_RATE = 0.5
WEIGHT_RANGE = (-10, 10)
JAR_PATH = "target/chess-0.0.3.jar"


creator.create("FitnessMax", base.Fitness, weights=(1.0,))
creator.create("Individual", list, fitness=creator.FitnessMax)

def eval_fitness(weights):
    white_weights = weights[:7]
    black_weights = weights[7:]


    cmdStr = "-a=A -ai-depth=4"
    cmdStr += " --ai-weight-w="
    for weight in white_weights:
        cmdStr += str(weight) + ","
    cmdStr = cmdStr[:-1]
    
    cmdStr += " --ai-weight-b="
    for weight in black_weights:
        cmdStr += str(weight) + ","
    cmdStr = cmdStr[:-1]
    cmdStr += " --ai-training"

    
    cmd = ["java", "-jar", JAR_PATH] + cmdStr.split()


    print("Running:", "java -jar", JAR_PATH, cmdStr)

    
    try:
        result = subprocess.run(cmd, text=True, capture_output=True, timeout=60)
        outcome = result.returncode

        if outcome == 3:
            return (1.0,)  # White wins
        elif outcome == 4:
            return (0.0,)  # Black wins
        elif outcome == 5:
            return (0.5,)  # Draw
        else:
            print("Unexpected outcome:", outcome)
            return (0.0,)

    except Exception as e:
        print("Error:", e)
        return (0.0,)


toolbox = base.Toolbox()
toolbox.register("attr_int", random.randint, *WEIGHT_RANGE)
toolbox.register("individual", tools.initRepeat, creator.Individual, toolbox.attr_int, 14)
toolbox.register("population", tools.initRepeat, list, toolbox.individual)
toolbox.register("evaluate", eval_fitness)

def int_crossover(ind1, ind2):
    for i in range(len(ind1)):
        if random.random() < CROSSOVER_RATE:
            ind1[i], ind2[i] = round((ind1[i] + ind2[i]) / 2), round((ind1[i] + ind2[i]) / 2)
    return ind1, ind2

def int_mutation(individual):
    for i in range(len(individual)):
        if random.random() < MUTATION_RATE:
            individual[i] += random.randint(-2, 2)
            individual[i] = max(WEIGHT_RANGE[0], min(WEIGHT_RANGE[1], individual[i]))
    return individual,

toolbox.register("mate", int_crossover)
toolbox.register("mutate", int_mutation)
toolbox.register("select", tools.selTournament, tournsize=3)

def main():
    pop = toolbox.population(n=POP_SIZE)
    hof = tools.HallOfFame(1)
    stats = tools.Statistics(lambda ind: ind.fitness.values)
    stats.register("max", np.max)

    pop, _ = algorithms.eaSimple(pop, toolbox, cxpb=CROSSOVER_RATE, mutpb=MUTATION_RATE, 
                                 ngen=GENS, stats=stats, halloffame=hof, verbose=True)
    
    best_weights = hof[0]
    print("\nBest Weights Found:")
    print("White:", best_weights[:7])
    print("Black:", best_weights[7:])

if __name__ == "__main__":
    main()