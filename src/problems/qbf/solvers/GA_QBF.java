package problems.qbf.solvers;

import java.io.IOException;
import metaheuristics.ga.AbstractGA;
import problems.qbf.QBF;
import solutions.Solution;

public class GA_QBF extends AbstractGA<Integer, Integer> {

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
                solution.add(new Integer(locus));
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
        String filename = "instances/qbf/qbf100";

        int generations = 500;
        int P1 = 100;
        int P2 = 200;
        double M1 = 1.0 / 100.0;
        double M2 = 0.05;

        System.out.println("========== PADRÃO ==========");
        GA_QBF ga1 = new GA_QBF(generations, P1, M1, filename);
        ga1.solve();

        System.out.println("========== PADRÃO + POP ==========");
        GA_QBF ga2 = new GA_QBF(generations, P2, M1, filename);
        ga2.solve();

        System.out.println("========== PADRÃO + MUT ==========");
        GA_QBF ga3 = new GA_QBF(generations, P1, M2, filename);
        ga3.solve();

        System.out.println("========== PADRÃO + EVOL1 (SUS) ==========");
        GA_QBF ga4 = new GA_QBF(generations, P1, M1, filename);
        ga4.useSUS = true;
        ga4.solve();

        System.out.println("========== PADRÃO + EVOL2 (Uniform Crossover) ==========");
        GA_QBF ga5 = new GA_QBF(generations, P1, M1, filename);
        ga5.useUniformCrossover = true;
        ga5.solve();
        System.out.println("========== Fim da Execução ==========");
    }
}
