package com.example.tictactoe;

import java.util.ArrayList;

enum GameState
{
    ONGOING,
    COMPUTER_WIN,
    PLAYER_WIN,
    TIE
    };
public class Model
{
    private static final  int ROWS=3;
    private static final  int COLS=3;
    private char[][] board;
    private int numTurns=0;
    public Model()
    {
        board = new char[ROWS][COLS];
    }
    public void startGame() {
        this.numTurns = 0;
        // clear board
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                this.board[i][j] = ' ';
            }
        }
    }

    // at the moment check only num turns
    // add here win check
    public GameState gameOver()
    {
        boolean playerWin=true,
                computerWin=true;

        for (int i=0;i<ROWS;i++)
        {
            // mian diagonal
            if(this.board[i][i]!='X')
                playerWin=false;
            if(this.board[i][i]!='O')
                computerWin=false;
        }
        if(playerWin)
            return GameState.PLAYER_WIN;
        else if(computerWin)
            return GameState.COMPUTER_WIN;

        //second diagonal
        playerWin=true;
        computerWin=true;
        for (int i=0;i<ROWS;i++)
        {
            if(this.board[i][COLS-i-1]!='X')
                playerWin=false;
            if(this.board[i][COLS-i-1]!='O')
                computerWin=false;
        }
        if(playerWin)
            return GameState.PLAYER_WIN;
        else if(computerWin)
            return GameState.COMPUTER_WIN;

        // all three rows
        for(int i=0;i<ROWS;i++)
        {
            if(this.board[i][0]=='X' && this.board[i][1]=='X' && this.board[i][2]=='X')
                return GameState.PLAYER_WIN;
            if(this.board[i][0]=='O' && this.board[i][1]=='O' && this.board[i][2]=='O')
                return GameState.COMPUTER_WIN;

        }

        // all three columns
        for(int i=0;i<COLS;i++)
        {
            if(this.board[0][i]=='X' && this.board[1][i]=='X' && this.board[2][i]=='X')
            return GameState.PLAYER_WIN;
            if(this.board[0][i]=='O' && this.board[1][i]=='O' && this.board[2][i]=='O')
                return GameState.COMPUTER_WIN;
        }


        if(numTurns == ROWS*COLS)
            return GameState.TIE;
        return GameState.ONGOING;

    }

    public void userTurn(Move m)
    {
        board[m.getRow()][m.getCol()] = 'X';
        this.numTurns++;
    }
    /// at the moment find the first available move
    public Move computerTurn()
    {
        boolean found = false;
        Move m=negamax(8,true);
        this.board[m.getRow()][m.getCol()]='O';
        this.numTurns++;
        return m;
    }

    private Move negamax(int depth,boolean computer)
    {
        // check if game is over - computer player or tie
        if(gameOver() != GameState.ONGOING || depth ==0)
        {
            Move m = new Move(0,0);
            m.setScore(evaluate(computer));
            return m;
        }
        ArrayList<Move> possibleMoves = getPossibleMoves();
        Move bestMove = new Move(0,0);
        bestMove.setScore(Integer.MIN_VALUE);

        for (Move m : possibleMoves) {
            //performMove(m);
            char XO = 'X';
            if (computer)
                XO = 'O';
            board[m.getRow()][m.getCol()] = XO;
            this.numTurns++;

            Move move = negamax(depth - 1, !computer);
            move.negateScore();
            if (move.getScore() > bestMove.getScore()) {
                bestMove = m;
                bestMove.setScore(move.getScore());
            }
            // undo move
            board[m.getRow()][m.getCol()] = ' ';
            this.numTurns--;
        }
        return bestMove;
    }

    private ArrayList<Move> getPossibleMoves()
    {
        ArrayList<Move> moves = new ArrayList<>();
        for(int i=0;i<ROWS;i++) {
            for (int j = 0; j < COLS; j++) {
                if (this.board[i][j] == ' ')
                {
                    moves.add(new Move(i,j));
                }
            }
        }
        return moves;

    }

    private int evaluate(boolean computer) {
        GameState state = gameOver();

        // if computer wins and computer turn 10, else other win -> -10
        if(state == GameState.COMPUTER_WIN )
            if(computer)
                return 10;
            else
                return -10;
            // if player wins and player turn 10, else other win -> -10
        else if (state == GameState.PLAYER_WIN)
            if(!computer)
                return 10;
            else
                return -10;

        if(state == GameState.ONGOING)
            return 0;
        else  // tie
            return 1;
    }

}

