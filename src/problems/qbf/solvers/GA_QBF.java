package problems.qbf.solvers;

import java.io.IOException;

import metaheuristics.ga.AbstractGA;
import problems.qbf.QBF;
import problems.qbf.SCQBF;
import solutions.Solution;

public class GA_QBF extends AbstractGA<Integer, Integer> {

    public GA_QBF(Integer generations, Integer popSize, Double mutationRate, QBF objFunction) {
        super(objFunction, generations, popSize, mutationRate);
    }

    @Override
    public Solution<Integer> createEmptySol() {
        Solution<Integer> sol = new Solution<Integer>();
        sol.cost = 0.0;
        return sol;
    }

    @Override
    protected Solution<Integer> decode(Chromosome chromosome) {
        Solution<Integer> solution = createEmptySol();
        for (int locus = 0; locus < chromosome.size(); locus++) {
            if (chromosome.get(locus) == 1) {
                solution.add(locus);
            }
        }
        ObjFunction.evaluate(solution);
        return solution;
    }

    @Override
    protected Chromosome generateRandomChromosome() {
        Chromosome chromosome = new Chromosome();
        for (int i = 0; i < chromosomeSize; i++) {
            chromosome.add(rng.nextInt(2));
        }
        return chromosome;
    }

    @Override
    protected Double fitness(Chromosome chromosome) {
        return decode(chromosome).cost;
    }

    @Override
    protected void mutateGene(Chromosome chromosome, Integer locus) {
        chromosome.set(locus, 1 - chromosome.get(locus));
    }

    public static void main(String[] args) throws IOException {

        int generations = 1000;
        int pop1 = 100, pop2 = 200;
        double mut1 = 1.0 / 100.0, mut2 = 2.0 / 100.0;

        String filename = "instances/qbfsc/scqbf025.txt"; // o arquivo que você mostrou

        long start, end;
        Solution<Integer> best;

        // 1. Padrão
        System.out.println("==== PADRÃO ====");
        start = System.currentTimeMillis();
        GA_QBF ga1 = new GA_QBF(generations, pop1, mut1, new SCQBF(filename, 1000));
        best = ga1.solve();
        end = System.currentTimeMillis();
        System.out.printf("Melhor solução: %s\nTempo: %.2fs\n\n", best, (end - start) / 1000.0);

        // 2. +POP
        System.out.println("==== PADRÃO + POP ====");
        start = System.currentTimeMillis();
        GA_QBF ga2 = new GA_QBF(generations, pop2, mut1, new SCQBF(filename, 1000));
        best = ga2.solve();
        end = System.currentTimeMillis();
        System.out.printf("Melhor solução: %s\nTempo: %.2fs\n\n", best, (end - start) / 1000.0);

        // 3. +MUT
        System.out.println("==== PADRÃO + MUT ====");
        start = System.currentTimeMillis();
        GA_QBF ga3 = new GA_QBF(generations, pop1, mut2, new SCQBF(filename, 1000));
        best = ga3.solve();
        end = System.currentTimeMillis();
        System.out.printf("Melhor solução: %s\nTempo: %.2fs\n\n", best, (end - start) / 1000.0);

        // 4. +EVOL1
        System.out.println("==== PADRÃO + EVOL1 (λ=500) ====");
        start = System.currentTimeMillis();
        GA_QBF ga4 = new GA_QBF(generations, pop1, mut1, new SCQBF(filename, 500));
        best = ga4.solve();
        end = System.currentTimeMillis();
        System.out.printf("Melhor solução: %s\nTempo: %.2fs\n\n", best, (end - start) / 1000.0);

        // 5. +EVOL2
        System.out.println("==== PADRÃO + EVOL2 (λ=5000) ====");
        start = System.currentTimeMillis();
        GA_QBF ga5 = new GA_QBF(generations, pop1, mut1, new SCQBF(filename, 5000));
        best = ga5.solve();
        end = System.currentTimeMillis();
        System.out.printf("Melhor solução: %s\nTempo: %.2fs\n\n", best, (end - start) / 1000.0);
        System.out.println("==== Fim da Execução ====");
        
    }
}
