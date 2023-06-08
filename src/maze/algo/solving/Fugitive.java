/*  Aiden Donavan
 *  6/7/2023
 *  This class is used for finding an escape path from the maze
 *  entrance to the maze exit. 
 */
package maze.algo.solving;

import maze.model.Cell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import static java.util.Comparator.comparingInt;
import static maze.model.Cell.Type.ESCAPE;

public class Fugitive {

    //Moves in all directions from current cell
    private static final int[][] DELTAS = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};

    //The height of the maze in node
    private int height;

    //The width of the maze in node
    private int width;

    //2-dimensional array of nodes representing a maze
    private Node[][] grid;

    //Start point
    private Node start;

    //End point
    private Node end;

    //Prioritizes nodes that are estimated to take the minimum length to end
    private PriorityQueue<Node> open = new PriorityQueue<>(comparingInt(Node::getFinalCost));

    //Already processed nodes
    private Set<Node> closed = new HashSet<>();

    //Makes a grid of nodes
    public Fugitive(Cell[][] grid, Cell start, Cell end) {
        this.height = grid.length;
        this.width = grid[0].length;
        this.grid = new Node[height][width];
        this.start = new Node(start.getRow(), start.getColumn(), false);
        this.end = new Node(end.getRow(), end.getColumn(), false);
        createNodes(grid);
    }

    //Calculates estimated length to end from each node
    private void createNodes(Cell[][] grid) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                var node = new Node(i, j, grid[i][j].isWall());
                node.calcHeuristicTo(end);
                this.grid[i][j] = node;
            }
        }
    }

    //Uses A* algorithm to find path
    public List<Cell> findEscape() {
        open.add(start);
        while (!open.isEmpty()) {
            var cur = open.poll();
            if (isEnd(cur))
                return reconstructPath(cur);
            closed.add(cur);
            updateNeighbors(cur);
        }
        return new ArrayList<>();
    }

    //Check if the end is a node
    private boolean isEnd(Node currentNode) {
        return currentNode.equals(end);
    }

    //Reconstructs path from given node (current node)
    private List<Cell> reconstructPath(Node cur) {
        var path = new LinkedList<Cell>();
        path.add(toCell(cur));
        while (cur.getParent() != cur) {
            var parent = cur.getParent();
            path.addFirst(toCell(parent));
            cur = parent;
        }
        return path;
    }

    //Converts node back to cell
    private Cell toCell(Node node) {
        return new Cell(node.getRow(), node.getColumn(), ESCAPE);
    }

    //Updates final length from neighboring nodes
    private void updateNeighbors(Node cur) {
        for (var delta : DELTAS) {
            var row = cur.getRow() + delta[0];
            var column = cur.getColumn() + delta[1];
            if (inBounds(row, column)) {
                var node = grid[row][column];
                if (!node.isWall() && !closed.contains(node)) {
                    if (open.contains(node)) {
                        if (node.hasBetterPath(cur)) {
                            open.remove(node);
                        } else {
                            continue;
                        }
                    }
                    node.updatePath(cur);
                    open.add(node);
                }
            }
        }
    }

    //Checks if cells are in bounds of array
    private boolean inBounds(int row, int column) {
        return row >= 0 && row < height
            && column >= 0 && column < width;
    }
}

