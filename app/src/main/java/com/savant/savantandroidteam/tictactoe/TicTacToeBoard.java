package com.savant.savantandroidteam.tictactoe;

public class TicTacToeBoard {

    private int size;
    private String host, opp, winner;
    private String turn;
    private String boardState;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getOpp() {
        return opp;
    }

    public void setOpp(String opp) {
        this.opp = opp;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }

    public void printBoard(){
        System.out.println("----------------");
        System.out.println("Host: " + host);
        System.out.println("Opp: " + opp);
        System.out.println("State: " + host);
        System.out.println("Winner: " + host);
        System.out.println("Turn: " + turn);
        System.out.println("----------------");



    }
}
