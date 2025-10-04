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

    public GA_QBF(Integer generations, Integer popSize, Double mutationRate, String filename) throws IOException {
        super(new QBF(filename), generations, popSize, mutationRate);
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
            chromosome.add(rng.nextInt(2)); // 0 ou 1
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

    /**
     * Método principal: executa todos os experimentos padrão e variações:
     * 1. PADRÃO
     * 2. PADRÃO+POP
     * 3. PADRÃO+MUT
     * 4. PADRÃO+EVOL1 (SCQBF)
     * 5. PADRÃO+EVOL2 (SCQBF com λ diferente)
     */
    public static void main(String[] args) throws IOException {

        // Parâmetros base
        int generations = 1000;
        int pop1 = 100;
        int pop2 = 200; // +POP
        double mut1 = 1.0 / 100.0;
        double mut2 = 2.0 / 100.0; // +MUT
        String filenameQBF = "instances/qbf/qbf100";
        String filenameCoverage = "instances/scqbf/coverage100";

        long start, end;
        Solution<Integer> best;

        // -------------------------------
        System.out.println("==== EXPERIMENTO 1: PADRÃO ====");
        start = System.currentTimeMillis();
        GA_QBF ga1 = new GA_QBF(generations, pop1, mut1, filenameQBF);
        best = ga1.solve();
        end = System.currentTimeMillis();
        System.out.printf("Melhor solução: %s\nTempo: %.2fs\n\n",
                best, (end - start) / 1000.0);

        // -------------------------------
        System.out.println("==== EXPERIMENTO 2: PADRÃO + POP ====");
        start = System.currentTimeMillis();
        GA_QBF ga2 = new GA_QBF(generations, pop2, mut1, filenameQBF);
        best = ga2.solve();
        end = System.currentTimeMillis();
        System.out.printf("Melhor solução: %s\nTempo: %.2fs\n\n",
                best, (end - start) / 1000.0);

        // -------------------------------
        System.out.println("==== EXPERIMENTO 3: PADRÃO + MUT ====");
        start = System.currentTimeMillis();
        GA_QBF ga3 = new GA_QBF(generations, pop1, mut2, filenameQBF);
        best = ga3.solve();
        end = System.currentTimeMillis();
        System.out.printf("Melhor solução: %s\nTempo: %.2fs\n\n",
                best, (end - start) / 1000.0);

        // -------------------------------
        System.out.println("==== EXPERIMENTO 4: PADRÃO + EVOL1 (SCQBF λ=1000) ====");
        start = System.currentTimeMillis();
        SCQBF scqbf1 = new SCQBF(filenameQBF, filenameCoverage, 1000.0);
        GA_QBF ga4 = new GA_QBF(generations, pop1, mut1, scqbf1);
        best = ga4.solve();
        end = System.currentTimeMillis();
        System.out.printf("Melhor solução: %s\nTempo: %.2fs\n\n",
                best, (end - start) / 1000.0);

        // -------------------------------
        System.out.println("==== EXPERIMENTO 5: PADRÃO + EVOL2 (SCQBF λ=5000) ====");
        start = System.currentTimeMillis();
        SCQBF scqbf2 = new SCQBF(filenameQBF, filenameCoverage, 5000.0);
        GA_QBF ga5 = new GA_QBF(generations, pop1, mut1, scqbf2);
        best = ga5.solve();
        end = System.currentTimeMillis();
        System.out.printf("Melhor solução: %s\nTempo: %.2fs\n\n",
                best, (end - start) / 1000.0);
        
        System.out.println("==== Fim da Execução ====");
    }
}
