

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class Game {
    private final boolean log;
    private final Player player1, player2;
    private ArrayList<Integer> turnir = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> res = new ArrayList<>();
    private ArrayList<Integer> temp = new ArrayList<>();
    private ArrayList<Integer> vyb = new ArrayList<>();
    private int count = 2;
    private int thisPlayers = 1;

    public Game(final boolean log, final Player player1, final Player player2) {
        this.log = log;
        this.player1 = player1;
        this.player2 = player2;
    }

    public Game(final boolean log, int count) {
        this.log = log;
        this.player1 = new HumanPlayer();
        this.player2 = new HumanPlayer();
        this.count = count;
        for (int i = 1; i <= count; i++) {
            turnir.add(i);
        }
        Collections.shuffle(turnir);
        temp = new ArrayList<>(turnir);
    }
    private boolean endOfStep () {
        if (thisPlayers >= turnir.size()) {
            turnir = temp;
            thisPlayers = 1;
        }
        res.add(new ArrayList<>(vyb));
        vyb.clear();
        if (this.turnir.size() == 1) {
            System.out.println("Turnir table:");
            System.out.println(turnir.get(0));
            for (int i = res.size() - 1; i >= 0; i--) {
                for (int u: res.get(i)) {
                    System.out.print(u + " ");
                }
                System.out.println();
            }
            return false;
        }
        return true;
    }
    public boolean play(Board board, boolean tur) {
        if (tur) {
            for (int i = thisPlayers; i < turnir.size(); i+=2) {
                final int playerNumber1 = turnir.get(i - 1);
                final int playerNumber2 = turnir.get(i);
                System.out.println("Players number: " + playerNumber1 + " and " + playerNumber2);
                while (true) {
                    int result1 = move(board, player1, playerNumber1);
                    while (result1 == -2) {
                        System.out.println("You have a bonus!");
                        result1 = move(board, player1, playerNumber1);
                    }
                    if (result1 != -1) {
                        if (result1 != 0) {
                            if (result1 == playerNumber1) {
                                vyb.add(playerNumber2);
                                temp.remove((Integer) playerNumber2);
                            } else {
                                vyb.add(playerNumber1);
                                temp.remove((Integer) playerNumber1);
                            }
                        }
                        System.out.println("Game result: " + result1);
                        thisPlayers = i + 2;
                        return endOfStep();
                    }
                    int result2 = move(board, player2, playerNumber2);
                    while (result2 == -2) {
                        System.out.println("You have a bonus!");
                        result2 = move(board, player2, playerNumber2);
                    }
                    if (result2 != -1) {
                        if (result2 != 0) {
                            if (result2 == playerNumber2) {
                                vyb.add(playerNumber1);
                                temp.remove((Integer) playerNumber1);
                            } else {
                                vyb.add(playerNumber2);
                                temp.remove((Integer) playerNumber2);
                            }
                        }
                        System.out.println("Game result: " + result2);
                        thisPlayers = i + 2;
                        return endOfStep();
                        //return true;
                        //return result2;
                    }
                }
            }
        } else {
            while (true) {
                final int  result1 = move(board, player1, 1);
                if (result1 != -1) {
                    System.out.println("Game result: " + result1);
                    return true;
                    //return result1;
                } else if (result1 == 0) {
                    System.out.println("Game result: " + result1);
                    return false;
                }
                final int result2 = move(board, player2, 2);
                if (result2 != -1) {
                    System.out.println("Game result: " + result2);
                    return true;
                    //return result1;
                } else if (result2 == 0) {
                    System.out.println("Game result: " + result2);
                    return false;
                }
            }
        }
        return false;
    }

    private int move(final Board board, final Player player, final int no) {
        final Move move = player.move(board.getPosition(), board.getCell());
        final Result result = board.makeMove(move);
        log("Player " + no + " move: " + move);
        log("Position:\n" + board);
        if (result == Result.WIN) {
            log("Player " + no + " won");
            return no;
        } else if (result == Result.LOSE) {
            log("Player " + no + " lose");
            return this.count + 1 - no;
        } else if (result == Result.DRAW) {
            log("Draw");
            return 0;
        } else if (result == Result.BONUS){
            return -2;
        } else {
            return -1;
        }
    }

    private void log(final String message) {
        if (log) {
            System.out.println(message);
        }
    }
}
