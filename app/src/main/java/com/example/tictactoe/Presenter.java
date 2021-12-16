package com.example.tictactoe;

import android.util.Log;

public class Presenter implements IPresenter,FirebaseComm.IOnFirebaseResult
{

    private static final String TAG = "Presenter";
    private Model model;
    private IView view;
    private char computer = 'O';

    private FirebaseComm comm;

    public Presenter(IView view)
    {
        this.view = view;
        this.model = new Model();
        this.model.startGame();
        comm = new FirebaseComm(this);
    }

    @Override
    public void restartGame()
    {
        // clear view and model
        this.model.startGame();
        this.view.clearBoard();

    }

    @Override
    public void userRegister(String email, String password) {
        comm.registerUser(email,password);

    }

    @Override
    public void userLogin(String email, String password) {

    }
    @Override
    public void firebaseResult(FirebaseComm.ResultType result, boolean success) {
        Log.d(TAG, "firebaseResult: PResenter : " + success);
    }

    @Override
    public void userClick(int row, int col)
    {
        // create move and pass to model
        Move m = new Move(row,col);
        this.model.userTurn(m);

        // check if game over or win
        GameState state =this.model.gameOver();
        if(  state== GameState.TIE) {
        //    this.view.displayMessage("No More Turns");
            this.view.displayEndGame("TIE");
        }
        else if (state == GameState.PLAYER_WIN)
        {
         //   this.view.displayMessage("CONGRATS!! you won");
            this.view.displayEndGame("PLAYER WINS!");
        }
        // move to computer turn
        else {

            Move computerMove = this.model.computerTurn();
            if(computerMove!=null)
                this.view.markButton(computerMove.getRow(),computerMove.getCol(),computer);
            // check if game over or win
            state =this.model.gameOver();
            if(  state== GameState.TIE) {
            //    this.view.displayMessage("No More Turns");
                this.view.displayEndGame("TIE");
            }
            else if (state == GameState.COMPUTER_WIN)
            {
             //   this.view.displayMessage("Computer win....");
                this.view.displayEndGame("COMPUTER WINS");
            }
        }



    }


}
