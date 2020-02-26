package com.kyrlach.tictactoe.model;

import com.kyrlach.tictactoe.InputOutput;
import com.kyrlach.tictactoe.error.InvalidMoveException;
import com.kyrlach.tictactoe.error.OutOfBoundsException;

public class HumanPlayer implements Player {
    private final InputOutput inputOutput;
    private Piece playingAs;

    public HumanPlayer(InputOutput inputOutput) {
        this.inputOutput = inputOutput;
    }

    @Override
    public void makeMove(Board board) throws OutOfBoundsException {
        boolean done = false;
        while(!done) {
            try {
                String moveStr = inputOutput.prompt("Pick a space (1-9)? ");
                int move = Integer.parseInt(moveStr) ;
                board.makeMove(playingAs, move);
                done = true;
            } catch (NumberFormatException nfe) {
                inputOutput.println("Must enter a space (1-9).");
            } catch (InvalidMoveException ime) {
                inputOutput.println("That's not a valid move.");
            } catch (OutOfBoundsException oobe) {
                inputOutput.println("You must enter a number between one and nine.");
            }
        }
    }

    @Override
    public void playAs(Piece piece) {
        this.playingAs = piece;
    }

    @Override
    public void notify(GameResult result) {

    }
}
