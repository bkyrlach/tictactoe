package com.kyrlach.tictactoe.ai;

import com.kyrlach.tictactoe.error.InvalidMoveException;
import com.kyrlach.tictactoe.error.OutOfBoundsException;
import com.kyrlach.tictactoe.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LearningComputer2 implements Player {
    private final Random random;
    private Piece playingAs;
    private final List<List<Piece>> history;
    private final List<List<Piece>> boardStates;
    private final List<Segment> paths;
    private int turn = 0;

    public LearningComputer2() {
        random = new Random();
        boardStates = new ArrayList<List<Piece>>();
        history = new ArrayList<List<Piece>>();
        paths = new ArrayList<Segment>();
    }

    private boolean compareState(List<Piece> stateA, List<Piece> stateB) {
        boolean same = true;
        for(int i = 0; i < 9; i++) {
            Piece pieceA = stateA.get(i);
            Piece pieceB = stateB.get(i);
            same = same && pieceA.equals(pieceB);
        }
        return same;
    }

    private int indexOf(List<Piece> boardState) {
        int idx = -1;
        for(int i = 0; i < boardStates.size(); i++) {
            List<Piece> otherState = boardStates.get(i);
            if(compareState(boardState, otherState)) {
                idx = i;
                break;
            }
        }
        return idx;
    }

    private Segment getSegmentFor(int startIndex, int endIndex) {
        Segment retval = null;
        for(int i = 0; i < paths.size(); i++) {
            Segment current = paths.get(i);
            if(current.getStartState() == startIndex && current.getEndState() == endIndex) {
                retval = current;
                break;
            }
        }
        return retval;
    }

    private List<Segment> findPath() {
        List<Segment> myPath = new ArrayList<Segment>();
        for(int i = 0; i < history.size() - 1; i++) {
            List<Piece> startState = history.get(i);
            List<Piece> endState = history.get(i + 1);
            int sidx = indexOf(startState);
            int eidx = indexOf(endState);
            Segment pathPart = getSegmentFor(sidx, eidx);
            myPath.add(pathPart);
        }
        return myPath;
    }

    private List<Segment> findPotentialPath(int startState) {
        List<Segment> possibilities = new ArrayList<Segment>();
        for(int i = 0; i < paths.size(); i++) {
            Segment current = paths.get(i);
            if(current.getStartState() == startState) {
                possibilities.add(current);
            }
        }
        return possibilities;
    }

    private int computedWeight(List<Segment> path) {
        int weight = 0;
        for(int i = 0; i < path.size(); i++) {
            Segment current = path.get(i);
            weight = weight + current.getWeight();
        }
        return weight;
    }

    private int calculateMove(List<Piece> currentState, List<Piece> futureState) {
        int move = 0;
        for(int i = 0; i < 9; i++) {
            Piece current = currentState.get(i);
            Piece future = futureState.get(i);
            if(Piece.SPACE.equals(current) && playingAs.equals(future)) {
                move = i + 1;
                break;
            }
        }
        return move;
    }

    private List<Integer> calculateOtherMoves(List<Piece> currentState, List<WeightedMove> potentialMoves) {
        List<Integer> otherMoves = new ArrayList<Integer>();
        for(int i = 0; i < 9; i++) {
            boolean alreadyConsidered = false;
            for(int j = 0; j < potentialMoves.size(); j++) {
                WeightedMove move = potentialMoves.get(j);
                if(move.getSquare() == (i + 1)) {
                    alreadyConsidered = true;
                }
            }
            if(!alreadyConsidered) {
                Piece current = currentState.get(i);
                if(Piece.SPACE.equals(current)) {
                    otherMoves.add(i + 1);
                    break;
                }
            }
        }
        return otherMoves;
    }

    private int highestScoring(List<WeightedMove> moves) {
        int score = Integer.MIN_VALUE;
        for(int i = 0; i < moves.size(); i++) {
            WeightedMove move = moves.get(i);
            if(score < move.getWeight()) {
                score = move.getWeight();
            }
        }
        return score;
    }

    private WeightedMove pickMove(List<WeightedMove> moves, int score, int epsilon) {
        List<WeightedMove> narrowed = new ArrayList<WeightedMove>();
        for(int i = 0; i < moves.size(); i++) {
            WeightedMove move = moves.get(i);
            int compareTo = score == Integer.MIN_VALUE ? Integer.MIN_VALUE : (score - epsilon);
            if(move.getWeight() >= compareTo) {
                narrowed.add(move);
            }
        }
        if(narrowed.size() == 0) {
            System.out.println("oops");
        }
        int moveIdx = random.nextInt(narrowed.size());
        return narrowed.get(moveIdx);
    }

    @Override
    public void makeMove(Board board) throws InvalidMoveException, OutOfBoundsException {
        List<Piece> boardState = board.getBoard();
        history.add(boardState);
        int stateIdx = indexOf(boardState);
        if(stateIdx == -1) {
            List<Integer> possibleMoves = new ArrayList<Integer>();
            for(int y = 1; y <= 3; y++) {
                for(int x = 1; x <= 3; x++) {
                    Coordinates coord = new Coordinates(x, y);
                    Piece piece = board.pieceAt(coord);
                    if(Piece.SPACE.equals(piece)) {
                        possibleMoves.add(coord.toSquare());
                    }
                }
            }
            int rng = random.nextInt(possibleMoves.size());
            int move = possibleMoves.get(rng);
            board.makeMove(playingAs, move);
        } else {
            List<Segment> potentials = findPotentialPath(stateIdx);
            List<WeightedMove> possibleMoves = new ArrayList<WeightedMove>();
            for(int i = 0; i < potentials.size(); i++) {
                Segment current = potentials.get(i);
                int eidx = current.getEndState();
                List<Piece> futureState = boardStates.get(eidx);
                int square = calculateMove(boardState, futureState);
                WeightedMove move = new WeightedMove(square, current.getWeight());
                possibleMoves.add(move);
            }
            int highScore = highestScoring(possibleMoves);

            List<Integer> otherMoves = calculateOtherMoves(boardState, possibleMoves);

            for(int i = 0; i < otherMoves.size(); i++) {
                int aMove = otherMoves.get(i);
                WeightedMove move = new WeightedMove(aMove, highScore);
                possibleMoves.add(move);
            }


            WeightedMove move = pickMove(possibleMoves, highScore, 25);

            board.makeMove(playingAs, move.getSquare());
        }
        turn++;
    }

    @Override
    public void playAs(Piece piece) {
        playingAs = piece;
    }

    public void load() {
        File pathFile = new File("ai_data/paths.txt");
        File stateFile = new File("ai_data/states.txt");
        try {
            if(!pathFile.exists()) {
                pathFile.createNewFile();
            }
            if(!stateFile.exists()) {
                stateFile.createNewFile();
            }

            BufferedReader pathInput = new BufferedReader(new FileReader(pathFile));
            String pathLine;
            do {
                pathLine = pathInput.readLine();
                if(pathLine != null) {
                    String[] parts = pathLine.split(",");
                    int sidx = Integer.parseInt(parts[0]);
                    int eidx = Integer.parseInt(parts[1]);
                    int weight = Integer.parseInt(parts[2]);
                    Segment s = new Segment(sidx, eidx, weight);
                    paths.add(s);
                }
            } while (pathLine != null);
            pathInput.close();

            BufferedReader stateInput = new BufferedReader(new FileReader(stateFile));
            String stateLine;
            do {
                stateLine = stateInput.readLine();
                List<Piece> state = new ArrayList<Piece>();
                if(stateLine != null) {
                    String[] parts = stateLine.split(",");
                    for(int i = 0; i < parts.length; i++) {
                        String s = parts[i];
                        Piece toAdd = Piece.SPACE;
                        if("X".equals(s)) {
                            toAdd = Piece.X;
                        } else if("O".equals(s)) {
                            toAdd = Piece.O;
                        }
                        state.add(toAdd);
                    }
                    boardStates.add(state);
                }
            } while (stateLine != null);
        } catch (Exception ex) {

        }
    }

    public void save() {
        File pathFile = new File("ai_data/paths.txt");
        File stateFile = new File("ai_data/states.txt");
        try {
            if (!pathFile.exists()) {
                pathFile.createNewFile();
            }
            if (!stateFile.exists()) {
                stateFile.createNewFile();
            }

            BufferedWriter pathWriter = new BufferedWriter(new FileWriter(pathFile));
            for(int i = 0; i < paths.size(); i++) {
                StringBuffer sb = new StringBuffer();
                Segment s = paths.get(i);
                sb.append(s.getStartState());
                sb.append(',');
                sb.append(s.getEndState());
                sb.append(',');
                sb.append(s.getWeight());
                sb.append('\n');
                pathWriter.write(sb.toString());
            }
            pathWriter.close();

            BufferedWriter stateWriter = new BufferedWriter(new FileWriter(stateFile));
            for(int i = 0; i < boardStates.size(); i++) {
                List<Piece> state = boardStates.get(i);
                StringBuffer sb = new StringBuffer();
                for(int j = 0; j < state.size(); j++) {
                    Piece current = state.get(j);
                    switch(current) {
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
                    if(j == state.size() - 1) {
                        sb.append('\n');
                    } else {
                        sb.append(',');
                    }
                }
                stateWriter.write(sb.toString());
            }
            stateWriter.close();
        } catch (Exception ex) {

        }
    }

    @Override
    public void notify(GameResult result) {
        for(int i = 0; i < history.size() - 1; i++) {
            List<Piece> previousState = history.get(i);
            List<Piece> nextState = history.get(i + 1);
            int sidx = indexOf(previousState);
            if(sidx == -1) {
                boardStates.add(previousState);
                sidx = boardStates.size() - 1;
            }
            int eidx = indexOf(nextState);
            if(eidx == -1) {
                boardStates.add(nextState);
                eidx = boardStates.size() - 1;
            }
            Segment s = getSegmentFor(sidx, eidx);
            if(s == null) {
                s = new Segment(sidx, eidx, 50);
                paths.add(s);
            }
            switch(result) {
                case X_WINS:
                    if(Piece.X.equals(playingAs)) {
                        s.modifyWeight(5);
                    } else {
                        s.modifyWeight(-5);
                    }
                    break;
                case O_WINS:
                    if(Piece.O.equals(playingAs)) {
                        s.modifyWeight(5);
                    } else {
                        s.modifyWeight(-5);
                    }
                    break;
            }
        }
    }
}
