package com.kyrlach.tictactoe.ai;

import com.kyrlach.tictactoe.error.InvalidMoveException;
import com.kyrlach.tictactoe.error.OutOfBoundsException;
import com.kyrlach.tictactoe.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LearningComputer implements Player {

    private final Random random;
    private int turn;
    private final List<History> previousMoves;
    private Piece playingAs;

    public LearningComputer() {
        this.random = new Random();
        turn = 0;
        previousMoves = new ArrayList<History>();
    }

    private byte[] boardToBytes(Board board) throws OutOfBoundsException {
        byte[] boardState = new byte[9];
        for(int i = 1; i <= 9; i++) {
            Piece currentPiece = board.pieceAt(i);
            switch(currentPiece) {
                case X:
                    boardState[i - 1] = 1;
                    break;
                case O:
                    boardState[i - 1] = 2;
                    break;
                case SPACE:
                    boardState[i - 1] = 3;
                    break;
            }
        }
        return boardState;
    }

    private boolean compareStates(byte[] fromFile, byte[] inGame) {
        boolean areIdentical = true;
        for(int i = 0; i < 9; i++) {
            byte fileByte = fromFile[i];
            byte gameByte = inGame[i];
            areIdentical = areIdentical && (fileByte == gameByte);
        }
        return areIdentical;
    }

    private File fileForTurn(int turn) {
        File turnFile = new File("ai_data/turn_" + turn + ".data");
        return turnFile;
    }

    private byte[] intsToBytes(int[] scores) {
        byte[] bytes = new byte[9];
        for(int i = 0; i < 9; i++) {
            bytes[i] = (byte)scores[i];
        }
        return bytes;
    }

    private int[] bytesToInts(byte[] scores) {
        int[] ints = new int[9];
        for(int i = 0; i < 9; i++) {
            ints[i] = scores[i];
        }
        return ints;
    }

    @Override
    public void makeMove(Board board) throws InvalidMoveException, OutOfBoundsException {
        byte[] scores = new byte[9];
        byte[] gameBoardState = boardToBytes(board);
        boolean foundScores = false;
        File turnFile = fileForTurn(turn);
        if(turnFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(turnFile);
                int read;
                do {
                    byte[] fileBoardState = new byte[9];
                    read = fis.read(fileBoardState);
                    if(read == 9) {
                        byte[] oldScores = new byte[9];
                        read = fis.read(oldScores);
                        if(compareStates(fileBoardState, gameBoardState)) {
                            foundScores = true;
                            int[] tempScores = bytesToInts(oldScores);
                            AIHelpers.scoreOccupied(board, tempScores);
                            scores = intsToBytes(tempScores);
                            break;
                        }
                    }
                } while (read != -1);
                fis.close();
            } catch (Exception ex) {

            }
        } else {
            try {
                turnFile.createNewFile();
            } catch (Exception ex) {

            }
        }
        if(!foundScores) {
            int[] newScores = new int[9];
            AIHelpers.scoreOccupied(board, newScores);
            scores = intsToBytes(newScores);
            try {
                FileOutputStream fos = new FileOutputStream(turnFile, true);
                fos.write(gameBoardState);
                fos.write(scores);
                fos.close();
            } catch (Exception ex) {

            }
        }

        int[] scoreInts = new int[9];
        for(int i = 0; i < 9; i++) {
            scoreInts[i] = scores[i];
        }

        Integer move = AIHelpers.pickBest(scoreInts, random, 12);
        previousMoves.add(new History(gameBoardState, move));
        turn++;

        Piece temp = board.pieceAt(move);
        if(!Piece.SPACE.equals(temp)) {
            boolean found = false;
            for(int y = 1; y <= 3 && !found; y++) {
                for(int x = 1; x <= 3 && !found; x++) {
                    Coordinates coords = new Coordinates(x, y);
                    Piece current = board.pieceAt(coords);
                    if(Piece.SPACE.equals(current)) {
                        move = coords.toSquare();
                        found = true;
                    }
                }
            }
        }

        board.makeMove(playingAs, move);
    }

    @Override
    public void playAs(Piece piece) {
        playingAs = piece;
    }

    @Override
    public void notify(GameResult result) {
        for(int i = 0; i < turn; i++) {
            History h = previousMoves.get(i);
            Path p = FileSystems.getDefault().getPath("ai_data", "turn_" + i + ".data");
            try {
                FileChannel fc = FileChannel.open(p, StandardOpenOption.READ, StandardOpenOption.WRITE);
                ByteBuffer buffer = ByteBuffer.allocate(18);
                int read;
                int pos = 0;
                do {
                    read = fc.read(buffer);
                    if(read == 18) {
                        buffer.rewind();

                        byte[] stupid = new byte[18];
                        buffer.get(stupid);

                        byte[] boardState = new byte[9];
                        byte[] scores = new byte[9];

                        System.arraycopy(stupid, 0, boardState, 0, 9);
                        System.arraycopy(stupid, 9, scores, 0, 9);

                        if (compareStates(boardState, h.getBoardState())) {
                            int sidx = h.getMove() - 1;
                            byte newScore = scores[sidx];
                            switch(result) {
                                case X_WINS:
                                    if(Piece.X.equals(playingAs)) {
                                        newScore = (byte)(newScore + (turn * 2));
                                    } else {
                                        newScore = (byte)(newScore + (turn * -1));
                                    }
                                    break;
                                case O_WINS:
                                    if(Piece.O.equals(playingAs)) {
                                        newScore = (byte)(newScore + (turn * 2));
                                    } else {
                                        newScore = (byte)(newScore + (turn * -1));
                                    }
                                    break;
                                case CATS_GAME:
                                    newScore = (byte)(newScore + turn);
                                    break;
                            }
                            scores[sidx] = newScore;
                            ByteBuffer newScores = ByteBuffer.wrap(scores);
                            fc.write(newScores, pos + 9);
                            break;
                        }
                        pos += read;
                        buffer.clear();
                    }
                } while(read == 18);
                fc.close();
            } catch (Exception ex) {

            }
        }

    }
}
