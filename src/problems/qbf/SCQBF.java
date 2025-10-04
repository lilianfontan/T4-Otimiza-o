package problems.qbf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Classe SCQBF — implementação do Set Covering Quadratic Binary Function (MAX-SC-QBF)
 * baseada no modelo:
 * 
 * max Σ Σ a_ij * y_ij
 * 
 * com restrições lineares de cobertura e linearização do produto binário.
 */
public class SCQBF extends QBF {

    private ArrayList<ArrayList<Integer>> subsets; // subconjuntos S_i
    private int numElements;                       // número de elementos do universo
    private double lambda;                         // parâmetro de penalização

    public SCQBF(String filename) throws IOException {
        super(filename);
    }

    public SCQBF(String filename, double lambda) throws IOException {
        super(filename);
        this.lambda = lambda;
    }

    @Override
    protected Integer readInput(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        // ======= Leitura do número de subconjuntos =======
        int n = Integer.parseInt(br.readLine().trim()); // número de subconjuntos
        subsets = new ArrayList<>();
        numElements = 0;

        // ======= Leitura da linha de pesos (λ_i) =======
        String[] weights = br.readLine().trim().split("\\s+");
        Double[] vector = new Double[n];
        for (int i = 0; i < n; i++) {
            vector[i] = Double.parseDouble(weights[i]);
        }

        // ======= Leitura dos subconjuntos S_i =======
        for (int i = 0; i < n; i++) {
            String line = br.readLine();
            String[] parts = line.trim().split("\\s+");
            ArrayList<Integer> set = new ArrayList<>();
            for (String p : parts) {
                int val = Integer.parseInt(p);
                set.add(val - 1); // converte para índice 0-based
                numElements = Math.max(numElements, val);
            }
            subsets.add(set);
        }

        // ======= Leitura da matriz A (triangular superior) =======
        A = new Double[n][n];

        // Inicializa a matriz com zeros
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = 0.0;
            }
        }

        // Lê as linhas da matriz A
        for (int i = 0; i < n; i++) {
            String line = br.readLine();
            if (line == null || line.trim().isEmpty()) break;

            String[] values = line.trim().split("\\s+");
            for (int j = 0; j < values.length; j++) {
                double val = Double.parseDouble(values[j]);
                int col = i + j;
                if (col < n) {
                    A[i][col] = val;
                    A[col][i] = val; // mantém simetria
                }
            }
        }

        br.close();
        return n;
    }

    @Override
    public Double evaluateQBF() {
        // mesma função objetivo da QBF, com penalização opcional por lambda
        double result = 0.0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result += variables[i] * A[i][j] * variables[j];
            }
        }
        // se lambda > 0, adiciona penalização proporcional
        result -= lambda * Math.abs(result);
        return result;
    }

    public int getNumElements() {
        return numElements;
    }

    public ArrayList<ArrayList<Integer>> getSubsets() {
        return subsets;
    }

    public double getLambda() {
        return lambda;
    }
}
