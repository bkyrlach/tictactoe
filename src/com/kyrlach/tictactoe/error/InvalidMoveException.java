package com.kyrlach.tictactoe.error;

public class InvalidMoveException extends Exception {
    public InvalidMoveException() {
        super("This move is invalid.");
    }
}
