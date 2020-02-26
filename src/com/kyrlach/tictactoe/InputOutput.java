package com.kyrlach.tictactoe;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public interface InputOutput {
    void print(final String message);
    void println();
    void println(final String message);
    String prompt(final String message);
    void close();
}
