package com.kyrlach.tictactoe;

import com.kyrlach.tictactoe.ai.*;
import com.kyrlach.tictactoe.model.HumanPlayer;
import com.kyrlach.tictactoe.model.Piece;
import com.kyrlach.tictactoe.model.Player;

public class Program {

    public static void main(String[] args) {
        InputOutput inputOutput = new ConsoleInputOutput(System.in, System.out);

        LearningComputer2 computerPlayer = new LearningComputer2();
        computerPlayer.playAs(Piece.X);
        computerPlayer.load();

        Player humanPlayer = new HumanPlayer(inputOutput);
        humanPlayer.playAs(Piece.O);

        Game game = new Game(inputOutput, computerPlayer, humanPlayer);
        try {
            game.play();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        inputOutput.close();
    }
}
