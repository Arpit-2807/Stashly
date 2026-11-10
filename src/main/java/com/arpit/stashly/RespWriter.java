package com.arpit.stashly;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RespWriter {

    //+OK\r\n -> short text
    public static String simple(String text) {
        return "+" + text + "\r\n";
    }

    // -ERR something went wrong\r\n
    public static String error(String message) {
        return "-ERR " + message + "\r\n";
    }

    // :5\r\n -> a number
    public static String integer(long number) {
        return ":" + number + "\r\n";
    }

    // $5\r\nhello\r\n -> text with its length. null means "nothing foud"
    public static String bulk(String text) {
        if(text == null) {
            return "$-1\r\n";
        }
        int length = text.getBytes(StandardCharsets.UTF_8).length;
        return "$" + length + "\r\n" + text + "\r\n";
    }

    // *2\r\n$1\r\na\r\n$1\r\bb\r\n -> a list of texts
    public static String array(List<String> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(items.size()).append("\r\n");
        for(String item : items) {
            sb.append(bulk(item));
        }
        return sb.toString();
    }
}
