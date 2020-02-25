package com.kyrlach.tictactoe.ai;

public class History {
    private final byte[] boardState;
    private final int move;

    public History(byte[] boardState, int move) {
        this.boardState = boardState;
        this.move = move;
    }

    public byte[] getBoardState() {
        return boardState;
    }

    public int getMove() {
        return move;
    }
}
