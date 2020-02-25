package com.kyrlach.tictactoe;

import com.kyrlach.tictactoe.ai.Computer;
import com.kyrlach.tictactoe.error.InvalidMoveException;
import com.kyrlach.tictactoe.error.OutOfBoundsException;
import com.kyrlach.tictactoe.model.*;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final InputOutput inputOutput;
    private final Computer computer;
    private final Board board;

    public Game(InputOutput inputOutput, Computer computer) {
        this.computer = computer;
        this.inputOutput = inputOutput;
        this.board = new Board();
    }

    private GameResult whoWon(CheckResult cr) {
        if(cr.getXCount() == 3) {
            return GameResult.X_WINS;
        } else if (cr.getOCount() == 3) {
            return GameResult.O_WINS;
        } else {
            return GameResult.CATS_GAME;
        }
    }

    private CheckResult check(ToCheck toCheck, int num) throws OutOfBoundsException {
        List<Piece> slice;
        switch(toCheck) {
            case ROW:
                slice = board.getRow(num);
                break;
            case COL:
                slice = board.getCol(num);
                break;
            case DIAG:
                slice = board.getDiag(num);
                break;
            default:
                throw new RuntimeException("Can't check this out.");
        }
        return new CheckResult(slice);
    }

    private boolean isGameWonOrTied() throws OutOfBoundsException {
        boolean gameWonOrTied;
        GameResult maybeWinner = determineWinner();
        if (GameResult.CATS_GAME.equals(maybeWinner)) {
            gameWonOrTied = board.checkAvailable() == 0;
        } else {
            gameWonOrTied = true;
        }
        return gameWonOrTied;
    }

    private GameResult determineWinner() throws OutOfBoundsException {
        GameResult winner = GameResult.CATS_GAME;
        for(int i = 1; i <= 3 && GameResult.CATS_GAME.equals(winner); i++) {
            winner = whoWon(check(ToCheck.ROW, i));
        }
        for(int i = 1; i <= 3 && GameResult.CATS_GAME.equals(winner); i++) {
            winner = whoWon(check(ToCheck.COL, i));
        }
        for(int i = 1; i <= 2 && GameResult.CATS_GAME.equals(winner); i++) {
            winner = whoWon(check(ToCheck.DIAG, i));
        }
        return winner;
    }

    private void humanMove() {
        boolean done = false;
        while(!done) {
            try {
                String moveStr = inputOutput.prompt("Pick a space (1-9)? ");
                int move = Integer.parseInt(moveStr) ;
                board.makeMove(Piece.O, move);
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

    private void computerMove() throws OutOfBoundsException, InvalidMoveException {
        Integer move = computer.makeMove(board);
        board.makeMove(Piece.X, move);
    }

    private void handleMove(Piece piece) throws InvalidMoveException, OutOfBoundsException {
        switch(piece) {
            case O:
                humanMove();
                break;
            case X:
                computerMove();
                break;
        }
    }

    public void play() throws InvalidMoveException, OutOfBoundsException {
        boolean gameOver = false;
        Piece currentMove = Piece.X;
        do {
            handleMove(currentMove);
            inputOutput.println(board.toString());
            inputOutput.println();
            gameOver = isGameWonOrTied();
            if(!gameOver) {
                if(Piece.X.equals(currentMove)) {
                    currentMove = Piece.O;
                } else {
                    currentMove = Piece.X;
                }
            }
        } while (!gameOver);

        GameResult result = determineWinner();

        computer.notify(result);

        switch(result) {
            case X_WINS:
                inputOutput.println("I won! Better luck next time.");
                break;
            case O_WINS:
                inputOutput.println("You won! Great job!");
                break;
            case CATS_GAME:
                inputOutput.println("Looks like a draw. I'm sure you'll get me next time.");
                break;
        }
    }
}
