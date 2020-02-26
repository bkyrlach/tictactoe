package com.kyrlach.tictactoe.model;

import com.kyrlach.tictactoe.error.InvalidMoveException;
import com.kyrlach.tictactoe.error.OutOfBoundsException;

public interface Player {
    void makeMove(Board board) throws InvalidMoveException, OutOfBoundsException;
    void playAs(Piece piece);
    void notify(GameResult result);
}
