package com.kyrlach.tictactoe.model;

public class WeightedMove {
    private final int square;
    private final int weight;

    public WeightedMove(int square, int weight) {
        this.square = square;
        this.weight = weight;
    }

    public int getSquare() {
        return square;
    }

    public int getWeight() {
        return weight;
    }
}
