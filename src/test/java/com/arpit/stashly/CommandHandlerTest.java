package com.arpit.stashly;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandHandlerTest {

    private final CommandHandler handler = new CommandHandler(new Store());

    @Test
    void pingGivesPong() {
        assertEquals("+PONG\r\n", handler.run(List.of("PING")));
    }

    @Test
    void lowercaseWorksToo() {
        assertEquals("+PONG\r\n", handler.run(List.of("ping")));
    }

    @Test
    void echoGivesBackTheWord() {
        assertEquals("$5\r\nhello\r\n", handler.run(List.of("ECHO", "hello")));
    }

    @Test
    void echoWithoutWordIsAnError() {
        assertEquals("-ERR wrong number of arguments for 'echo' command\r\n", handler.run(List.of("ECHO")));
    }

    @Test
    void unknownCommandIsAnError() {
        assertEquals("-ERR unknown command 'HELLO'\r\n", handler.run(List.of("HELLO")));
    }

    @Test
    void setReturnOk() {
        assertEquals("+OK\r\n", handler.run(List.of("SET", "name", "arpit")));
    }

    @Test
    void setThenGetReturnsTheValue() {
        handler.run(List.of("SET", "name", "arpit"));
        assertEquals("$5\r\narpit\r\n", handler.run(List.of("GET", "name")));
    }

    @Test
    void getMissingKeyReturnsNull() {
        assertEquals("$-1\r\n", handler.run(List.of("GET", "missing")));
    }

    @Test
    void delReturnsHowManyWereDeleted() {
        handler.run(List.of("SET", "a", "1"));
        assertEquals(":1\r\n", handler.run(List.of("EXISTS", "a", "zzz")));
    }

    @Test
    void setWrongArgumentsIsAnError() {
        assertEquals("-ERR wrong number of arguments for 'set' command\r\n", handler.run(List.of("SET", "onlykey")));
    }

    @Test
    void incrCountsUp() {
        assertEquals(":1\r\n", handler.run(List.of("INCR", "visits")));
        assertEquals(":2\r\n", handler.run(List.of("INCR", "visits")));
    }

    @Test
    void decrCountsDown() {
        handler.run(List.of("SET", "n", "10"));
        assertEquals(":9\r\n", handler.run(List.of("DECR", "n")));
    }

    @Test
    void incrOnTextIsAnError() {
        handler.run(List.of("SET", "name", "arpit"));
        assertEquals("-ERR value is not an integer or out of range\r\n", handler.run(List.of("INCR", "name")));
    }

    @Test
    void incrWithoutKeyIsAnError() {
        assertEquals("-ERR wrong number of arguments for 'incr' command\r\n", handler.run(List.of("INCR")));
    }

    @Test
    void dbSizeCountsKeys() {
        handler.run(List.of("SET", "a", "b"));
        handler.run(List.of("SET", "b", "2"));
        assertEquals(":2\r\n", handler.run(List.of("DBSIZE")));
    }

    @Test
    void keysListEveryting() {
        handler.run(List.of("SET", "b", "2"));
        handler.run(List.of("SET", "a", "1"));
        assertEquals(":2\r\n$1\r\na\r\n$1\r\nb\r\b\n", handler.run(List.of("KEYS", "*")));
    }

    @Test
    void flushallEmptiesStore() {
        handler.run(List.of("SET", "a", "1"));
        assertEquals("+OK\r\n", handler.run(List.of("FLUSHALL")));
        assertEquals(":0\r\n", handler.run(List.of("DBSIZE")));
    }

}
