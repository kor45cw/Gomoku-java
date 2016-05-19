import java.util.Scanner;

/**
 * Created by sonchangwoo on 2016. 5. 2..
 */
public class Gomoku3 {
    public static void main(String args[]) {
        Scanner keyboard = new Scanner(System.in);

        String firstInput = keyboard.nextLine();

        int boardSize = 15;

        AI ai = new AI();
        Player player = new Player();
        Board board = new Board(boardSize);
        Rule playerRule = new Rule();

        board.init();
        playerRule.initBoard();
        ai.aiRuleInit();

        int count = 0;
        NextMove playerMove = new NextMove(0,0);

        if (firstInput.contains("START")) {
            while (true) {
//            long startTime = System.currentTimeMillis();
                NextMove aimove;

                if (count==0) {
                    aimove = ai.inputAi();
                    count++;
                } else {
                    aimove = ai.inputAi(playerMove);
                }
//            System.out.println((System.currentTimeMillis() -startTime)/1000);
                System.out.println(aimove.toString());
                board.inputBoard(aimove, -1);
                ai.previousMove = aimove;

                if (board.checkBoard(aimove, -1) == 1) {
                    // Win
//                board.printBoard();
//                System.out.println("BetaGo Win");
                    break;
                } else if (board.checkBoard(aimove, -1) == 2) {
                    // Draw
//                board.printBoard();
//                System.out.println("This game ended in a draw!");
                    break;
                }
                playerMove = player.inputKeyboard();
                board.inputBoard(playerMove, 1);

//            board.printBoard();
//            NextMove playerMove;
//            while(true) {
////                playerMove = player.inputKeyboard();
//                if (!player.checkinput(playerMove)) {
////                    System.out.println("Not available");
//                } else if (playerRule.check33(playerMove, 1) == -1) {
////                    System.out.println("Not available");
//                } else {
//                    board.inputBoard(playerMove, 1);
//                    break;
//                }
//            }


                if (board.checkBoard(playerMove, 1) == 1) {
                    // Win
//                board.printBoard();
//                System.out.println("Player Win");
                    break;
                } else if (board.checkBoard(playerMove, 1) == 2) {
                    // Draw
//                board.printBoard();
//                System.out.println("This game ended in a draw!");
                    break;
                }
            }
        } else {
            while (true) {
                if (count == 0) {
                    playerMove = player.inputKeyboard(firstInput);
                    count++;
                } else {
                    playerMove = player.inputKeyboard();
                }
                board.inputBoard(playerMove, 1);

                if (board.checkBoard(playerMove, 1) == 1) {
                    // Win
//                board.printBoard();
//                System.out.println("Player Win");
                    break;
                } else if (board.checkBoard(playerMove, 1) == 2) {
                    // Draw
//                board.printBoard();
//                System.out.println("This game ended in a draw!");
                    break;
                }


//            long startTime = System.currentTimeMillis();
                NextMove aimove = ai.inputAi(playerMove);
//            System.out.println((System.currentTimeMillis() -startTime)/1000);
                System.out.println(aimove.toString());
                board.inputBoard(aimove, -1);
                ai.previousMove = aimove;

                if (board.checkBoard(aimove, -1) == 1) {
                    // Win
//                board.printBoard();
//                System.out.println("BetaGo Win");
                    break;
                } else if (board.checkBoard(aimove, -1) == 2) {
                    // Draw
//                board.printBoard();
//                System.out.println("This game ended in a draw!");
                    break;
                }
            }

        }

    }
}