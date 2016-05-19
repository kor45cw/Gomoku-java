/**
 * Created by sonchangwoo on 2016. 5. 2..
 */
public class Rule {
    int boardSize = 15;
    int ruleBoard[][];

    public Rule() {
        ruleBoard = new int[boardSize][boardSize];
    }

    void initBoard() {
        for (int i=0; i<boardSize ;i++) {
            for (int j=0; j< boardSize; j++) {
                ruleBoard[i][j] = 0;
            }
        }
    }

    private int getBoardScore(NextMove input) {
        return ruleBoard[input.getMoveRow()][input.getMoveCol()];
    }

    public int check33(NextMove move, int playerOrAi) {

        //가로
        checkLeftRight(move, playerOrAi);

        //세로
        checkUpDown(move, playerOrAi);

        //대각선
        checkdiag(move, playerOrAi);

        if (getBoardScore(move) < -1) {
            this.initBoard();
            return -1;
        } else {
            this.initBoard();
            return 1;
        }
    }

    // x는 숫자, y는 문자
    private void checkLeftRight(NextMove input, int playerOrAi) {
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
                y = input.getMoveCol();
                ruleBoard[x][y] -= 1;
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
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
            }
        }
    }

    private void checkUpDown(NextMove input, int playerOrAi) {
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
                x = input.getMoveRow();
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                x = input.getMoveRow();
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                x = input.getMoveRow();
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                x = input.getMoveRow();
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                x = input.getMoveRow();
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                x = input.getMoveRow();
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                x = input.getMoveRow();
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                x = input.getMoveRow();
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                x = input.getMoveRow();
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
            }
        }


    }

    private void checkdiag(NextMove input, int playerOrAi) {
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
                x = input.getMoveRow();
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                x = input.getMoveRow();
                y = input.getMoveCol();

                ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
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
                    x = input.getMoveRow();
                    y = input.getMoveCol();

                    ruleBoard[x][y] -= 1;
                }
            }
        }
        x = input.getMoveRow();
        y = input.getMoveCol();


    }
}
