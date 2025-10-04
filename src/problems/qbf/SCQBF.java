package problems.qbf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * SCQBF — Set Covering Quadratic Binary Function (MAX-SC-QBF)
 *
 * Formato do arquivo:
 *   Linha 1: N                          (nº de elementos/variáveis)
 *   Linha 2: |S1| |S2| ... |SN|         (tamanhos de cada subconjunto)
 *   Linhas 3..(2+N): S_i (lista de elementos 1-based cobertos por i)
 *   Linhas seguintes (N linhas): matriz A triangular superior (linha i tem N-i+1 valores)
 *
 * Avaliação (max):
 *   f(x) = x^T A x  -  lambda * (# elementos não cobertos)
 * Cobertura: um elemento k está coberto se existir i com x_i=1 e k ∈ S_i.
 */
public class SCQBF extends QBF {

    private List<List<Integer>> subsets; // S_i (0-based internamente)
    private int N;                       // nº de variáveis/elementos
    private double lambda;               // penalidade por elemento não coberto

    // Usa lambda padrão (calculado após leitura de A)
    public SCQBF(String filename) throws IOException {
        super(filename);
        this.lambda = computeDefaultLambda();
    }

    // Usa lambda fornecido
    public SCQBF(String filename, double lambda) throws IOException {
        super(filename);
        this.lambda = lambda;
    }

    @Override
    protected Integer readInput(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        // ---- N ----
        N = Integer.parseInt(br.readLine().trim());

        // ---- tamanhos |S_i| (podemos ignorar para parsing robusto) ----
        String[] sizesStr = br.readLine().trim().split("\\s+");
        int[] sizes = new int[sizesStr.length];
        for (int i = 0; i < sizesStr.length; i++) sizes[i] = Integer.parseInt(sizesStr[i]);

        if (sizes.length != N) {
            // Alguns conjuntos vêm com N tamanhos; se não vier, ainda lemos N linhas logo abaixo
            // mas é bom avisar em debug.
            // System.out.println("Aviso: linha de tamanhos tem " + sizes.length + " entradas, N=" + N);
        }

        // ---- S_i ----
        subsets = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            String line = br.readLine();
            if (line == null) line = "";
            String[] toks = line.trim().isEmpty() ? new String[0] : line.trim().split("\\s+");
            List<Integer> set = new ArrayList<>(toks.length);
            for (String t : toks) {
                int elem1Based = Integer.parseInt(t);
                set.add(elem1Based - 1); // guarda 0-based
            }
            subsets.add(set);
        }

        // ---- Matriz A triangular superior ----
        A = new Double[N][N];
        // zera toda a matriz (evita null)
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                A[i][j] = 0.0;

        for (int i = 0; i < N; i++) {
            String line = br.readLine();
            if (line == null) break;
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] vals = line.split("\\s+");
            for (int j = 0; j < vals.length; j++) {
                int col = i + j;
                if (col >= N) break;
                double v = Double.parseDouble(vals[j]);
                A[i][col] = v;
                A[col][i] = v; // espelha para deixar simétrica
            }
        }

        br.close();
        return N; // tamanho do domínio (nº de variáveis x_i)
    }

    @Override
    public Double evaluateQBF() {
        // x^T A x (usa implementação do QBF, agora com A preenchida)
        double quad = super.evaluateQBF();

        // penaliza elementos não cobertos
        int uncovered = countUncovered();
        return quad - lambda * uncovered;
    }

    /** Conta quantos elementos k não estão cobertos por nenhum S_i com x_i=1. */
    private int countUncovered() {
        boolean[] covered = new boolean[N];
        // para cada i selecionado, marca todos k ∈ S_i como cobertos
        for (int i = 0; i < N; i++) {
            if (variables[i] == 1.0) {
                for (int k : subsets.get(i)) covered[k] = true;
            }
        }
        int miss = 0;
        for (boolean c : covered) if (!c) miss++;
        return miss;
    }

    /** Lambda padrão: maior soma absoluta por linha de A, multiplicada por 2. */
    private double computeDefaultLambda() {
        double maxRowAbsSum = 0.0;
        for (int i = 0; i < N; i++) {
            double s = 0.0;
            for (int j = 0; j < N; j++) s += Math.abs(A[i][j]);
            if (s > maxRowAbsSum) maxRowAbsSum = s;
        }
        // multiplicador > 1 para tornar a cobertura mais importante que qualquer melhoria local
        return Math.max(1.0, 2.0 * maxRowAbsSum);
    }

    // getters úteis (opcional)
    public int getN() { return N; }
    public List<List<Integer>> getSubsets() { return subsets; }
    public double getLambda() { return lambda; }
}
