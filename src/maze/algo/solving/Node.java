package maze.algo.solving;

import java.util.Objects;

/*  Aiden Donavan
 *  6/7/2023
 *  This class stores an information about the particular node in a node grid.
 */
class Node {

    //cost of moving to a neighboring node
    private static final int EDGE_COST = 1;

    //vertical coordinate of current node
    private final int row;

    //horizontal coordinate of current node
    private final int column;

    //Indicates if current node is a wall
    private final boolean isWall;

    //Parent nodes are used to reconstruct paths from certain points
    private Node parent;

    //Determines the cost of the path from start node to current node
    private int g;

    //Estimates cost of path from current node to end
    private int h;

    //Final cost of start to end if it goes through this node
    private int f;

    //Creates a new node with given row and column and sets its parent to itself.
    Node(int row, int column, boolean isWall) {
        this.row = row;
        this.column = column;
        this.isWall = isWall;
        parent = this;
    }

    int getRow() {
        return row;
    }

    int getColumn() {
        return column;
    }

    boolean isWall() {
        return isWall;
    }

    Node getParent() {
        return parent;
    }

    int getFinalCost() {
        return f;
    }

    /*
     * Calculates the estimated cost of the path from this node to the
     * end node
     */
    void calcHeuristicTo(Node node) {
        this.h = Math.abs(node.row - this.row)
            + Math.abs(node.column - this.column);
    }

    //Checks if path through new node is cheaper than current node
    boolean hasBetterPath(Node node) {
        return node.g + EDGE_COST < this.g;
    }

    /*
     * Updates the path such that the given node becomes the
     * new parent and recalculates the final cost through it
     */
    void updatePath(Node node) {
        this.parent = node;
        this.g = node.g + EDGE_COST;
        f = g + h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var node = (Node) o;
        return row == node.row &&
            column == node.column &&
            isWall == node.isWall;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column, isWall);
    }
}