

import java.util.Scanner;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the number of rows, columns and K:");
        int rows = sc.nextInt();
        int cols = sc.nextInt();
        int k = sc.nextInt();
        System.out.println("Do you want play turnir? (yes/no):");
        if (sc.next().equals("yes")) {
            System.out.println("Please enter the number of players:");
            int count = sc.nextInt();
            Game game = new Game(false, count);
            //обработать count < 1  и =1
            while (count < 1) {
                System.out.println("Please enter correct count of players!");
                count = sc.nextInt();
            }
            if (count == 1) {
                game = new Game(false, new HumanPlayer(), new RandomPlayer(rows, cols));
                //обработать count < 1  и =1
                boolean r;
                do {
                    r = game.play(new TicTacToeBoard(rows, cols, k), false);
                } while (r);
            }
            boolean r;
            do {
                r = game.play(new TicTacToeBoard(rows, cols, k), true);
            } while (r);
        } else {
            final Game game = new Game(false, new HumanPlayer(), new HumanPlayer());
            //обработать count < 1  и =1
            boolean r;
            do {
                r = game.play(new TicTacToeBoard(rows, cols, k), false);
            } while (r);
        }
        //обработать count < 1  и =1
    }
}
