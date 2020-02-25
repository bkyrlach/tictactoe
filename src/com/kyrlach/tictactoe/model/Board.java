package com.kyrlach.tictactoe.model;

import com.kyrlach.tictactoe.error.InvalidMoveException;
import com.kyrlach.tictactoe.error.OutOfBoundsException;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final List<Piece> board;

    public Board() {
        board = new ArrayList<Piece>();
        for(int i = 0; i < 9; i++) {
            board.add(Piece.SPACE);
        }
    }

    public void checkBounds(int square) throws OutOfBoundsException {
        if(square < 1 || square > 9) {
            throw new OutOfBoundsException(square);
        }
    }

    public void checkBounds(Coordinates coords) throws OutOfBoundsException {
        int x = coords.getX();
        int y = coords.getY();
        if(x < 1 || x > 3 || y < 1 || y > 3) {
            throw new OutOfBoundsException(coords);
        }
    }

    public Piece pieceAt(int square) throws OutOfBoundsException {
        checkBounds(square);
        return board.get(square - 1);
    }

    public Piece pieceAt(Coordinates coords) throws OutOfBoundsException {
        checkBounds(coords);
        return pieceAt(coords.toSquare());
    }

    public int checkAvailable() throws OutOfBoundsException {
        int count = 0;
        for(int i = 1; i <= 9; i++) {
            Piece piece = pieceAt(i);
            if(Piece.SPACE.equals(piece)) {
                count++;
            }
        }
        return count;
    }

    public void makeMove(Piece piece, int square) throws OutOfBoundsException, InvalidMoveException {
        checkBounds(square);
        Piece current = pieceAt(square);
        if(Piece.SPACE.equals(current)) {
            board.set(square - 1, piece);
        } else {
            throw new InvalidMoveException();
        }
    }

    public void makeMove(Piece piece, Coordinates coords) throws OutOfBoundsException, InvalidMoveException {
        checkBounds(coords);
        makeMove(piece, coords.toSquare());
    }

    public List<Piece> getRow(int rowNum) throws OutOfBoundsException {
        List<Piece> row = new ArrayList<Piece>();
        for(int col = 1; col <= 3; col++) {
            Coordinates coords = new Coordinates(col, rowNum);
            Piece piece = pieceAt(coords);
            row.add(piece);
        }
        return row;
    }

    public List<Piece> getCol(int colNum) throws OutOfBoundsException {
        List<Piece> col = new ArrayList<Piece>();
        for(int row = 1; row <= 3; row++) {
            Coordinates coords = new Coordinates(colNum, row);
            Piece piece = pieceAt(coords);
            col.add(piece);
        }
        return col;
    }

    public List<Piece> getDiag(int diag) throws OutOfBoundsException {
        List<Piece> result = new ArrayList<Piece>();
        switch(diag) {
            case 1:
                for(int i = 1; i <= 3; i++) {
                    Coordinates coords = new Coordinates(i, i);
                    Piece piece = pieceAt(coords);
                    result.add(piece);
                }
                break;
            case 2:
                for(int i = 1; i <= 3; i++) {
                    Coordinates coords = new Coordinates(i, 4 - i);
                    Piece piece = pieceAt(coords);
                    result.add(piece);
                }
                break;
            default:
                throw new RuntimeException(diag + " is not one of the available diagonals.");
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i <= 9; i++) {
            Piece piece;
            try {
                piece = pieceAt(i);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            switch(piece) {
                case X:
                    sb.append('X');
                    break;
                case O:
                    sb.append('O');
                    break;
                case SPACE:
                    sb.append(' ');
                    break;
            }
            if(i != 9) {
                if(i % 3 == 0) {
                    sb.append('\n');
                    sb.append("-+-+-\n");
                } else {
                    sb.append("|");
                }
            } else {
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
