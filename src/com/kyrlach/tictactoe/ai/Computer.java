package com.kyrlach.tictactoe.ai;

import com.kyrlach.tictactoe.error.OutOfBoundsException;
import com.kyrlach.tictactoe.model.Board;
import com.kyrlach.tictactoe.model.GameResult;

public interface Computer {
    Integer makeMove(Board board) throws OutOfBoundsException;
    void notify(GameResult result);
}
