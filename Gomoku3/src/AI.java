import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sonchangwoo on 2016. 5. 2..
 */
public class AI {
    List<NextMove> defenceMove = new ArrayList<>();
    List<NextMove> offenceMove = new ArrayList<>();
    NextMove previousMove = new NextMove(0, 0);

    Rule aiRule = new Rule();

    void aiRuleInit() {
        aiRule.initBoard();
    }

    NextMove inputAi() {
        NextMove nextMove = nextInput(Board.board.clone(), -1);
        return nextMove;
    }

    NextMove inputAi(NextMove playerMove) {
        defenceMove.clear();
        offenceMove.clear();

        if (isOffence()) {
            List<NextMove> resultMove = new ArrayList<>();
            for (NextMove nextMove : offenceMove) {
                if (aiRule.check33(nextMove, 1) ==  -1) {
                    continue;
                } else {
                    resultMove.add(nextMove);
                }
            }

            if (resultMove.size() != 0) {
                int randomIndex = new Random().nextInt(resultMove.size());
                return resultMove.get(randomIndex);
            } else {
                if (isDefense(playerMove)) {
                    List<NextMove> resultMove2 = new ArrayList<>();
                    for (NextMove nextMove : defenceMove) {
                        if (aiRule.check33(nextMove, -1) == -1) {
                            continue;
                        } else {
                            resultMove2.add(nextMove);
                        }
                    }
                    if (resultMove2.size() != 0) {
                        int randomIndex = new Random().nextInt(resultMove2.size());
                        return resultMove2.get(randomIndex);
                    } else {
                        NextMove nextMove = nextInput(Board.board.clone(), -1);
                        return nextMove;
                    }
                } else {
                    NextMove nextMove = nextInput(Board.board.clone(), -1);
                    return nextMove;
                }
            }
        } else if (isDefense(playerMove)) {
            List<NextMove> resultMove =  new ArrayList<>();
            for (NextMove nextMove : defenceMove) {
                if(aiRule.check33(nextMove, -1) == -1) {
                    continue;
                } else {
                    resultMove.add(nextMove);
                }
            }
            if (resultMove.size() != 0) {
                int randomIndex = new Random().nextInt(resultMove.size());
                return resultMove.get(randomIndex);
            } else {
                NextMove nextMove = nextInput(Board.board.clone(), -1);
                return nextMove;
            }
        } else {
            NextMove nextMove = nextInput(Board.board.clone(), -1);
            return nextMove;
        }
    }

    private NextMove nextInput(int[][] board, int playerOrAi) {
        double MAX_SCORE = 1e20;

        List<NextMove> best = this.getBestCandidate(board, playerOrAi);

        if (best.isEmpty()) {
            NextMove nextMove = new NextMove(Board.boardSize / 2, Board.boardSize / 2);
            return nextMove;
        }

        best.parallelStream().forEach(pair -> {
            int[][] newChessboard = board.clone();
            newChessboard[pair.getMoveRow()][pair.getMoveCol()] = playerOrAi;
            double evaluationScore = isWin(newChessboard, pair) ? MAX_SCORE
                    : this.alphaBeta(0, -MAX_SCORE, MAX_SCORE, newChessboard, -playerOrAi);
            newChessboard[pair.getMoveRow()][pair.getMoveCol()] = 0;
            pair.setValue(evaluationScore);
        });

        double bestEvaluationScore = best.stream().map(pair -> pair.getValue()).max((a, b) -> Double.compare(a, b))
                .get();
        List<NextMove> result = best.stream()
                .filter(pair -> Math.abs(bestEvaluationScore - pair.getValue()) < 1e-6).collect(Collectors.toList());

        int randomIndex = new Random().nextInt(result.size());
        return result.get(randomIndex);
    }

    private List<NextMove> getBestCandidate(int[][] board, int playerOrAi) {
        double MAX_SCORE = 1e20;

        List<NextMove> temp = getCandidate(board);
        List<NextMove> realCandidate = possibleCandidate(temp, playerOrAi);

        return realCandidate.parallelStream().map(point -> {
            int[][] newChessboard = board.clone();
            newChessboard[point.getMoveRow()][point.getMoveCol()] = playerOrAi;
            double evaluationScore = isWin(newChessboard, point) ? MAX_SCORE
                    : getAllBoardEvaluation(newChessboard, playerOrAi);
            newChessboard[point.getMoveRow()][point.getMoveCol()] = 0;

            NextMove nextMove = new NextMove(point.getMoveRow(), point.getMoveCol());
            nextMove.setValue(evaluationScore);

            return nextMove;
        }).sorted((a, b) -> Double.compare(b.getValue(), a.getValue())).limit(20)
                .collect(Collectors.toList());
    }

