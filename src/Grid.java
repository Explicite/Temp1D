/**
 * Author: Jan Paw
 * Date: 04.03.12
 * Time: 20:43
 */
public class Grid {
    private Element[] grid;
    private int numberOfNodes;
    public Grid(int numberOfNodes) {
        grid = new Element[numberOfNodes];
        this.numberOfNodes = numberOfNodes;
    }

    public void generate(){

    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }
}
