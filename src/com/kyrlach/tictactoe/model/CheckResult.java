package com.kyrlach.tictactoe.model;

import java.util.List;

public class CheckResult {
    private int xCount = 0;
    private int oCount = 0;

    public CheckResult(List<Piece> slice) {
        for(int i = 0; i < slice.size(); i++) {
            Piece piece = slice.get(i);
            switch(piece) {
                case X:
                    xCount++;
                    break;
                case O:
                    oCount++;
                    break;
            }
        }
    }

    public int getXCount() {
        return xCount;
    }

    public int getOCount() {
        return oCount;
    }
}
