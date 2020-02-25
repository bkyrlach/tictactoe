package com.kyrlach.tictactoe;

import com.kyrlach.tictactoe.ai.Computer;
import com.kyrlach.tictactoe.ai.EasyComputer;
import com.kyrlach.tictactoe.ai.LearningComputer;
import com.kyrlach.tictactoe.ai.MediumComputer;

public class Program {

    public static void main(String[] args) {
        InputOutput inputOutput = new InputOutput(System.in, System.out);
        Computer computer = new MediumComputer();
        Game game = new Game(inputOutput, computer);
        try {
            game.play();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        inputOutput.close();
    }
}
