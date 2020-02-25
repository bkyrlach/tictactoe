package com.kyrlach.tictactoe.ai;

import com.kyrlach.tictactoe.error.OutOfBoundsException;
import com.kyrlach.tictactoe.model.Board;
import com.kyrlach.tictactoe.model.GameResult;
import com.kyrlach.tictactoe.model.Piece;

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

public class LearningComputer implements Computer {

    private final Random random;
    private int turn;
    private List<History> previousMoves;

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

    @Override
    public Integer makeMove(Board board) throws OutOfBoundsException {
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
                            scores = oldScores;
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
            for(int i = 0; i < 9; i++) {
                scores[i] = (byte)newScores[i];
            }
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

        Integer move = AIHelpers.pickBest(scoreInts, random);
        previousMoves.add(new History(gameBoardState, move));
        turn++;
        Piece currentOccupant = board.pieceAt(move);
        if(!Piece.SPACE.equals(currentOccupant)) {
            System.out.println("oops.");
        }
        return move;
    }

    private void updateLoss() {
        for(int i = 0; i < turn; i++) {
            History h = previousMoves.get(i);
            Path p = FileSystems.getDefault().getPath("ai_date", "turn_" + i + ".data");
            try {
                FileChannel fc = FileChannel.open(p, StandardOpenOption.READ, StandardOpenOption.WRITE);
                ByteBuffer buffer = ByteBuffer.allocate(18);
                int read;
                int pos = 0;
                do {
                    read = fc.read(buffer);

                    byte[] boardState = new byte[9];
                    buffer.get(boardState, 0, 9);
                    byte[] scores = new byte[9];
                    buffer.get(scores, 9, 9);
                    if(compareStates(boardState, h.getBoardState())) {
                        scores[h.getMove() - 1] = (byte)(scores[h.getMove() - 1] - 1);
                        ByteBuffer newScores = ByteBuffer.wrap(scores);
                        fc.write(newScores, pos + 9);
                        break;
                    }
                    pos += read;
                } while(read != -1);
                fc.close();
            } catch (Exception ex) {

            }
        }
    }

    private void updateWin() {
        for(int i = 0; i < turn; i++) {
            History h = previousMoves.get(i);
            Path p = FileSystems.getDefault().getPath("ai_date", "turn_" + i + ".data");
            try {
                FileChannel fc = FileChannel.open(p, StandardOpenOption.READ, StandardOpenOption.WRITE);
                ByteBuffer buffer = ByteBuffer.allocate(18);
                int read;
                int pos = 0;
                do {
                    read = fc.read(buffer);

                    byte[] boardState = new byte[9];
                    buffer.get(boardState, 0, 9);
                    byte[] scores = new byte[9];
                    buffer.get(scores, 9, 9);
                    if(compareStates(boardState, h.getBoardState())) {
                        scores[h.getMove() - 1] = (byte)(scores[h.getMove() - 1] + 2);
                        ByteBuffer newScores = ByteBuffer.wrap(scores);
                        fc.write(newScores, pos + 9);
                        break;
                    }
                    pos += read;
                } while(read != -1);
                fc.close();
            } catch (Exception ex) {

            }
        }
    }

    private void updateTie() {
        for(int i = 0; i < turn; i++) {
            History h = previousMoves.get(i);
            Path p = FileSystems.getDefault().getPath("ai_date", "turn_" + i + ".data");
            try {
                FileChannel fc = FileChannel.open(p, StandardOpenOption.READ, StandardOpenOption.WRITE);
                ByteBuffer buffer = ByteBuffer.allocate(18);
                int read;
                int pos = 0;
                do {
                    read = fc.read(buffer);

                    byte[] boardState = new byte[9];
                    buffer.get(boardState, 0, 9);
                    byte[] scores = new byte[9];
                    buffer.get(scores, 9, 9);
                    if(compareStates(boardState, h.getBoardState())) {
                        scores[h.getMove() - 1] = (byte)(scores[h.getMove() - 1] + 1);
                        ByteBuffer newScores = ByteBuffer.wrap(scores);
                        fc.write(newScores, pos + 9);
                        break;
                    }
                    pos += read;
                } while(read != -1);
                fc.close();
            } catch (Exception ex) {

            }
        }
    }

    @Override
    public void notify(GameResult result) {
        switch(result) {
            case X_WINS:
                updateWin();
                break;
            case O_WINS:
                updateLoss();
                break;
            case CATS_GAME:
                updateTie();
                break;
        }
    }
}
