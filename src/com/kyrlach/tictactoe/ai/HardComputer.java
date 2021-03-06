package com.kyrlach.tictactoe.ai;

import com.kyrlach.tictactoe.error.InvalidMoveException;
import com.kyrlach.tictactoe.error.OutOfBoundsException;
import com.kyrlach.tictactoe.model.Board;
import com.kyrlach.tictactoe.model.GameResult;
import com.kyrlach.tictactoe.model.Piece;
import com.kyrlach.tictactoe.model.Player;

import java.util.Random;

public final class HardComputer implements Player {

    private final Random random;
    private Piece playingAs;

    public HardComputer() {
        this.random = new Random();
    }

    private int[] scoreMoves(Board board) throws OutOfBoundsException {
        // Seed initial scores with biases
        int[] scores = { 1, 0, 1, 0, 2, 0, 1, 0, 1};
        AIHelpers.scoreOccupied(board, scores);
        AIHelpers.scorePossibleTwos(board, scores, playingAs);
        AIHelpers.scoreBlocks(board, scores, playingAs);
        AIHelpers.scoreWins(board, scores, playingAs);
        return scores;
    }

    @Override
    public void makeMove(Board board) throws InvalidMoveException, OutOfBoundsException {
        int[] scores = scoreMoves(board);
        int move = AIHelpers.pickBest(scores, random);
        board.makeMove(playingAs, move);
    }

    @Override
    public void playAs(Piece piece) {
        playingAs = piece;
    }

    @Override
    public void notify(GameResult result) {
        // I don't do anything.
    }
}
