/*  Aiden Donavan
 *  6/7/2023
 *  This class stores an information about the particular cell in a grid.
 */
package maze.model;

import java.util.Objects;

public class Cell {

    //Cells are either a passage, wall, or part of the escape path
    public enum Type {
        PASSAGE,
        WALL,
        ESCAPE;
    }

    //Vertical coordinates of this cell in a grid
    private final int row;

    //Horizontal coordinates of this cell in a grid
    private final int column;

    //Type of cell (3 options stated above)
    private final Type type;

    public Cell(int row, int column, Type type) {
        this.row = row;
        this.column = column;
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isPassage() {
        return type == Type.PASSAGE;
    }

    public boolean isWall() {
        return type == Type.WALL;
    }

    public boolean isEscape() {
        return type == Type.ESCAPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var cell = (Cell) o;
        return row == cell.row &&
            column == cell.column &&
            type == cell.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column, type);
    }

    @Override
    public String toString() {
        return "Cell{" +
            "row=" + row +
            ", column=" + column +
            ", type=" + type +
            '}';
    }
}
