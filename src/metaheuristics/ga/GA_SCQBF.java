package metaheuristics.ga;

import problems.Evaluator;
import solutions.Solution;

public class GA_SCQBF extends AbstractGA<Integer, Integer> {

    public GA_SCQBF(Evaluator<Integer> objFunction, Integer generations, Integer popSize, Double mutationRate) {
        super(objFunction, generations, popSize, mutationRate);
    }

    @Override
    public Solution<Integer> createEmptySol() {
        return new Solution<>();
    }

    @Override
    protected Solution<Integer> decode(Chromosome chromosome) {
        Solution<Integer> sol = createEmptySol();
        for (int i = 0; i < chromosomeSize; i++) {
            if (chromosome.get(i) == 1)
                sol.add(i);
        }
        sol.cost = ObjFunction.evaluate(sol);
        return sol;
    }

    @Override
    protected Chromosome generateRandomChromosome() {
        Chromosome chromosome = new Chromosome();
        for (int i = 0; i < chromosomeSize; i++) {
            chromosome.add(rng.nextDouble() < 0.5 ? 0 : 1);
        }
        return chromosome;
    }

    @Override
    protected Double fitness(Chromosome chromosome) {
        Solution<Integer> sol = decode(chromosome);
        return sol.cost;
    }

    @Override
    protected void mutateGene(Chromosome chromosome, Integer locus) {
        int value = chromosome.get(locus);
        chromosome.set(locus, 1 - value);
    }
}
