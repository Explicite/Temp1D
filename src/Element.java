/**
 * Author: Jan Paw
 * Date: 04.03.12
 * Time: 20:48
 */
public class Element {
    private double[] weights;
    private double[] firstShapeFunction;
    private double[] secondShapeFunction;
    private double x, y;

    public Element(double x, double y) {
        this.x = x;
        this.y = y;
        weights = new double[2];
        weights[0] = 1;
        weights[1] = 1;
        firstShapeFunction = new double[2];
        firstShapeFunction[0] = 0.5 * (1 + 0.5773502692);
        firstShapeFunction[1] = 0.5 * (1 + 0.5773502692);
        secondShapeFunction = new double[2];
        secondShapeFunction[0] = 0.5 * (1 - 0.5773502692);
        secondShapeFunction[1] = 0.5 * (1 - 0.5773502692);
    }
}
