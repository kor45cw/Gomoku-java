import java.util.Scanner;

/**
 * Created by sonchangwoo on 2016. 5. 2..
 */
public class Player {
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    NextMove inputKeyboard() {
        Scanner keyboard = new Scanner(System.in);

//        System.out.print("Please input position of white stone: ");
        String input = keyboard.nextLine();

        int x; // 숫자
        int y; // 문자
        if (input.length() == 2) {
            y = alphabet.indexOf(input.charAt(0));
            x = Character.getNumericValue(input.charAt(1))-1;
        } else {
            y = alphabet.indexOf(input.charAt(0));
            x = 10 + Character.getNumericValue(input.charAt(2))-1;
        }
        NextMove move = new NextMove(x, y);

        return move;
    }

    NextMove inputKeyboard(String input) {
//        Scanner keyboard = new Scanner(System.in);

//        System.out.print("Please input position of white stone: ");
//        String input = keyboard.nextLine();

        int x; // 숫자
        int y; // 문자
        if (input.length() == 2) {
            y = alphabet.indexOf(input.charAt(0));
            x = Character.getNumericValue(input.charAt(1))-1;
        } else {
            y = alphabet.indexOf(input.charAt(0));
            x = 10 + Character.getNumericValue(input.charAt(2))-1;
        }
        NextMove move = new NextMove(x, y);

        return move;
    }

    public boolean checkinput(NextMove nextMove) {
        int x = nextMove.getMoveRow();
        int y = nextMove.getMoveCol();
        if (x <0 || x>14 || y<0 || y>14) {
            return false;
        }

        if (!(Board.board[nextMove.getMoveRow()][nextMove.getMoveCol()] == 0)) {
            return false;
        }

        return true;
    }
}
