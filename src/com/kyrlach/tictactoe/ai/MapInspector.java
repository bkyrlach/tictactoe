package com.kyrlach.tictactoe.ai;

import java.io.File;
import java.io.FileInputStream;

public class MapInspector {

    public static String pad(byte b) {
        String result = Integer.toString(b);
        int toPad = 4 - result.length();
        for(int i = 0; i < toPad; i++) {
            result = "0" + result;
        }
        return result;
    }

    public static void main(String[] args) {
        File f = new File("ai_data/turn_" + 4 + ".data");
        try {
            FileInputStream fis = new FileInputStream(f);
            int read;
            do {
                byte[] row = new byte[18];
                read = fis.read(row);
                if(read != -1) {
                    byte[] boardRow = new byte[3];
                    byte[] scoreRow = new byte[3];
                    for(int i = 0; i < 9; i++) {
                        boardRow[i % 3] = row[i];
                        scoreRow[i % 3] = row[i + 9];
                        if((i + 1) % 3 == 0) {
                            for(int j = 0; j < 3; j++) {
                                byte boardByte = boardRow[j];
                                switch(boardByte) {
                                    case 1:
                                        System.out.print('X');
                                        break;
                                    case 2:
                                        System.out.print('O');
                                        break;
                                    case 3:
                                        System.out.print('_');
                                }
                            }

                            System.out.print(' ');

                            for(int j = 0; j < 3; j++) {
                                byte scoreByte = scoreRow[j];
                                String output = pad(scoreByte);
                                System.out.print(output);
                                System.out.print(' ');
                            }
                            System.out.println();
                            boardRow = new byte[3];
                            scoreRow = new byte[3];
                        }
                    }
                }
                System.out.println();
            } while(read != -1);
            fis.close();
        } catch (Exception ex) {

        }
    }
}
