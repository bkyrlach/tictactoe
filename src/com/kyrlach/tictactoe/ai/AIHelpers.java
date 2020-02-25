package com.kyrlach.tictactoe.ai;

import com.kyrlach.tictactoe.error.OutOfBoundsException;
import com.kyrlach.tictactoe.model.Board;
import com.kyrlach.tictactoe.model.CheckResult;
import com.kyrlach.tictactoe.model.Coordinates;
import com.kyrlach.tictactoe.model.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIHelpers {
    public static boolean isTLBRDiagonal(int x, int y) {
        return x == y;
    }

    public static boolean isTRBLDiagonal(int x, int y) {
        return x == (4 - y);
    }

    public static int determineHighest(int[] scores) {
        int highScore = -1;
        for(int i = 0; i < 9; i++) {
            int moveScore = scores[i];
            if(moveScore > highScore) {
                highScore = moveScore;
            }
        }
        return highScore;
    }

    public static List<Integer> highestScoringMoves(int[] scores, int highScore) {
        List<Integer> moves = new ArrayList<Integer>();
        for(int i = 0; i < 9; i++) {
            int score = scores[i];
            if(score == highScore) {
                moves.add(i + 1);
            }
        }
        return moves;
    }

    public static Integer pickBest(int[] scores, Random random) {
        int highScore = determineHighest(scores);
        List<Integer> moves = highestScoringMoves(scores, highScore);
        int move = random.nextInt(moves.size());
        return moves.get(move);
    }

    public static void scoreWins(Board board, int[] scores) throws OutOfBoundsException {
        for(int y = 1; y <= 3; y++) {
            for(int x = 1; x <= 3; x++) {
                Coordinates coords = new Coordinates(x, y);
                boolean possibleWin = false;

                Piece piece = board.pieceAt(coords);
                if(Piece.SPACE.equals(piece)) {
                    CheckResult rowCheck = new CheckResult(board.getRow(y));
                    possibleWin = possibleWin || rowCheck.getXCount() == 2;

                    CheckResult colCheck = new CheckResult(board.getCol(x));
                    possibleWin = possibleWin || colCheck.getXCount() == 2;

                    if(isTLBRDiagonal(x, y)) {
                        CheckResult diag1Check = new CheckResult(board.getDiag(1));
                        possibleWin = possibleWin || diag1Check.getXCount() == 2;
                    }

                    if(isTRBLDiagonal(x,y)) {
                        CheckResult diag2check = new CheckResult(board.getDiag(2));
                        possibleWin = possibleWin || diag2check.getXCount() == 2;
                    }
                }
                if(possibleWin) {
                    scores[coords.toSquare() - 1] = Byte.MAX_VALUE;
                }
            }
        }
    }

    public static void scoreOccupied(Board board, int[] scores) throws OutOfBoundsException {
        for(int i = 1; i <= 9; i++) {
            Piece current = board.pieceAt(i);
            if(Piece.SPACE.equals(current)) {
                scores[i - 1] = 0;
            } else {
                scores[i - 1] = Byte.MIN_VALUE;
            }
        }
    }

    public static void scoreAdjacents(Board board, int[] scores) {
        for(int i = 0; i < 9; i++) {
            int currentScore = scores[i];
            if(currentScore == 0) {
                CheckResult cr;
                int currX = (i % 3) + 1;
                int currY = (i / 3) + 1;
                List<Piece> area = new ArrayList<Piece>();
                for(int y = (currY - 1); y <= (currY + 1); y++) {
                    for(int x = (currX - 1); x <= (currX + 1); x++) {
                        Coordinates coords = new Coordinates(x, y);
                        try {
                            Piece piece = board.pieceAt(coords);
                            area.add(piece);
                        } catch (OutOfBoundsException oobe) {

                        }
                    }
                }
                cr = new CheckResult(area);
                scores[i] = cr.getXCount();
            }
        }
    }

}
