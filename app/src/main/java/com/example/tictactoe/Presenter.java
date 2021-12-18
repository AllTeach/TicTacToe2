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

        comm.createFbUser(email,password);
        comm.registerUser();

    }

    @Override
    public void userLogin(String email, String password) {
        comm.loginUser(email,password);

    }

    @Override
    public void startOrJoinGame() {
        comm.joinGame();
    }

    @Override
    public void firebaseResult(FirebaseComm.ResultType result, boolean success) {

        switch(result)
        {
            case REGISTER:
                this.view.displayMessage("Register success");
                break;
            case LOGIN:
                this.view.displayMessage("Login success");
                break;



        }
        Log.d(TAG, "firebaseResult: PResenter : "  + result + "," + success);
    }

    @Override
    public void firebaseGameInfo(FirebaseComm.ResultType result, boolean success, int row, int col) {
        switch(result) {
            case NO_PENDING_GAMES:
                // start a new game
                this.view.displayMessage("No Open Games, wait for another player");
                comm.createGame();
                break;

            case GAME_CREATED:
                this.view.displayMessage("No Open Games, wait for another player");
                break;

            case GAME_JOINED:
                // game has started,
                this.view.displayMessage("Joined game, wait for other move");
                break;

            case GAME_STARTED:
                // game has started,
                this.view.displayMessage("Game Started, please play your turn ");
                break;

        }
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
