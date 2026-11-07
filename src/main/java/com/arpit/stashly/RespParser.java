package com.arpit.stashly;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RespParser {

    public static List<String> readCommand(InputStream in) throws Exception {
        String first = readLine(in);
        if(first == null) return null;
        if(!first.startsWith("*")) {
            throw new IOException("Expected '*' but got:" + first);
        }
        int count = Integer.parseInt(first.substring(1));

        List<String> parts = new ArrayList<>();
        for(int i=0; i<count; i++){
            String lengthLine = readLine(in);
            if(lengthLine == null || !lengthLine.startsWith("$")) {
                throw new IOException("Expected '$' but got:" + lengthLine);
            }
            int length = Integer.parseInt(lengthLine.substring(1));
            byte[] data = in.readNBytes(length);
            in.readNBytes(2);
            parts.add(new String(data, StandardCharsets.UTF_8));
        }
        return parts;
    }

    private static String readLine(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int b;
        while((b = in.read()) != -1) {
            if(b == '\r') {
                in.read();
                return sb.toString();
            }
            sb.append((char) b);
        }
        return null;
    }
}
