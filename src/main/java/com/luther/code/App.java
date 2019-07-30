package com.luther.code;

import java.io.IOException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        DiffJson.diffJson(args[0], args[1], args[2]);
    }
}
