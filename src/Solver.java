/**
 * Author: Jan Paw
 * Date: 07.01.12
 * Time: 17:52
 */
import Jama.Matrix;

/**
 * Klasa rozwiązująca typowe układy równań w zadaniach MES. Posiada dwa przeładowane konstruktory. Pierwszy znich
 * przyjmuje wektor obciążeń oraz macierz sztywności. Kolejny pozwala na podawanie jako agumenty cztery macierze.
 * @author Jan Paw
 */
class Solver {
    private double[] resultsVector, loadsVector;
    private double[][] stiffnessMatrix;

    /**
     * @param loadsVector -> wektor obciążeń double[n]
     * @param stiffnessMatrix -> macierz sztywności double[n][n]
     */
    public Solver(double[] loadsVector, double[][] stiffnessMatrix) {
        this.loadsVector = loadsVector;
        this.stiffnessMatrix = stiffnessMatrix;
        resultsVector = new double[loadsVector.length];
    }

    /**
     * @param aB -> wektor obciążeń
     * @param aC -> naddiagonalna macierzy sztywności
     * @param aD -> diagonalna macierzy sztywności
     * @param aE -> poddiagonalna macierzy sztywności
     */
    public Solver(double[] aB, double[] aC, double[] aD, double[] aE) {
        stiffnessMatrix = new double[aB.length][aB.length];
        loadsVector = new double[aB.length];
        resultsVector = new double[aB.length];
        for (int i = 0; i < aB.length; i++) {
            stiffnessMatrix[i][i] = aD[i];
            loadsVector[i] = aB[i];
        }
        for (int i = 0; i < aB.length - 1; i++) {
            stiffnessMatrix[i + 1][i] = aC[i + 1];
            stiffnessMatrix[i][i + 1] = aE[i];
        }
    }

    /**
     * @return -> wektor rozwiązań dla podanego problemu
     */
    public double[] solve() {
        Matrix mainMatrix = new Matrix(stiffnessMatrix);
        Matrix resultsMatrix = mainMatrix.solve(new Matrix(loadsVector, loadsVector.length));
        resultsVector = resultsMatrix.getColumnPackedCopy();
        return resultsVector;
    }
}
