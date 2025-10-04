package problems.qbf.solvers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import metaheuristics.ga.GA_SCQBF;
import problems.qbf.SCQBF;
import solutions.Solution;

/**
 * Executa 5 experimentos automáticos com o problema MAX-SC-QBF
 * e salva resultados em arquivo CSV.
 *
 * Experimentos:
 *  1. PADRÃO
 *  2. +POP (x2 população)
 *  3. +MUT (x2 taxa de mutação)
 *  4. +EVOL1 (x2 gerações)
 *  5. +EVOL2 (x2 pop e x2 gerações)
 */
public class GA_QBF {

    private static final String RESULTS_DIR = "results";

    public static void main(String[] args) throws IOException {

        Locale.setDefault(Locale.US);

        // ======== INSTÂNCIAS ========
        String[] instanceFiles = {
                "instances/qbfsc/scqbf025.txt"
            };

        // ======== PARÂMETROS BASE ========
        int popSizeBase = 100;
        double mutationRateBase = 0.05;
        int generationsBase = 1000;

        // ======== PREPARA CSV ========
        File dir = new File(RESULTS_DIR);
        if (!dir.exists()) dir.mkdirs();
        String csvFile = RESULTS_DIR + "/GA_SCQBF_results.csv";
        PrintWriter out = new PrintWriter(new FileWriter(csvFile));
        out.println("Instance,Experiment,Generations,Population,MutationRate,BestFitness");

        // ======== LOOP DAS INSTÂNCIAS ========
        for (String filename : instanceFiles) {

            System.out.println("\n==============================");
            System.out.println("Instância: " + filename);
            System.out.println("==============================\n");

            // (1) Padrão
            runExperiment(out, filename, "PADRÃO", generationsBase, popSizeBase, mutationRateBase);

            // (2) +POP
            runExperiment(out, filename, "+POP (x2)", generationsBase, popSizeBase * 2, mutationRateBase);

            // (3) +MUT
            runExperiment(out, filename, "+MUT (x2)", generationsBase, popSizeBase, mutationRateBase * 2);

            // (4) +EVOL1 (x2 gerações)
            runExperiment(out, filename, "+EVOL1 (x2 gerações)", generationsBase * 2, popSizeBase, mutationRateBase);

            // (5) +EVOL2 (x2 pop + x2 gerações)
            runExperiment(out, filename, "+EVOL2 (x2 pop + x2 gerações)", generationsBase * 2, popSizeBase * 2, mutationRateBase);
        }

        out.close();
        System.out.println("\n>>> Todos os experimentos concluídos com sucesso!");
        System.out.println("Resultados salvos em: " + csvFile);
    }

    private static void runExperiment(PrintWriter out, String filename, String expName,
                                      int generations, int popSize, double mutationRate) throws IOException {

        System.out.printf("Rodando experimento %-25s | G=%d, Pop=%d, Mut=%.3f\n",
                expName, generations, popSize, mutationRate);

        long start = System.currentTimeMillis();

        SCQBF problem = new SCQBF(filename);
        GA_SCQBF ga = new GA_SCQBF(problem, generations, popSize, mutationRate);
        Solution<Integer> best = ga.solve();

        long end = System.currentTimeMillis();
        double time = (end - start) / 1000.0;

        System.out.printf("  >> Melhor fitness: %.4f | Tempo: %.2fs\n", best.cost, time);
        System.out.println("  >> Solução: " + best);

        // grava no CSV
        out.printf("%s,%s,%d,%d,%.4f,%.8f\n",
                filename, expName, generations, popSize, mutationRate, best.cost);
        out.flush();
    }
}
