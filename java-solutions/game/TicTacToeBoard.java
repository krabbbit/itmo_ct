

import java.util.Arrays;
import java.util.Map;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class TicTacToeBoard implements Board, Position {
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.'
    );

    private final Cell[][] cells;
    private Cell turn;
    private int k;

    public TicTacToeBoard(int rows, int cols, int k) {
        this.cells = new Cell[rows][cols];
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
        this.k = k;
    }

    @Override
    public Position getPosition() {
        return this;
    }

    @Override
    public Cell getCell() {
        return turn;
    }

    @Override
    public Result makeMove(final Move move) {
        if (move.getValue() == Cell.ERROR) {
            return Result.UNKNOWN;
        }
        if (!isValid(move)) {
            return Result.LOSE;
        }

        cells[move.getRow()][move.getColumn()] = move.getValue();

        int inDiag1 = 0;
        int inDiag2 = 0;
        int empty = 0;
        for (int u = 0; u < cells.length; u++) {
            int inRow = 0;
            int inColumn = 0;
            for (int v = 0; v < cells[u].length; v++) {
                if (cells[u][v] == turn) {
                    inRow++;
                }
                if (v < cells.length && u < cells[u].length && cells[v][u] == turn) {
                    inColumn++;
                }
                if (cells[u][v] == Cell.E) {
                    empty++;
                }
            }
            if (inRow == k || inColumn == k) {
                return Result.WIN;
            }
            if (u < cells[u].length && cells[u][u] == turn) {
                inDiag1++;
            }
            if ((cells.length - u + 1) < cells[u].length && cells[u][cells.length - u + 1] == turn) {
                inDiag2++;
            }
        }
        if (inDiag1 == k || inDiag2 == k) {
            return Result.WIN;
        }
        if (empty == 0) {
            return Result.DRAW;
        }
        int vert = 1;
        for (int i = 1; i < cells.length; i++) {
            if ((move.getRow() + i) < cells.length && cells[move.getRow() + i][move.getColumn()] == move.getValue()) {
                vert++;
            } else {
                break;
            }
        }
        for (int i = 1; i < cells.length; i++) {
            if ((move.getRow() - i) >= 0 && cells[move.getRow() - i][move.getColumn()] == move.getValue()) {
                vert++;
            } else {
                break;
            }
        }

        int gor = 1;
        for (int i = 1; i < cells[0].length; i++) {
            if ((move.getColumn() + i) < cells[0].length && cells[move.getRow()][move.getColumn() + i] == move.getValue()) {
                gor++;
            } else {
                break;
            }
        }
        for (int i = 1; i < cells[0].length; i++) {
            if ((move.getColumn() - i) >= 0 && cells[move.getRow()][move.getColumn() - i] == move.getValue()) {
                gor++;
            } else {
                break;
            }
        }

        int d1 = 1, d2 = 1;
        for (int i = 1; i < Math.max(cells.length, cells[0].length); i++) {
            if ((move.getRow() - i) >= 0 && (move.getColumn() - i) >= 0 && cells[move.getRow() - i][move.getColumn() - i] == move.getValue()) {
                d1++;
            } else {
                break;
            }
        }
        for (int i = 1; i < Math.max(cells.length, cells[0].length); i++) {
            if ((move.getRow() + i) < cells.length && (move.getColumn() + i) < cells[0].length && cells[move.getRow() + i][move.getColumn() + i] == move.getValue()) {
                d1++;
            } else {
                break;
            }
        }
        for (int i = 1; i < Math.max(cells.length, cells[0].length); i++) {
            if ((move.getRow() - i) >= 0 && (move.getColumn() + i) < cells[0].length && cells[move.getRow() - i][move.getColumn() + i] == move.getValue()) {
                d2++;
            } else {
                break;
            }
        }
        for (int i = 1; i < Math.max(cells.length, cells[0].length); i++) {
            if ((move.getRow() + i) < cells.length && (move.getColumn() - i) >= 0 && cells[move.getRow() + i][move.getColumn() - i] == move.getValue()) {
                d2++;
            } else {
                break;
            }
        }

        //сделать диагонали
        if (vert >= 4 || gor >= 4 || d1 >= 4 || d2 >= 4) {
            return Result.BONUS;
        }

        turn = turn == Cell.X ? Cell.O : Cell.X;
        return Result.UNKNOWN;
    }

    @Override
    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < cells.length
                && 0 <= move.getColumn() && move.getColumn() < cells[0].length
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == getCell();
    }

    @Override
    public Cell getCell(final int r, final int c) {
        return cells[r][c];
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(" ");
        for (int i = 0; i < cells[0].length; i++) {
            sb.append(Integer.toString(i));
        }
        for (int r = 0; r < cells.length; r++) {
            sb.append("\n");
            sb.append(r);
            for (int c = 0; c < cells[r].length; c++) {
                sb.append(SYMBOLS.get(cells[r][c]));
            }
        }
        return sb.toString();
    }
}
