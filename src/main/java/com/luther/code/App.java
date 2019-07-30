package com.luther.code;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            return;
        }
        DiffJson.diffJson(args[0], args[1], args[2]);
    }
}
