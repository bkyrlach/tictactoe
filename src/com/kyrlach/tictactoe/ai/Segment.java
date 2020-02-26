package com.kyrlach.tictactoe.ai;

import java.util.ArrayList;
import java.util.List;

public class Segment {
    private int startState;
    private int endState;
    private int weight;

    public Segment(int startState, int endState, int weight) {
        this.endState = endState;
        this.startState = startState;
        this.weight = weight;
    }

    public int getStartState() {
        return startState;
    }

    public int getEndState() {
        return endState;
    }

    public int getWeight() {
        return weight;
    }

    public void modifyWeight(int delta) {
        this.weight += delta;
    }
}
