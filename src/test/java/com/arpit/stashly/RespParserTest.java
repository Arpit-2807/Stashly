package com.arpit.stashly;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java .util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RespParserTest {

    private ByteArrayInputStream stream(String text) {
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void parsesPing() throws Exception {
        List<String> cmd = RespParser.readCommand(stream("*1\r\n$4\r\nPING\r\n"));
        assertEquals(List.of("PING"), cmd);
    }

    @Test
    void parsesSet() throws Exception {
        String text = "*3\r\n$3\r\nSET\r\n$4\r\nname\r\n$5\r\narpit\r\n";
        assertEquals(List.of("SET" , "name" , "arpit"), RespParser.readCommand(stream(text)));
    }

    @Test
    void parsesTwoCommandsInARow() throws Exception {
        ByteArrayInputStream in = stream("*1\r\n$4\r\nPING\r\n*1\r\n$4\r\nPONG\r\n");
        assertEquals(List.of("PING"), RespParser.readCommand(in));
        assertEquals(List.of("PONG"), RespParser.readCommand(in));
    }

    @Test
    void returnNullWhenNothingToRead() throws Exception {
        assertNull(RespParser.readCommand(stream("")));
    }
}
