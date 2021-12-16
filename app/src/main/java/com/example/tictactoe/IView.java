package com.example.tictactoe;

public interface IView
{
    void markButton(int i,int j,char letter);
    void displayMessage(String message);
    void clearBoard();
    void displayEndGame(String message);
}