    private double alphaBeta(int depth, double alpha, double beta, int[][] board, int playerOrAi) {
        double MAX_SCORE = 1e20;

        if (2 == depth) {
            return getAllBoardEvaluation(board, playerOrAi);
        } else {
            List<NextMove> bestPointsList = this.getBestCandidate(board, playerOrAi);
            for (NextMove nextMove : bestPointsList) {
                if ((depth & 1) == 0) {
                    board[nextMove.getMoveRow()][nextMove.getMoveCol()] = playerOrAi;
                    if (isWin(board, nextMove)) {
                        beta = -MAX_SCORE;
                    } else {
                        beta = Math.min(beta, this.alphaBeta(depth + 1, alpha, beta, board, -playerOrAi));
                    }
                    board[nextMove.getMoveRow()][nextMove.getMoveCol()] = 0;
                    if (beta <= alpha) {
                        break;
                    }
                } else {
                    board[nextMove.getMoveRow()][nextMove.getMoveCol()] = playerOrAi;
                    if (isWin(board, nextMove)) {
                        alpha = MAX_SCORE;
                    } else {
                        alpha = Math.max(alpha, this.alphaBeta(depth + 1, alpha, beta, board, -playerOrAi));
                    }
                    board[nextMove.getMoveRow()][nextMove.getMoveCol()] = 0;
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
            return ((depth & 1) == 0 ? beta : alpha) * 0.99;
        }
    }

    private double getAllBoardEvaluation(int[][] board, int playerOrAi) {
        Point[] calDirection = {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(-1, 1)};

        double black = 0;
        double white = 0;
        int size = Board.boardSize;

        for (int row = 0; row < size; ++row) {
            for (int column = 0; column < size; ++column) {
                if (0 != board[row][column]) {
                    NextMove point = new NextMove(row, column);
                    for (int i = 0; i < 4; ++i) {
                        double estimate = getInputEvaluation(board, point, calDirection[i], playerOrAi);
                        if (-1 == board[row][column]) {
                            black += estimate;
                        } else {
                            white += estimate;
                        }
                    }
                }
            }
        }
        double k = 2;
        return -1 == playerOrAi ? black - k * white : white - k * black;
    }

    private List<NextMove> getCandidate(int[][] board) {
        int SEARCH_RANGE = 1;
        List<NextMove> candidate = new ArrayList<>();
        int size = Board.boardSize;

        boolean[][] canBoard = new boolean[size][size];

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (0 == board[row][column]) {
                    continue;
                }
                int x0 = Math.max(0, row - SEARCH_RANGE), x1 = Math.min(size - 1, row + SEARCH_RANGE);
                int y0 = Math.max(0, column - SEARCH_RANGE), y1 = Math.min(size - 1, column + SEARCH_RANGE);
                for (int i = x0; i <= x1; ++i) {
                    for (int j = y0; j <= y1; ++j) {
                        if (0 == board[i][j]) {
                            canBoard[i][j] = true;
                        }
                    }
                }
            }
        }

        for (int row = 0; row < size; ++row) {
            for (int column = 0; column < size; ++column) {
                if (canBoard[row][column]) {
                    candidate.add(new NextMove(row, column));
                }
            }
        }

        if (candidate.isEmpty()) {
            candidate.add(new NextMove(Board.boardSize / 2, Board.boardSize / 2));
        }
        return candidate;
    }

    private double getInputEvaluation(int[][] board, NextMove nextMove, Point point, int playerOrAi) {
        Point resultPoint = analLinearCount(board, nextMove, point);
        int temp = resultPoint.x *3 + resultPoint.y;
        int result = Math.min(temp, 15);

        return calculateEvaluationScore(result, playerOrAi);
    }

    private boolean isWin(int[][] board, NextMove nextMove) {
        Point[] calDirection = {new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(-1, 1)};

        return Arrays.stream(calDirection).anyMatch(
                direction -> getlinearCount(board, nextMove, direction) == 5);
    }

    private Point analLinearCount(int[][] board, NextMove nextMove, Point point) {
        int playerOrAi = board[nextMove.getMoveRow()][nextMove.getMoveCol()];

        if (0 == playerOrAi) {
            return new Point(0, 0);
        }
        int size = Board.boardSize;

        int continue0 = 0;
        int empty0 = 0;
        int oppo0 = 0;
        for (int i = nextMove.getMoveRow(), j = nextMove.getMoveCol(); i >= 0 && i < size && j >= 0 && j < size; i += point.x, j += point.y) {
            int currentChessType = board[i][j];
            if (currentChessType == playerOrAi) {
                if (empty0 == 0) {
                    continue0++;
                } else {
                    oppo0++;
                }
            } else if (0 == currentChessType) {
                empty0++;
                if (empty0 > 1) {
                    break;
                }
            } else {
                break;
            }
        }

        int continue1 = 0;
        int empty1 = 0;
        int oppo1 = 0;
        for (int i = nextMove.getMoveRow() - point.x, j = nextMove.getMoveCol() - point.y; i >= 0 && i < size && j >= 0
                && j < size; i -= point.x, j -= point.y) {
            int currentChessType = board[i][j];
            if (currentChessType == playerOrAi) {
                if (empty1 == 0) {
                    continue1++;
                } else {
                    oppo1++;
                }
            } else if (0 == currentChessType) {
                empty1++;
                if (empty1 > 1) {
                    break;
                }
            } else {
                break;
            }
        }

        int continueCount = continue0 + continue1 + (Math.max(oppo1, oppo0) - 1);
        int empty = Math.min(1, empty1) + Math.min(1, empty0);
        if (!isPossible(board, nextMove, point)) {
            continueCount--;
            empty = 0;
        }
        return new Point(continueCount, empty);
    }

