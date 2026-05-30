package io.github.mathter.memifydb.command;

public class Test {
    static void main() {
        long l = 256;

        System.out.println(l & 0xff);
        System.out.println((l & 0xff00) >> 8);
    }
}
