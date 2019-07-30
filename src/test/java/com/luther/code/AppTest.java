package com.luther.code;


import org.junit.Test;

import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void test() throws IOException {
        String pre = "D://workspace//diffJSON//src//main//source//";
        DiffJson.diffJson(pre + "a.json", pre + "b.json", pre + "result.txt", true);
    }
}
