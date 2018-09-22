package com.github.tankist88.object2source;

public class TestUtil {
    public static String clearWhiteSpaces(String str) {
        return str
                .replace("\n","")
                .replace("\r","")
                .replace("\t","")
                .replace(" ", "");
    }
}
