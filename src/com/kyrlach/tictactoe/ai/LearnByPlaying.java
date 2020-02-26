package com.kyrlach.tictactoe.ai;

import com.kyrlach.tictactoe.DummyInputOutput;
import com.kyrlach.tictactoe.Game;
import com.kyrlach.tictactoe.InputOutput;
import com.kyrlach.tictactoe.model.Piece;
import com.kyrlach.tictactoe.model.Player;

public class LearnByPlaying {
    public static void main(String[] args) {
        InputOutput inputOutput = new DummyInputOutput();

        for(int i = 0; i < 100000; i++) {
            LearningComputer2 xs = new LearningComputer2();
            xs.playAs(Piece.X);

            xs.load();

            Player os = new EasyComputer();
            os.playAs(Piece.O);

            Game game = new Game(inputOutput, xs, os);
            try {
                game.play();
                xs.save();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        inputOutput.close();
    }
}
