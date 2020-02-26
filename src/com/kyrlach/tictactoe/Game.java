package com.kyrlach.tictactoe;

import com.kyrlach.tictactoe.error.InvalidMoveException;
import com.kyrlach.tictactoe.error.OutOfBoundsException;
import com.kyrlach.tictactoe.model.*;

import java.util.List;

public class Game {
    private final InputOutput inputOutput;
    private final Board board;
    private final Player xs;
    private final Player os;

    public Game(InputOutput inputOutput, Player xs, Player os) {
        this.inputOutput = inputOutput;
        this.xs = xs;
        this.os = os;
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

    private void handleMove(Piece piece) throws InvalidMoveException, OutOfBoundsException {
        switch(piece) {
            case O:
                os.makeMove(board);
                break;
            case X:
                xs.makeMove(board);
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

        xs.notify(result);
        os.notify(result);

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
