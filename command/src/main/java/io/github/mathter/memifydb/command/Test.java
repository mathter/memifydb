package io.github.mathter.memifydb.command;

import org.apache.commons.lang3.RandomUtils;

public class Test {
    static void main() {
        long l = 256;

        System.out.println(l & 0xff);
        System.out.println((l & 0xff00) >> 8);
    }
}
