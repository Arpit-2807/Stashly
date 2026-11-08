package com.arpit.stashly;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RespWriterTest {

    @Test
    void simpleReply() {
        assertEquals("+PONG\r\n" , RespWriter.simple("PONG"));
    }

    @Test
    void errorReply() {
        assertEquals("-ERR oops\r\n", RespWriter.error("oops"));
    }

    @Test
    void numberReply() {
        assertEquals(":5\r\n", RespWriter.integer(5));
    }

    @Test
    void bulkReply() {
        assertEquals("$5\r\nhello\r\n", RespWriter.bulk("hello"));
    }

    @Test
    void bulkReplyForNull() {
        assertEquals("$-1\r\n", RespWriter.bulk(null));
    }

}
