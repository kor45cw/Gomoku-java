/**
 * Created by sonchangwoo on 2016. 5. 2..
 */
public class Board {
    static int boardSize;
    static int board[][];

    public Board(int boardSize) {
        Board.boardSize = boardSize;
        board = new int[boardSize][boardSize];
    }

    void init() {
        for (int i=0; i< boardSize ;i++) {
            for (int j=0; j< boardSize; j++) {
                board[i][j] = 0;
            }
        }
    }

    void printBoard() {
        System.out.println("   ABCDEFGHIJKLMNO");
        for (int i=boardSize-1; i>= 0; i--) {
            if (i < 9) {
                System.out.print((i+1) + "  ");
            } else {
                System.out.print((i+1) + " ");
            }
            for (int j=0; j<boardSize; j++) {
                if (board[i][j] == 0) {
                    System.out.print("-");
                } else if (board[i][j] == 1) {
                    System.out.print("○");
                } else {
                    System.out.print("●");
                }
            }
            System.out.print(" "+(i+1));
            System.out.println();
        }
        System.out.println("   ABCDEFGHIJKLMNO");

    }

    void inputBoard(NextMove move, int playerOrAi) {
        board[move.getMoveRow()][move.getMoveCol()] = playerOrAi;
    }

    int checkBoard(NextMove move, int playerOrAi) {
        int count = 0;

        count= winCheckLeftRight(move, -1, -1, playerOrAi) + winCheckLeftRight(move, 1, boardSize, playerOrAi) - 1;
        if(count == 5) {
            return 1;
        }

        count = winCheckUpDown(move, -1, -1, playerOrAi) + winCheckUpDown(move, 1, boardSize, playerOrAi) - 1;
        if(count == 5) {
            return 1;
        }

        count = winCheckDiag(move, -1, -1, -1, -1, playerOrAi) + winCheckDiag(move, 1, 1, boardSize, boardSize, playerOrAi) - 1;
        if(count == 5) {
            return 1;
        }

        count = winCheckDiag(move, 1, -1, boardSize, -1, playerOrAi) + winCheckDiag(move, -1, 1, -1, boardSize, playerOrAi) - 1;
        if(count == 5) {
            return 1;
        }

        for (int i=0; i < boardSize ;i++) {
            for (int j =0; j<boardSize;j++) {
                if (board[i][j] == 0) {
                    return 0;
                }
            }
        }

        return 2;
    }

    private int winCheckLeftRight(NextMove pos, int offsetX, int maxX, int playerOrAi) {
        if(pos.getMoveRow() == maxX || board[pos.getMoveRow()][pos.getMoveCol()] != playerOrAi) {
            return 0;
        } else {
            NextMove nv = new NextMove(pos.getMoveRow() + offsetX, pos.getMoveCol());
            return 1 + winCheckLeftRight(nv, offsetX, maxX, playerOrAi);
        }
    }

    private int winCheckUpDown(NextMove pos, int offsetY, int maxY, int playerOrAi) {
        if(pos.getMoveCol() == maxY || board[pos.getMoveRow()][pos.getMoveCol()] != playerOrAi) {
            return 0;
        } else {
            NextMove nv = new NextMove(pos.getMoveRow(), pos.getMoveCol() + offsetY);
            return 1 + winCheckUpDown(nv, offsetY, maxY, playerOrAi);
        }
    }

    private int winCheckDiag(NextMove pos, int offsetX, int offsetY, int maxX, int maxY, int playerOrAi) {
        if(pos.getMoveRow() == maxX || pos.getMoveCol() == maxY || board[pos.getMoveRow()][pos.getMoveCol()] != playerOrAi) {
            return 0;
        } else {
            NextMove nv = new NextMove(pos.getMoveRow() + offsetX, pos.getMoveCol() + offsetY);
            return 1 + winCheckDiag(nv, offsetX, offsetY, maxX, maxY, playerOrAi);
        }
    }
}
