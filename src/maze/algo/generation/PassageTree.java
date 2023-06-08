/*  Aiden Donavan
 *  6/7/2023
 *  This class is used for creating random passages between
 *  isolated passage cells such that every cell is connected
 */
package maze.algo.generation;

import maze.model.Cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static maze.model.Cell.Type.PASSAGE;

public class PassageTree {

    //Height of the maze
    private int height;

    //Width of the maze
    private int width;

    //Creates a new edge
    public PassageTree(int height, int width) {
        this.height = (height - 1) / 2;
        this.width = (width - 1) / 2;
    }

    /*
     * Generates a random list of cells that connect passages in
     * an original form such that a maze is simply connected.
     */
    public List<Cell> generate() {
        var edges = createEdges();
        Collections.shuffle(edges);
        var tree = buildRandomSpanningTree(edges);
        return createPassages(tree);
    }

    //Creates a list of all possible edges
    private List<Edge> createEdges() {
        var edges = new ArrayList<Edge>();
        for (int column = 1; column < width; column++) {
            edges.add(new Edge(toIndex(0, column),
                               toIndex(0, column - 1)));
        }
        for (int row = 1; row < height; row++) {
            edges.add(new Edge(toIndex(row, 0),
                               toIndex(row - 1, 0)));
        }
        for (int row = 1; row < height; row++) {
            for (int column = 1; column < width; column++) {
                edges.add(new Edge(toIndex(row, column),
                                   toIndex(row, column - 1)));
                edges.add(new Edge(toIndex(row, column),
                                   toIndex(row - 1, column)));
            }
        }
        return edges;
    }

    
    //Transforms the coordinates in a 2-dimensional array into a 1-dimensional array
    private int toIndex(int row, int column) {
        return row * width + column;
    }

    //Generates a list of edges that connect passages. It is a
    private List<Edge> buildRandomSpanningTree(List<Edge> edges) {
        var disjointSets = new DisjointSet(width * height);
        return edges
            .stream()
            .filter(edge -> connects(edge, disjointSets))
            .collect(toList());
    }

    //Checks if an edge connects 2 disjoint subsets
    private boolean connects(Edge edge, DisjointSet disjointSet) {
        return disjointSet.union(edge.getFirstCell(), edge.getSecondCell());
    }

    /*
     * Scales and converts edges in an imaginary edge form to the cells
     * which connect passages in a original form.
     */
    private List<Cell> createPassages(List<Edge> spanningTree) {
        return spanningTree
            .stream()
            .map(edge -> {
                var first = fromIndex(edge.getFirstCell());
                var second = fromIndex(edge.getSecondCell());
                return getPassage(first, second);
            }).collect(toList());
    }

    
    //Transforms the coordinate in a 1-dimensional array back to a 2-dimensional array
    private Cell fromIndex(int index) {
        var row = index / width;
        var column = index % width;
        return new Cell(row, column, PASSAGE);
    }

    //Scales, transforms, and finalizes cell passages
    private Cell getPassage(Cell first, Cell second) {
        var row = first.getRow() + second.getRow() + 1;
        var column = first.getColumn() + second.getColumn() + 1;
        return new Cell(row, column, PASSAGE);
    }
}
