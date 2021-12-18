package com.example.tictactoe;

public class Game
{
    private String nameOwner;
    private String nameOther;
    private String status;
    private int row;
    private int col;

    public Game(String nameOwner, String nameOther, String status, int row, int col, String currPlayer) {
        this.nameOwner = nameOwner;
        this.nameOther = nameOther;
        this.status = status;
        this.row = row;
        this.col = col;
        this.currPlayer = currPlayer;
    }

    private String currPlayer;

    public Game()
    {

    }

    public String getCurrPlayer() {
        return currPlayer;
    }

    public void setCurrPlayer(String currPlayer) {
        this.currPlayer = currPlayer;
    }

    public Game(String nameOwner, String nameOther, String status, int row, int col) {
        this.nameOwner = nameOwner;
        this.nameOther = nameOther;
        this.status = status;
        this.row = row;
        this.col = col;
    }

    public String getNameOwner() {
        return nameOwner;
    }

    public void setNameOwner(String nameOwner) {
        this.nameOwner = nameOwner;
    }

    public String getNameOther() {
        return nameOther;
    }

    public void setNameOther(String nameOther) {
        this.nameOther = nameOther;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
