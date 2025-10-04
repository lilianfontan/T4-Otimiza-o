package metaheuristics.ga;

import java.util.ArrayList;
import java.util.Random;
import problems.Evaluator;
import solutions.Solution;

public abstract class AbstractGA<G extends Number, F> {

    @SuppressWarnings("serial")
    public class Chromosome extends ArrayList<G> {
    }

    @SuppressWarnings("serial")
    public class Population extends ArrayList<Chromosome> {
    }

    public static boolean verbose = true;
    public static final Random rng = new Random(0);

    protected Evaluator<F> ObjFunction;
    protected int generations;
    protected int popSize;
    protected int chromosomeSize;
    protected double mutationRate;

    protected Double bestCost;
    protected Solution<F> bestSol;
    protected Chromosome bestChromosome;

    // ==== Flags para estratégias evolutivas ====
    protected boolean useSUS = false;
    protected boolean useUniformCrossover = false;
    protected boolean useAdaptiveMutation = false;
    protected boolean useSteadyState = false;

    // Para adaptive mutation
    protected int currentGeneration = 0;

    public abstract Solution<F> createEmptySol();
    protected abstract Solution<F> decode(Chromosome chromosome);
    protected abstract Chromosome generateRandomChromosome();
    protected abstract Double fitness(Chromosome chromosome);
    protected abstract void mutateGene(Chromosome chromosome, Integer locus);

    public AbstractGA(Evaluator<F> objFunction, Integer generations, Integer popSize, Double mutationRate) {
        this.ObjFunction = objFunction;
        this.generations = generations;
        this.popSize = popSize;
        this.chromosomeSize = this.ObjFunction.getDomainSize();
        this.mutationRate = mutationRate;
    }

    public Solution<F> solve() {
        Population population = initializePopulation();
        bestChromosome = getBestChromosome(population);
        bestSol = decode(bestChromosome);
        System.out.println("(Gen. " + 0 + ") BestSol = " + bestSol);

        for (int g = 1; g <= generations; g++) {
            currentGeneration = g;
            Population parents = selectParents(population);
            Population offsprings = crossover(parents);
            Population mutants = mutate(offsprings);
            Population newpopulation = selectPopulation(population, mutants);
            population = newpopulation;
            bestChromosome = getBestChromosome(population);
            if (fitness(bestChromosome) > bestSol.cost) {
                bestSol = decode(bestChromosome);
                if (verbose)
                    System.out.println("(Gen. " + g + ") BestSol = " + bestSol);
            }
        }
        return bestSol;
    }

    protected Population initializePopulation() {
        Population population = new Population();
        while (population.size() < popSize) {
            population.add(generateRandomChromosome());
        }
        return population;
    }

