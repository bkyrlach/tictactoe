package com.kyrlach.tictactoe;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class InputOutput {
    private final Scanner scanner;
    private final PrintStream out;

    public InputOutput(final InputStream in, final PrintStream out) {
        this.scanner = new Scanner(in);
        this.out = out;
    }

    public void print(final String message) {
        out.print(message);
    }

    public void println() {
        out.println();
    }

    public void println(final String message) {
        out.println(message);
    }

    public String prompt(final String message) {
        print(message);
        return scanner.next();
    }

    public void close() {
        this.scanner.close();
    }
}
