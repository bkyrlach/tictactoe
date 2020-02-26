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
        int highScore = Byte.MIN_VALUE;
        for(int i = 0; i < 9; i++) {
            int moveScore = scores[i];
            if(moveScore > highScore) {
                highScore = moveScore;
            }
        }
        return highScore;
    }

    public static List<Integer> highestScoringMoves(int[] scores, int highScore, int epsilon) {
        List<Integer> moves = new ArrayList<Integer>();
        for(int i = 0; i < 9; i++) {
            int score = scores[i];
            if(score >= (highScore - epsilon)) {
                moves.add(i + 1);
            }
        }
        return moves;
    }

    public static Integer pickBest(int[] scores, Random random) {
        return pickBest(scores, random, 0);
    }

    public static Integer pickBest(int[] scores, Random random, int epsilon) {
        int highScore = determineHighest(scores);
        List<Integer> moves = highestScoringMoves(scores, highScore, epsilon);
        int move = random.nextInt(moves.size());
        return moves.get(move);
    }

    private static boolean matchingPieceCount(CheckResult cr, Piece piece, int count) {
        boolean matches = false;
        switch(piece) {
            case X:
                matches = cr.getXCount() == count;
                break;
            case O:
                matches = cr.getOCount() == count;
                break;
            default:
                throw new RuntimeException("Can only count X's or O's.");
        }
        return matches;
    }

    public static void scoreWins(Board board, int[] scores, Piece playingAs) throws OutOfBoundsException {
        for(int y = 1; y <= 3; y++) {
            for(int x = 1; x <= 3; x++) {
                Coordinates coords = new Coordinates(x, y);
                boolean possibleWin = false;

                Piece piece = board.pieceAt(coords);
                if(Piece.SPACE.equals(piece)) {
                    CheckResult rowCheck = new CheckResult(board.getRow(y));
                    possibleWin = possibleWin || matchingPieceCount(rowCheck, playingAs, 2);

                    CheckResult colCheck = new CheckResult(board.getCol(x));
                    possibleWin = possibleWin || matchingPieceCount(colCheck, playingAs, 2);

                    if(isTLBRDiagonal(x, y)) {
                        CheckResult diag1Check = new CheckResult(board.getDiag(1));
                        possibleWin = possibleWin || matchingPieceCount(diag1Check, playingAs, 2);
                    }

                    if(isTRBLDiagonal(x,y)) {
                        CheckResult diag2check = new CheckResult(board.getDiag(2));
                        possibleWin = possibleWin || matchingPieceCount(diag2check, playingAs, 2);
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
            int sidx = i - 1;
            int currentScore = scores[sidx];
            Piece current = board.pieceAt(i);
            if(Piece.SPACE.equals(current)) {
                scores[sidx] = currentScore;
            } else {
                scores[sidx] = Byte.MIN_VALUE;
            }
        }
    }

    public static void scoreAdjacents(Board board, int[] scores, Piece playingAs) throws OutOfBoundsException {
        for(int i = 0; i < 9; i++) {
            int currentScore = scores[i];
            int currX = (i % 3) + 1;
            int currY = (i / 3) + 1;
            Coordinates currentCoords = new Coordinates(currX, currY);
            Piece current = board.pieceAt(currentCoords);
            if(Piece.SPACE.equals(current)) {
                CheckResult cr;
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
                int toAdd = 0;
                switch(playingAs) {
                    case X:
                        toAdd = cr.getXCount();
                        break;
                    case O:
                        toAdd = cr.getOCount();
                        break;
                }
                scores[i] = currentScore + toAdd;
            }
        }
    }

    private static Piece opponentIs(Piece playingAs) {
        Piece opponentsPiece = Piece.SPACE;
        switch(playingAs) {
            case X:
                opponentsPiece = Piece.O;
                break;
            case O:
                opponentsPiece = Piece.X;
                break;
            default:
                throw new RuntimeException("Can't get here.");
        }
        return opponentsPiece;
    }

    public static void scorePossibleTwos(Board board, int[] scores, Piece playingAs) throws OutOfBoundsException {
        Piece opponentsPiece = opponentIs(playingAs);
        for(int y = 1; y <= 3; y++) {
            for(int x = 1; x <= 3; x++) {
                Coordinates coords = new Coordinates(x, y);
                int sidx = coords.toSquare() - 1;
                int currentScore = scores[sidx];
                Piece piece = board.pieceAt(coords);
                if(Piece.SPACE.equals(piece)) {
                    CheckResult rowCheck = new CheckResult(board.getRow(y));
                    if(matchingPieceCount(rowCheck, playingAs, 1) && matchingPieceCount(rowCheck, opponentsPiece, 0)) {
                        currentScore++;
                    }

                    CheckResult colCheck = new CheckResult(board.getCol(x));
                    if(matchingPieceCount(colCheck, playingAs, 1) && matchingPieceCount(colCheck, opponentsPiece, 0)) {
                        currentScore++;
                    }

                    if(isTLBRDiagonal(x, y)) {
                        CheckResult diag1Check = new CheckResult(board.getDiag(1));
                        if(matchingPieceCount(diag1Check, playingAs, 1) && matchingPieceCount(diag1Check, opponentsPiece, 0)) {
                            currentScore++;
                        }
                    }

                    if(isTRBLDiagonal(x,y)) {
                        CheckResult diag2check = new CheckResult(board.getDiag(2));
                        if(matchingPieceCount(diag2check, playingAs, 1) && matchingPieceCount(diag2check, opponentsPiece, 0)) {
                            currentScore++;
                        }
                    }
                }
                scores[sidx] = currentScore;
            }
        }
    }

    public static void scoreBlocks(Board board, int[] scores, Piece playingAs) throws OutOfBoundsException {
        Piece opponentsPiece = opponentIs(playingAs);
        for(int y = 1; y <= 3; y++) {
            for(int x = 1; x <= 3; x++) {
                Coordinates coords = new Coordinates(x, y);
                int sidx = coords.toSquare() - 1;
                boolean needsBlocked = false;
                Piece piece = board.pieceAt(coords);
                if(Piece.SPACE.equals(piece)) {
                    CheckResult rowCheck = new CheckResult(board.getRow(y));
                    needsBlocked = needsBlocked || matchingPieceCount(rowCheck, opponentsPiece, 2);

                    CheckResult colCheck = new CheckResult(board.getCol(x));
                    needsBlocked = needsBlocked || matchingPieceCount(colCheck, opponentsPiece, 2);

                    if(isTLBRDiagonal(x, y)) {
                        CheckResult diag1Check = new CheckResult(board.getDiag(1));
                        needsBlocked = needsBlocked || matchingPieceCount(diag1Check, opponentsPiece, 2);
                    }

                    if(isTRBLDiagonal(x,y)) {
                        CheckResult diag2check = new CheckResult(board.getDiag(2));
                        needsBlocked = needsBlocked || matchingPieceCount(diag2check, opponentsPiece, 2);
                    }
                }
                if(needsBlocked) {
                    scores[sidx] = Byte.MAX_VALUE - 1;
                }
            }
        }
    }
}