    protected Chromosome getBestChromosome(Population population) {
        double bestFitness = Double.NEGATIVE_INFINITY;
        Chromosome bestChromosome = null;
        for (Chromosome c : population) {
            double fitness = fitness(c);
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestChromosome = c;
            }
        }
        return bestChromosome;
    }

    protected Chromosome getWorseChromosome(Population population) {
        double worseFitness = Double.POSITIVE_INFINITY;
        Chromosome worseChromosome = null;
        for (Chromosome c : population) {
            double fitness = fitness(c);
            if (fitness < worseFitness) {
                worseFitness = fitness;
                worseChromosome = c;
            }
        }
        return worseChromosome;
    }

    // ===== Seleção de pais =====
    protected Population selectParents(Population population) {
        if (useSUS) {
            return stochasticUniversalSelection(population);
        }

        // Torneio binário padrão
        Population parents = new Population();
        while (parents.size() < popSize) {
            int index1 = rng.nextInt(popSize);
            Chromosome parent1 = population.get(index1);
            int index2 = rng.nextInt(popSize);
            Chromosome parent2 = population.get(index2);
            if (fitness(parent1) > fitness(parent2)) {
                parents.add(parent1);
            } else {
                parents.add(parent2);
            }
        }
        return parents;
    }

    private Population stochasticUniversalSelection(Population population) {
        Population parents = new Population();
        double totalFitness = 0.0;
        for (Chromosome c : population) {
            totalFitness += fitness(c);
        }

        double distance = totalFitness / popSize;
        double start = rng.nextDouble() * distance;
        double[] pointers = new double[popSize];
        for (int i = 0; i < popSize; i++) {
            pointers[i] = start + i * distance;
        }

        int index = 0;
        double sum = 0.0;
        for (Chromosome c : population) {
            sum += fitness(c);
            while (index < popSize && sum >= pointers[index]) {
                parents.add(c);
                index++;
            }
        }
        return parents;
    }

    // ===== Crossover =====
    protected Population crossover(Population parents) {
        if (useUniformCrossover) {
            return uniformCrossover(parents);
        }

        // Crossover de 2 pontos (padrão)
        Population offsprings = new Population();
        for (int i = 0; i < popSize; i = i + 2) {
            Chromosome parent1 = parents.get(i);
            Chromosome parent2 = parents.get(i + 1);
            int crosspoint1 = rng.nextInt(chromosomeSize + 1);
            int crosspoint2 = crosspoint1 + rng.nextInt((chromosomeSize + 1) - crosspoint1);
            Chromosome offspring1 = new Chromosome();
            Chromosome offspring2 = new Chromosome();
            for (int j = 0; j < chromosomeSize; j++) {
                if (j >= crosspoint1 && j < crosspoint2) {
                    offspring1.add(parent2.get(j));
                    offspring2.add(parent1.get(j));
                } else {
                    offspring1.add(parent1.get(j));
                    offspring2.add(parent2.get(j));
                }
            }
            offsprings.add(offspring1);
            offsprings.add(offspring2);
        }
        return offsprings;
    }

    private Population uniformCrossover(Population parents) {
        Population offsprings = new Population();
        for (int i = 0; i < popSize; i = i + 2) {
            Chromosome parent1 = parents.get(i);
            Chromosome parent2 = parents.get(i + 1);
            Chromosome offspring1 = new Chromosome();
            Chromosome offspring2 = new Chromosome();
            for (int j = 0; j < chromosomeSize; j++) {
                if (rng.nextBoolean()) {
                    offspring1.add(parent1.get(j));
                    offspring2.add(parent2.get(j));
                } else {
                    offspring1.add(parent2.get(j));
                    offspring2.add(parent1.get(j));
                }
            }
            offsprings.add(offspring1);
            offsprings.add(offspring2);
        }
        return offsprings;
    }

    // ===== Mutação =====
    protected Population mutate(Population offsprings) {
        double effectiveRate = mutationRate;
        if (useAdaptiveMutation) {
            // exemplo simples: aumenta taxa ao longo das gerações
            effectiveRate = mutationRate * (1.0 + (double) currentGeneration / generations);
        }

        for (Chromosome c : offsprings) {
            for (int locus = 0; locus < chromosomeSize; locus++) {
                if (rng.nextDouble() < effectiveRate) {
                    mutateGene(c, locus);
                }
            }
        }
        return offsprings;
    }

    // ===== Seleção da nova população =====
    protected Population selectPopulation(Population oldPop, Population offsprings) {
        if (useSteadyState) {
            // mantém os melhores da união pais + filhos
            Population combined = new Population();
            combined.addAll(oldPop);
            combined.addAll(offsprings);
            // ordena por fitness
            combined.sort((c1, c2) -> Double.compare(fitness(c2), fitness(c1)));
            Population newPop = new Population();
            for (int i = 0; i < popSize; i++) {
                newPop.add(combined.get(i));
            }
            return newPop;
        }

        // elitismo padrão: mantém melhor
        Chromosome worse = getWorseChromosome(offsprings);
        if (fitness(worse) < fitness(bestChromosome)) {
            offsprings.remove(worse);
            offsprings.add(bestChromosome);
        }
        return offsprings;
    }
}
