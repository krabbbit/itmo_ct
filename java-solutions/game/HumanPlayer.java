

import java.io.PrintStream;
import java.util.Scanner;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class HumanPlayer implements Player {
    private final PrintStream out;
    private final Scanner in;

    public HumanPlayer(final PrintStream out, final Scanner in) {
        this.out = out;
        this.in = in;
    }

    public HumanPlayer() {
        this(System.out, new Scanner(System.in));
    }

    @Override
    public Move move(final Position position, final Cell cell) {
        while (true) {
            out.println("Position");
            out.println(position);
            out.println(cell + "'s move");
            out.println("Enter row and column");
            int row = -1, col = -1;
            if (in.hasNextInt()) {
                row = in.nextInt();
                if (in.hasNextInt()) {
                    col = in.nextInt();
                }
            } else {
                in.nextLine();
            }
            if (row >= 0 && col >= 0) {
                final Move move = new Move(row, col, cell);
                return move;
            }
            out.println("your move was wrong, try again");
            //return new Move(-1, -1, Cell.ERROR);
//            final int row = move.getRow();
//            final int column = move.getColumn();
//            out.println("Move " + move + " is invalid");
        }
    }
}
