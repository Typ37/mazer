/*  
 *  Aiden Donavan
 *  6/7/2023
 *  This class encapsulates the internal representation of the maze and provides
 *  methods for creating, managing and extracting information about it.
 */
package maze.model;

import maze.algo.generation.PassageTree;
import maze.algo.solving.Fugitive;

import java.util.function.Consumer;

import static java.lang.Integer.parseInt;
import static maze.model.Cell.Type.PASSAGE;
import static maze.model.Cell.Type.WALL;


public class Maze {

    //Heigh of maze in cells
    private final int height;

    //Width of maze in cells
    private final int width;

    //2-dimensional array of cells representing maze
    private final Cell[][] grid;

    //Prevents recalculation by determining if the solving method was already used
    private boolean isSolved = false;

    //Generates a new maze given height and width
    public Maze(int height, int width) {
        if (height < 3 || width < 3) {
            throw new IllegalArgumentException(
                "Both the height and the width " +
                    "of the maze must be at least 3");
        }
        this.height = height;
        this.width = width;
        grid = new Cell[height][width];
        fillGrid();
    }

    //Generates a new maze if it's a square
    public Maze(int size) {
        this(size, size);
    }

    //Fills the maze with connections so there arent like floating walls
    private void fillGrid() {
        fillAlternately();
        fillGaps();
        makeEntranceAndExit();
        generatePassages();
    }

    //Creates a new cell with given coordinates
    private void putCell(int row, int column, Cell.Type type) {
        grid[row][column] = new Cell(row, column, type);
    }

    //Fills every second cell with a passage, and the other with a wall
    private void fillAlternately() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((i & 1) == 0 || (j & 1) == 0) {
                    putCell(i, j, WALL);
                } else {
                    putCell(i, j, PASSAGE);
                }
            }
        }
    }

    /*
     * If the maze has an even height or width it is needed to fill the
     * last row or column of the grid with a wall 
     */
    private void fillGaps() {
        if (height % 2 == 0) wallLastRow();
        if (width % 2 == 0) wallLastColumn();
    }

    //Fills the last column in the grid with a wall
    private void wallLastColumn() {
        for (int i = 0; i < height; i++)
            putCell(i, width - 1, WALL);
    }

    //Fills the last row in the grid with a wall
    private void wallLastRow() {
        for (int i = 0; i < width; i++)
            putCell(height - 1, i, WALL);
    }

    /*
     * Calculates the index of the passage in the last row. For a maze
     * with an odd (1) and even (2) width its indices differ
     */
    private int getExitColumn() {
        return width - 3 + width % 2;
    }

    /*
     * Puts entrance and exit passages to upper left and lower right
     * corners
     */
    private void makeEntranceAndExit() {
        putCell(0, 1, PASSAGE);
        putCell(height - 1, getExitColumn(), PASSAGE);
        if (height % 2 == 0)
            putCell(height - 2, getExitColumn(), PASSAGE);
    }

    /*
     * Creates random passages between isolated passage cells such
     * that every cell is connected to the other in one way and
     * has no cycles
     */
    private void generatePassages() {
        new PassageTree(height, width)
            .generate()
            .forEach(putCell());
    }

    //Puts the cell in a corresponding place in the grid
    private Consumer<Cell> putCell() {
        return cell -> grid[cell.getRow()][cell.getColumn()] = cell;
    }

    //Finds a path in the maze from its entrance to its exit
    public String findEscape() {
        if (!isSolved) {
            new Fugitive(grid, getEntrance(), getExit())
                .findEscape()
                .forEach(putCell());
            isSolved = true;
        }
        return toString(true);
    }

    //Return the entrance cell
    private Cell getEntrance() {
        return grid[0][1];
    }

    //Return the exit cell
    private Cell getExit() {
        return grid[height - 1][getExitColumn()];
    }

    /*
     * Return the string representation of the grid. The path
     * from the entrance to the exit can be displayed if it
     * is already found and showEscape is true
     */
    private String toString(boolean showEscape) {
        var sb = new StringBuilder();
        for (var row : grid) {
            for (var cell : row) {
                if (cell.isWall()) {
                    sb.append("██");
                } else if (showEscape && cell.isEscape()) {
                    sb.append("▓▓");
                } else {
                    sb.append("  ");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    //Return the string representation of the grid
    @Override
    public String toString() {
        return toString(false);
    }

    /*
     * Parses a serialized maze representation and
     * constructs a new maze from it
     */
    public static Maze load(String str) {
        try {
            var whole = str.split("\n");
            var size = whole[0].split(" ");
            var height = parseInt(size[0]);
            var width = parseInt(size[1]);
            var grid = new Cell[height][width];
            for (int i = 0; i < height; i++) {
                var row = whole[i + 1].split(" ");
                for (int j = 0; j < width; j++)
                    grid[i][j] = new Cell(
                        i, j, intToType(parseInt(row[j]))
                    );
            }
            return new Maze(height, width, grid);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Cannot load the maze. " +
                    "It has an invalid format"
            );
        }
    }

    //Creates a maze instance with given height, width and grid
    private Maze(int height, int width, Cell[][] grid) {
        this.height = height;
        this.width = width;
        this.grid = grid;
    }

    //Converts 1 to the WALL and 0 to the PASSAGE
    private static Cell.Type intToType(int val) {
        return val == 1 ? WALL : PASSAGE;
    }

    //Converts the maze to the serialized form
    public String export() {
        var sb = new StringBuilder();
        sb.append(height).append(' ')
          .append(width).append('\n');
        for (var row : grid) {
            for (var cell : row)
                sb.append(typeToInt(cell))
                  .append(' ');
            sb.append('\n');
        }
        return sb.toString();
    }

    //Converts WALL to the 1 and PASSAGE to the 0
    private int typeToInt(Cell cell) {
        return cell.isWall() ? 1 : 0;
    }
}