    private int getlinearCount(int[][] board, NextMove nextMove, Point point) {
        int playerOrAi = board[nextMove.getMoveRow()][nextMove.getMoveCol()];

        if (0 == playerOrAi) {
            return 0;
        }
        int size = Board.boardSize;

        int count = 0;
        for (int i = nextMove.getMoveRow(), j = nextMove.getMoveCol(); i >= 0 && i < size && j >= 0 && j < size; i += point.x, j += point.y) {
            if (board[i][j] == playerOrAi) {
                count++;
            } else {
                break;
            }
        }

        for (int i = nextMove.getMoveRow() - point.x, j = nextMove.getMoveCol() - point.y; i >= 0 && i < size && j >= 0
                && j < size; i -= point.x, j -= point.y) {
            if (board[i][j] == playerOrAi) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private boolean isPossible(int[][] board, NextMove nextMove, Point point) {
        int possible = 0;
        int size = Board.boardSize;

        int playerOrAi = board[nextMove.getMoveRow()][nextMove.getMoveCol()];
        for (int i = nextMove.getMoveRow(), j = nextMove.getMoveCol(); i >= 0 && i < size && j >= 0 && j < size
                && possible < 5; i += point.x, j += point.y) {
            int currentChessType = board[i][j];
            if (currentChessType == playerOrAi) {
                ++possible;
            } else {
                if (currentChessType == 0) {
                    ++possible;
                } else {
                    break;
                }
            }
        }
        for (int i = nextMove.getMoveRow() - point.x, j = nextMove.getMoveCol() - point.y; i >= 0 && i < size && j >= 0 && j < size
                && possible < 5; i -= point.x, j -=point.y) {
            int currentChessType = board[i][j];

            if (currentChessType == playerOrAi) {
                ++possible;
            } else {
                if (currentChessType == 0) {
                    ++possible;
                } else {
                    break;
                }
            }
        }
        return possible == 5;
    }

    private List<NextMove> possibleCandidate(List<NextMove> nextMoves, int playerOrAi) {
        List<NextMove> result = new ArrayList<>();

        for (NextMove nextMove : nextMoves) {
            if (aiRule.check33(nextMove, playerOrAi) == -1) {
                continue;
            } else {
                result.add(nextMove);
            }
        }
        return result;
    }

    private double calculateEvaluationScore(int caseCount, int playerOrAi) {

        if (playerOrAi == -1) {
            if (caseCount == 15) {
                // ooooo
                return 99999999;
            } else if (caseCount == 14) {
                // .oooo.
                return 20000000;
            } else if (caseCount == 13) {
                // .oooox
                return 4000000;
            } else if (caseCount == 11) {
                // .ooo.
                return 130000;
            } else if (caseCount == 10) {
                // .ooox
                return 11000;
//            } else if (caseCount == 12) {
//                // xoooox
            } else if (caseCount == 8) {
                // .oo.
                return 12000;
            } else if (caseCount == 7) {
                // .oox
                return 2500;
//            } else if (caseCount == 9) {
//                // xooox
            } else if (caseCount == 5) {
                // .o.
                return 5000;
//            } else if (caseCount == 6) {
//                // xoox
            } else if (caseCount == 4) {
                // .ox
                return 1250;
//            } else if (caseCount == 3) {
//                // xox
            } else {
                return 0;
            }
        } else {
            if (caseCount == 15) {
                // ooooo
                return 99999999;
            } else if (caseCount == 14) {
                // .oooo.
                return 200000;
            } else if (caseCount == 13) {
                // .oooox
                return 200000;
            } else if (caseCount == 11) {
                // .ooo.
                return 60000;
            } else if (caseCount == 10) {
                // .ooox
                return 11000;
//            } else if (caseCount == 12) {
//                // xoooox
            } else if (caseCount == 8) {
                // .oo.
                return 4000;
            } else if (caseCount == 7) {
                // .oox
                return 1000;
//            } else if (caseCount == 9) {
//                // xooox
            } else if (caseCount == 5) {
                // .o.
                return 2000;
//            } else if (caseCount == 6) {
//                // xoox
            } else if (caseCount == 4) {
                // .ox
                return 500;
//            } else if (caseCount == 3) {
//                // xox
            } else {
                return 0;
            }
        }
    }

    public boolean isDefense(NextMove playerMove) {
        defenceMove.clear();

        // check 4
        if (checkDefenceLeftRight4(playerMove, 1, defenceMove)) {
            return true;
        } else if (checkDefenceUpDown4(playerMove, 1, defenceMove)) {
            return true;
        } else if (checkDefenceDiag4(playerMove, 1, defenceMove)) {
            return true;
        }

        // check open3
        if (checkDefenceLeftRight(playerMove, 1, defenceMove)) {
            return true;
        } else if (checkDefenceUpDown(playerMove, 1, defenceMove)) {
            return true;
        } else if (checkDefenceDiag(playerMove, 1, defenceMove)) {
            return true;
        }
        return false;
    }

    private boolean isOffence() {
        offenceMove.clear();

        if (checkDefenceLeftRight4(previousMove, -1, offenceMove)) {
            return true;
        } else if (checkDefenceUpDown4(previousMove, -1, offenceMove)) {
            return true;
        } else if (checkDefenceDiag4(previousMove, -1, offenceMove)) {
            return true;
        }

        return false;
    }

    private boolean checkDefenceLeftRight4(NextMove input, int playerOrAi, List<NextMove> defenceMove) {
        int board[][] = Board.board;
        int x = input.getMoveRow();
        int y = input.getMoveCol();

        // Case 1
        if (y >= 0 && y <= 10) {
            int count = 0;
            for (int i=1; i<5;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==2 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==4) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()+4;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 1 && y<= 11) {
            int count = 0;
            for (int i=-1; i<4;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==2 || i==-1) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==3) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()+3;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 2 && y <= 12) {
            int count = 0;
            for (int i=-2; i<3;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()+2;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 3 && y <= 13) {
            int count = 0;
            for (int i=-3; i<2;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-3 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()+1;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        // Case 2
        if (y >= 1 && y <= 11) {
            int count = 0;
            for (int i=-1; i<4;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==2 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-1;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 2 && y <= 12) {
            int count = 0;
            for (int i=-2; i<3;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==2 || i==-1) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-2;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 3 && y <= 13) {
            int count = 0;
            for (int i=-3; i<2;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==-1 || i==-2) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-3) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-3;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 4 && y <= 14) {
            int count = 0;
            for (int i=-4; i<1;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-1 || i==-2 || i==-3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-4) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-4;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        // Case 3
        if (y >= 0 && y <= 10) {
            int count = 0;
            for (int i=1; i<5;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==4 || i==2 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()+1;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 2 && y <= 12) {
            int count = 0;
            for (int i=-2; i<3;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==2 || i==-2) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-1;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 3 && y <= 13) {
            int count = 0;
            for (int i=-3; i<2;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==-1 || i==-3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-2;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 4 && y <= 14) {
            int count = 0;
            for (int i=-4; i<1;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-1 || i==-2 || i==-4) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-3) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-3;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        // Case 4
        if (y >= 0 && y <= 10) {
            int count = 0;
            for (int i=1; i<5;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==4 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()+2;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 1 && y <= 11) {
            int count = 0;
            for (int i=-1; i<4;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-1 || i==2 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()+1;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 3 && y <= 13) {
            int count = 0;
            for (int i=-3; i<2;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==-3 || i==-2) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-1;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 4 && y <= 14) {
            int count = 0;
            for (int i=-4; i<1;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-1 || i==-4 || i==-3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-2;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        // Case 5
        if (y >= 0 && y <= 10) {
            int count = 0;
            for (int i=1; i<5;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==2 || i==4) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==3) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()+3;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 1 && y <= 11) {
            int count = 0;
            for (int i=-1; i<4;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==1 || i==-1 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()+2;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 2 && y <= 12) {
            int count = 0;
            for (int i=-2; i<3;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-2 || i==2 || i==-1) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()+1;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 4 && y <= 14) {
            int count = 0;
            for (int i=-4; i<1;i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-4 || i==-2 || i==-3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-1;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }


        return false;
    }

    private boolean checkDefenceUpDown4(NextMove input, int playerOrAi, List<NextMove> defenceMove) {
        int board[][] = Board.board;
        int x = input.getMoveRow();
        int y = input.getMoveCol();

        // Case 1
        if (x >= 0 && x <= 10) {
            int count = 0;
            for (int i=1; i<5;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==2 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==4) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()+4;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 1 && x<= 11) {
            int count = 0;
            for (int i=-1; i<4;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==2 || i==-1) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==3) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()+3;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 2 && x <= 12) {
            int count = 0;
            for (int i=-2; i<3;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()+2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 3 && x <= 13) {
            int count = 0;
            for (int i=-3; i<2;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-3 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()+1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        // Case 2
        if (x >= 1 && x <= 11) {
            int count = 0;
            for (int i=-1; i<4;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==2 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 2 && x <= 12) {
            int count = 0;
            for (int i=-2; i<3;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==2 || i==-1) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 3 && x <= 13) {
            int count = 0;
            for (int i=-3; i<2;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==-1 || i==-2) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-3) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-3;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 4 && x <= 14) {
            int count = 0;
            for (int i=-4; i<1;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-1 || i==-2 || i==-3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-4) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-4;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        // Case 3
        if (x >= 0 && x <= 10) {
            int count = 0;
            for (int i=1; i<5;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==4 || i==2 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()+1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 2 && x <= 12) {
            int count = 0;
            for (int i=-2; i<3;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==2 || i==-2) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 3 && x <= 13) {
            int count = 0;
            for (int i=-3; i<2;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==-1 || i==-3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 4 && x <= 14) {
            int count = 0;
            for (int i=-4; i<1;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-1 || i==-2 || i==-4) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-3) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-3;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        // Case 4
        if (x >= 0 && x <= 10) {
            int count = 0;
            for (int i=1; i<5;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==4 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()+2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 1 && x <= 11) {
            int count = 0;
            for (int i=-1; i<4;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-1 || i==2 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()+1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 3 && x <= 13) {
            int count = 0;
            for (int i=-3; i<2;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==-3 || i==-2) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 4 && x <= 14) {
            int count = 0;
            for (int i=-4; i<1;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-1 || i==-4 || i==-3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        // Case 5
        if (x >= 0 && x <= 10) {
            int count = 0;
            for (int i=1; i<5;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==2 || i==4) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==3) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()+3;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 1 && x <= 11) {
            int count = 0;
            for (int i=-1; i<4;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==1 || i==-1 || i==3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==2) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()+2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 2 && x <= 12) {
            int count = 0;
            for (int i=-2; i<3;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-2 || i==2 || i==-1) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()+1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (x >= 4 && x <= 14) {
            int count = 0;
            for (int i=-4; i<1;i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-4 || i==-2 || i==-3) && (board[x][y] == playerOrAi)) {
                    count++;
                } else if ((i==-1) && (board[x][y] == 0)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        return false;
    }

    private boolean checkDefenceDiag4(NextMove input, int playerOrAi, List<NextMove> defenceMove) {
        int board[][] = Board.board;
        int x = input.getMoveRow();
        int y = input.getMoveCol();

        // Case 1
        if (y >= 0 && y <= 10) {
            if (x >=0 && x <= 10) {
                int count = 0;
                for (int i=1; i<5;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==1 || i==2 || i==3) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==4) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+4;
                    y = input.getMoveCol()+4;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x >= 4 && x <= 14) {
                int count = 0;
                for (int i=1; i<5;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==1 || i==2 || i==3) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==4) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-4;
                    y = input.getMoveCol()+4;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 1 && y <= 11) {
            if (x >=1 && x <= 11) {
                int count = 0;
                for (int i=-1; i<4;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==1 || i==2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==3) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+3;
                    y = input.getMoveCol()+3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 3 && x <= 13) {
                int count = 0;
                for (int i=-1; i<4;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==1 || i==2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==3) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-3;
                    y = input.getMoveCol()+3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 2 && y <= 12) {
            if (x >= 2 && x <= 12) {
                int count = 0;
                for (int i=-2; i<3;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==1 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+2;
                    y = input.getMoveCol()+2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 2 && x <= 12) {
                int count = 0;
                for (int i=-2; i<3;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==1 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-2;
                    y = input.getMoveCol()+2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 3 && y <= 13) {
            if (x >= 3 && x <= 13) {
                int count = 0;
                for (int i=-3; i<2;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==-3 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }

            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 1 && x <= 11) {
                int count = 0;
                for (int i=-3; i<2;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==-3 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        // Case 2
        if (y >= 0 && y <= 10) {
            if (x >=0 && x <= 10) {
                int count = 0;
                for (int i=1; i<5;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==1 || i==2 || i==4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==3) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+3;
                    y = input.getMoveCol()+3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x >= 4 && x <= 14) {
                int count = 0;
                for (int i=1; i<5;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==1 || i==2 || i==4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==3) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-3;
                    y = input.getMoveCol()+3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 1 && y <= 11) {
            if (x >=1 && x <= 11) {
                int count = 0;
                for (int i=-1; i<4;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==1 || i==3 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+2;
                    y = input.getMoveCol()+2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 3 && x <= 13) {
                int count = 0;
                for (int i=-1; i<4;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==1 || i==3 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-2;
                    y = input.getMoveCol()+2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 2 && y <= 12) {
            if (x >= 2 && x <= 12) {
                int count = 0;
                for (int i=-2; i<3;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==2 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 2 && x <= 12) {
                int count = 0;
                for (int i=-2; i<3;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==2 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 4 && y <= 14) {
            if (x >= 4 && x <= 14) {
                int count = 0;
                for (int i=-4; i<1;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==-3 || i==-2 || i==-4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }

            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 0 && x <= 10) {
                int count = 0;
                for (int i=-4; i<1;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==-3 || i==-2 || i==-4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        // Case 3

        if (y >= 0 && y <= 10) {
            if (x >=0 && x <= 10) {
                int count = 0;
                for (int i=1; i<5;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==1 || i==3 || i==4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+2;
                    y = input.getMoveCol()+2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x >= 4 && x <= 14) {
                int count = 0;
                for (int i=1; i<5;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==1 || i==3 || i==4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-2;
                    y = input.getMoveCol()+2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 1 && y <= 11) {
            if (x >=1 && x <= 11) {
                int count = 0;
                for (int i=-1; i<4;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==2 || i==3 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 3 && x <= 13) {
                int count = 0;
                for (int i=-1; i<4;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==2 || i==3 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 3 && y <= 13) {
            if (x >= 3 && x <= 13) {
                int count = 0;
                for (int i=-3; i<2;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==-3 || i==-2 || i==1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }

            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 1 && x <= 11) {
                int count = 0;
                for (int i=-3; i<2;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==-3 || i==-2 || i==1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 4 && y <= 14) {
            if (x >= 4 && x <= 14) {
                int count = 0;
                for (int i=-4; i<1;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==-3 || i==-1 || i==-4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-2;
                    y = input.getMoveCol()-2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }

            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 0 && x <= 10) {
                int count = 0;
                for (int i=-4; i<1;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==-3 || i==-1 || i==-4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+2;
                    y = input.getMoveCol()-2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        // Case 4

        if (y >= 0 && y <= 10) {
            if (x >=0 && x <= 10) {
                int count = 0;
                for (int i=1; i<5;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==3 || i==2 || i==4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x >= 4 && x <= 14) {
                int count = 0;
                for (int i=1; i<5;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==3 || i==2 || i==4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 4 && y <= 14) {
            if (x >= 4 && x <= 14) {
                int count = 0;
                for (int i=-4; i<1;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==-1 || i==-2 || i==-4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-3) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-3;
                    y = input.getMoveCol()-3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }

            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 0 && x <= 10) {
                int count = 0;
                for (int i=-4; i<1;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==-1 || i==-2 || i==-4) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-3) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+3;
                    y = input.getMoveCol()-3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 2 && y <= 12) {
            if (x >= 2 && x <= 12) {
                int count = 0;
                for (int i=-2; i<3;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==2 || i==-2 || i==1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 2 && x <= 12) {
                int count = 0;
                for (int i=-2; i<3;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==2 || i==-2 || i==1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }

        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 3 && y <= 13) {
            if (x >= 3 && x <= 13) {
                int count = 0;
                for (int i=-3; i<2;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==-3 || i==1 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-2;
                    y = input.getMoveCol()-2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }

            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 1 && x <= 11) {
                int count = 0;
                for (int i=-3; i<2;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==-3 || i==1 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+2;
                    y = input.getMoveCol()-2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        // Case 5

        if (y >= 4 && y <= 14) {
            if (x >= 4 && x <= 14) {
                int count = 0;
                for (int i=-4; i<1;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==-3 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-4) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-4;
                    y = input.getMoveCol()-4;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }

            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 0 && x <= 10) {
                int count = 0;
                for (int i=-4; i<1;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==-3 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-4) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+4;
                    y = input.getMoveCol()-4;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 1 && y <= 11) {
            if (x >=1 && x <= 11) {
                int count = 0;
                for (int i=-1; i<4;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==1 || i==3 || i==2) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 3 && x <= 13) {
                int count = 0;
                for (int i=-1; i<4;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==1 || i==3 || i==2) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-1) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 2 && y <= 12) {
            if (x >= 2 && x <= 12) {
                int count = 0;
                for (int i=-2; i<3;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==2 || i==1 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-2;
                    y = input.getMoveCol()-2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 2 && x <= 12) {
                int count = 0;
                for (int i=-2; i<3;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==2 || i==1 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-2) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+2;
                    y = input.getMoveCol()-2;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }

        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        if (y >= 3 && y <= 13) {
            if (x >= 3 && x <= 13) {
                int count = 0;
                for (int i=-3; i<2;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;

                    if ((i==1 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-3) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()-3;
                    y = input.getMoveCol()-3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }

            x = input.getMoveRow();
            y = input.getMoveCol();

            if (x >= 1 && x <= 11) {
                int count = 0;
                for (int i=-3; i<2;i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i==1 || i==-2 || i==-1) && (board[x][y] == playerOrAi)) {
                        count++;
                    } else if ((i==-3) && (board[x][y] == 0)) {
                        count++;
                    }
                }
                if (count==4) {
                    x = input.getMoveRow()+3;
                    y = input.getMoveCol()-3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        return false;
    }


    private boolean checkDefenceLeftRight(NextMove input, int playerOrAi, List<NextMove> defenceMove) {
        int board[][] = Board.board;
        int x = input.getMoveRow();
        int y = input.getMoveCol();

        //Case1
        if (y >= 2 && y <= 12) {
            int count = 0;
            for(int i = -2; i<3 ; i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-2 || i==2) && (board[x][y]==0)) {
                    count++;
                } else if ((i==-1 || i==1) && (board[x][y] == playerOrAi)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-2;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()+2;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 2
        if (y >= 1 && y <= 11) {
            int count = 0;
            for(int i = -1; i<4 ; i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-1 || i==3) && board[x][y] == 0) {
                    count++;
                } else if ((i==1 || i==2) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-1;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()+3;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 3
        if (y >= 3 && y <= 13) {
            int count = 0;
            for(int i = -3; i<2 ; i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-3 || i==1) && board[x][y] == 0) {
                    count++;
                } else if ((i==-1 || i==-2) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow();
                y = input.getMoveCol()-3;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()+1;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 4
        if (y >= 3 && y <= 12) {
            int count =0;
            for(int i= -3; i< 3; i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-3 || i==2 || i==-1) && board[x][y] == 0) {
                    count++;
                } else if ((i==1 || i==-2) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow();
                y = input.getMoveCol()-1;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()+2;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()-3;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 5
        if (y >= 2 && y <= 11) {
            int count =0;
            for(int i= -2; i< 4; i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-2 || i==1 || i==3) && board[x][y] == 0) {
                    count++;
                } else if ((i==-1 || i==2) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow();
                y = input.getMoveCol()+1;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()-2;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()+3;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 6
        if (y >= 1 && y <= 10) {
            int count =0;
            for(int i= -1; i< 5; i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-1 || i==1 || i==4) && board[x][y] == 0) {
                    count++;
                } else if ((i==2 || i==3) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow();
                y = input.getMoveCol()+1;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()-1;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()+4;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 7
        if (y >= 4 && y <= 13) {
            int count =0;
            for(int i= -4; i < 2; i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-4 || i==-1 || i==1) && board[x][y] == 0) {
                    count++;
                } else if ((i==-2 || i==-3) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow();
                y = input.getMoveCol()-1;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()-4;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()+1;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 8
        if (y >= 1 && y <= 10) {
            int count =0;
            for(int i= -1; i< 5; i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-1 || i==2 || i==4) && board[x][y]==0) {
                    count++;
                } else if ((i==1 || i==3) && board[x][y]==playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow();
                y = input.getMoveCol()+2;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()-1;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()+4;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 9
        if (y >= 4 && y <= 13) {
            int count =0;
            for(int i= -4; i< 2; i++) {
                y = input.getMoveCol();
                y = y+i;

                if ((i==-4 || i==-2 || i==1) && board[x][y]== 0) {
                    count++;
                } else if ((i==-1 || i==-3) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow();
                y = input.getMoveCol()-2;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()-4;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow();
                y = input.getMoveCol()+1;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        return false;
    }

    private boolean checkDefenceUpDown(NextMove input, int playerOrAi, List<NextMove> defenceMove) {
        int board[][] = Board.board;
        int x = input.getMoveRow();
        int y = input.getMoveCol();


        if (x >=2 && x<= 12) {
            int count = 0;
            for(int i = -2; i<3 ; i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-2 || i==2) && (board[x][y] == 0)) {
                    count++;
                } else if ((i==-1 || i==1) && (board[x][y] == playerOrAi)) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()+2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 2
        if (x >= 1 && x <= 11) {
            int count = 0;
            for(int i = -1; i<4 ; i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-1 || i==3) && board[x][y]==0) {
                    count++;
                } else if ((i==1 || i==2) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()+3;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 3
        if (x >= 3 && x <= 13) {
            int count = 0;
            for(int i = -3; i<2 ; i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-3 || i==1) && board[x][y] == 0) {
                    count++;
                } else if ((i==-1 || i==-2) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-3;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()+1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 4
        if (x >= 3 && x <= 12) {
            int count =0;
            for(int i= -3; i< 3; i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-3 || i==2 || i==-1) && board[x][y] ==0) {
                    count++;
                } else if ((i==1 || i==-2) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow()-1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()-3;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()+2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 5
        if (x >= 2 && x <= 11) {
            int count =0;
            for(int i= -2; i< 4; i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-2 || i==1 || i==3) && board[x][y] == 0) {
                    count++;
                } else if ((i==-1 || i==2) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow()+1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()-2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()+3;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 6
        if (x >= 1 && x <= 10) {
            int count =0;
            for(int i= -1; i< 5; i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-1 || i==1 || i==4) && board[x][y] == 0) {
                    count++;
                } else if ((i==2 || i==3) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow()+1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()-1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()+4;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 7
        if (x >= 4 && x <= 13) {
            int count =0;
            for(int i= -4; i< 2; i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-4 || i==-1 || i==1) && board[x][y] == 0) {
                    count++;
                } else if ((i==-2 || i==-3) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow()-1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()-4;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()+1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 8
        if (x >= 1 && x <= 10) {
            int count =0;
            for(int i= -1; i< 5; i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-1 || i==2 || i==4) && board[x][y] == 0) {
                    count++;
                } else if ((i==1 || i==3) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow()+2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()-1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()+4;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        x = input.getMoveRow();
        y = input.getMoveCol();
        //Case 9
        if (x >= 4 && x <= 13) {
            int count =0;
            for(int i= -4; i< 2; i++) {
                x = input.getMoveRow();
                x = x+i;

                if ((i==-4 || i==-2 || i==1) && board[x][y] == 0) {
                    count++;
                } else if ((i==-1 || i==-3) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==5) {
                x = input.getMoveRow()-2;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()-4;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()+1;
                y = input.getMoveCol();
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }

        return false;

    }

    private boolean checkDefenceDiag(NextMove input, int playerOrAi, List<NextMove> defenceMove) {
        int board[][] = Board.board;
        int x = input.getMoveRow();
        int y = input.getMoveCol();


        if ((y >=2 && x >=2) && (y<= 12 && x <= 12)) {
            int count = 0;
            for(int i = -2; i<3 ; i++) {
                x = input.getMoveRow();
                y = input.getMoveCol();
                x = x+i;
                y = y+i;

                if ((i==-2 || i==2) && board[x][y] == 0) {
                    count++;
                } else if ((i==-1 || i==1) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()-2;
                y = input.getMoveCol()-2;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()+2;
                y = input.getMoveCol()+2;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            count = 0;
            for(int i = -2; i<3 ; i++) {
                x = input.getMoveRow();
                y = input.getMoveCol();
                x = x-i;
                y = y+i;


                if ((i==-2 || i==2) && board[x][y] == 0) {
                    count++;
                } else if ((i==-1 || i==1) && board[x][y] == playerOrAi) {
                    count++;
                }
            }
            if (count==4) {
                x = input.getMoveRow()+2;
                y = input.getMoveCol()-2;
                defenceMove.add(new NextMove(x, y));
                x = input.getMoveRow()-2;
                y = input.getMoveCol()+2;
                defenceMove.add(new NextMove(x, y));
                return true;
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        //Case 2
        if (y >= 1 && y <= 11) {
            if (x >= 2 && x <= 11) {
                int count = 0;
                for (int i = -1; i < 4; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;


                    if ((i == -1 || i == 3) && board[x][y] == 0) {
                        count++;
                    } else if ((i == 1 || i == 2) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 4) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+3;
                    y = input.getMoveCol()+3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x >=3 && x <= 13) {
                int count = 0;
                for (int i = -1; i < 4; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;


                    if ((i == -1 || i == 3) && board[x][y] == 0) {
                        count++;
                    } else if ((i == 1 || i == 2) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 4) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-3;
                    y = input.getMoveCol()+3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        //Case 3
        if (y >= 3 && y <= 13) {
            if (x>=3 && x<=13) {
                int count = 0;
                for (int i = -3; i < 2; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;


                    if ((i == -3 || i == 1) && board[x][y] == 0) {
                        count++;
                    } else if ((i == -1 || i == -2) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 4) {
                    x = input.getMoveRow()-3;
                    y = input.getMoveCol()-3;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x>=1 && x<=11) {
                int count = 0;
                for (int i = -3; i < 2; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;


                    if ((i == -3 || i == 1) && board[x][y] == 0) {
                        count++;
                    } else if ((i == -1 || i == -2) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 4) {
                    x = input.getMoveRow()+3;
                    y = input.getMoveCol()-3;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        //Case 4
        if (y >= 3 && y <= 12) {
            if (x>=3 && x<=12) {
                int count = 0;
                for (int i = -3; i < 3; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;


                    if ((i == -3 || i == 2 || i == -1) && board[x][y] == 0) {
                        count++;
                    } else if ((i == 1 || i == -2) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+2;
                    y = input.getMoveCol()+2;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-3;
                    y = input.getMoveCol()-3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x>=2 && x<=11) {
                int count = 0;
                for (int i = -3; i < 3; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;


                    if ((i == -3 || i == 2 || i == -1) && board[x][y] == 0) {
                        count++;
                    } else if ((i == 1 || i == -2) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-2;
                    y = input.getMoveCol()+2;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+3;
                    y = input.getMoveCol()-3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        //Case 5
        if (y >= 2 && y <= 11) {
            if (x>=2 && x<=11) {
                int count = 0;
                for (int i = -2; i < 4; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;


                    if ((i == -2 || i == 1 || i == 3) && board[x][y] == 0) {
                        count++;
                    } else if ((i == -1 || i == 2) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-2;
                    y = input.getMoveCol()-2;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+3;
                    y = input.getMoveCol()+3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x>=3 && x<=12) {
                int count = 0;
                for (int i = -2; i < 4; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;


                    if ((i == -2 || i == 1 || i == 3) && board[x][y] == 0) {
                        count++;
                    } else if ((i == -1 || i == 2) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+2;
                    y = input.getMoveCol()-2;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-3;
                    y = input.getMoveCol()+3;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        //Case 6
        if (y >= 1 && y <= 10) {
            if (x>=1 && x<=10) {
                int count = 0;
                for (int i = -1; i < 5; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;


                    if ((i == -1 || i == 1 || i == 4) && board[x][y] == 0) {
                        count++;
                    } else if ((i == 2 || i == 3) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+4;
                    y = input.getMoveCol()+4;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x>=4 && x<=13) {
                int count = 0;
                for (int i = -1; i < 5; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;


                    if ((i == -1 || i == 4 || i == 1) && board[x][y] == 0) {
                        count++;
                    } else if ((i == 2 || i == 3) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-4;
                    y = input.getMoveCol()+4;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        //Case 7
        if (y >= 4 && y <= 13) {
            if (x>=4 && x<=13) {
                int count = 0;
                for (int i = -4; i < 2; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;


                    if ((i == -4 || i == -1 || i == 1) && board[x][y] == 0) {
                        count++;
                    } else if ((i == -2 || i == -3) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-4;
                    y = input.getMoveCol()-4;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x>=1 && x<=10) {
                int count = 0;
                for (int i = -4; i < 2; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i == -4 || i == -1 || i == 1) && board[x][y] == 0) {
                        count++;
                    } else if ((i == -2 || i == -3) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+4;
                    y = input.getMoveCol()-4;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }

        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        //Case 8
        if (y >= 1 && y <= 10) {
            if (x >= 1 && x <= 10) {
                int count = 0;
                for (int i = -1; i < 5; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;


                    if ((i == -1 || i == 2 || i == 4) && board[x][y] == 0) {
                        count++;
                    } else if ((i == 1 || i == 3) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()+2;
                    y = input.getMoveCol()+2;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+4;
                    y = input.getMoveCol()+4;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x>=4 && x<=13) {
                int count = 0;
                for (int i = -1; i < 5; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;


                    if ((i == -1 || i == 2 || i == 4) && board[x][y] == 0) {
                        count++;
                    } else if ((i == 1 || i == 3) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()-2;
                    y = input.getMoveCol()+2;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-4;
                    y = input.getMoveCol()+4;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()-1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        //Case 9
        if (y >= 4 && y <= 13) {
            if (x >= 4 && x <= 13) {
                int count = 0;
                for (int i = -4; i < 2; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x+i;
                    y = y+i;


                    if ((i == -4 || i == -2 || i == 1) && board[x][y] == 0) {
                        count++;
                    } else if ((i == -1 || i == -3) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()-2;
                    y = input.getMoveCol()-2;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-4;
                    y = input.getMoveCol()-4;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
            x = input.getMoveRow();
            y = input.getMoveCol();
            if (x>=1 && x<=10) {
                int count = 0;
                for (int i = -4; i < 2; i++) {
                    x = input.getMoveRow();
                    y = input.getMoveCol();
                    x = x-i;
                    y = y+i;

                    if ((i == -4 || i == -2 || i == 1) && board[x][y] == 0) {
                        count++;
                    } else if ((i == -1 || i == -3) && board[x][y] == playerOrAi) {
                        count++;
                    }
                }
                if (count == 5) {
                    x = input.getMoveRow()+2;
                    y = input.getMoveCol()-2;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()+4;
                    y = input.getMoveCol()-4;
                    defenceMove.add(new NextMove(x, y));
                    x = input.getMoveRow()-1;
                    y = input.getMoveCol()+1;
                    defenceMove.add(new NextMove(x, y));
                    return true;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();

        return false;

    }
}