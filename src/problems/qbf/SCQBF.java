package problems.qbf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import solutions.Solution;

public class SCQBF extends QBF {

    // Para cada elemento k, a lista de subconjuntos i que o cobrem
    private List<List<Integer>> coversOfK;
    private double lambda; // penalização

    public SCQBF(String fileMatrixA, String fileCoverageS, double lambda) throws IOException {
        super(fileMatrixA);
        this.lambda = lambda;
        readCoverage(fileCoverageS);
    }

    // Lê o arquivo de cobertura S_i (cada linha: "k i1 i2 i3 ...")
    private void readCoverage(String filename) throws IOException {
        coversOfK = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            List<Integer> covers = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                covers.add(Integer.parseInt(parts[i]));
            }
            coversOfK.add(covers);
        }
        br.close();
    }

    @Override
    public Double evaluate(Solution<Integer> sol) {
        setVariables(sol);
        double q = evaluateQBF();
        int violations = 0;

        for (int k = 0; k < coversOfK.size(); k++) {
            int covered = 0;
            for (int i : coversOfK.get(k)) {
                if (variables[i] == 1.0)
                    covered++;
            }
            if (covered < 1) // não coberto
                violations += 1;
        }

        double penalized = q - lambda * violations;
        return sol.cost = penalized;
    }

    public static void main(String[] args) throws IOException {
        // Exemplo de uso
        SCQBF scqbf = new SCQBF("instances/qbf/qbf100", "instances/scqbf/coverage100", 1000.0);
        System.out.println("Tamanho domínio = " + scqbf.getDomainSize());
    }
}
