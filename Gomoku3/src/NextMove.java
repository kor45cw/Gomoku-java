/**
 * Created by sonchangwoo on 2016. 5. 2..
 */

public class NextMove {
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private int moveCol;
    private int moveRow;
    private double value;

    public NextMove(int i, int j) {
        this.moveRow = i;
        this.moveCol = j;
    }

    @Override
    public String toString() {
        char alpha = alphabet.charAt(moveCol);
        String result = Character.toString(alpha) + Integer.toString(moveRow+1);
        return result;
    }

    public void setMove(int i, int j) {
        moveRow = i;
        moveCol = j;
    }

    public int getMoveCol() {
        return moveCol;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setMoveCol(int moveCol) {
        this.moveCol = moveCol;
    }

    public int getMoveRow() {
        return moveRow;
    }

    public void setMoveRow(int moveRow) {
        this.moveRow = moveRow;
    }
}

