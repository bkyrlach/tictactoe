package com.kyrlach.tictactoe.error;

import com.kyrlach.tictactoe.model.Coordinates;

public class OutOfBoundsException extends Exception {
    public OutOfBoundsException(int square) {
        super(square + " is not a valid square on the board. (Must be 1-9).");
    }

    public OutOfBoundsException(Coordinates coords) {
        super(coords + " is not a valid location on the board.");
    }
}
