package maze.algo.generation;

/*  Aiden Donavan
 *  6/7/2023
 * This class stores an information about the particular edge in a passage tree.
 */
class Edge {

     //The coordinate of the first cell.
    private final int firstCell;

    //The coordinate of the second cell.
    private final int secondCell;

    //Creates a new edge with given cell coordinates.
    Edge(int firstCell, int secondCell) {
        this.firstCell = firstCell;
        this.secondCell = secondCell;
    }

    //Return the first cell coordinates
    int getFirstCell() {
        return firstCell;
    }

    //Return the second cell coordinates
    int getSecondCell() {
        return secondCell;
    }
}
