package com.kyrlach.tictactoe.ai;

import com.kyrlach.tictactoe.error.OutOfBoundsException;
import com.kyrlach.tictactoe.model.Board;
import com.kyrlach.tictactoe.model.GameResult;
import jdk.jshell.spi.ExecutionControl;

import java.util.Random;

public final class EasyComputer implements Computer {
    private final Random random;

    public EasyComputer() {
        this.random = new Random();
    }

    private int[] scoreMoves(Board board) throws OutOfBoundsException {
        int[] scores = new int[9];
        AIHelpers.scoreOccupied(board, scores);
        AIHelpers.scoreWins(board, scores);
        return scores;
    }

    @Override
    public Integer makeMove(Board board) throws OutOfBoundsException {
        int[] scores = scoreMoves(board);
        return AIHelpers.pickBest(scores, random);
    }

    @Override
    public void notify(GameResult result) {
        // I don't do anything.
    }
}
