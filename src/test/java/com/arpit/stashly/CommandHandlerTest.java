package com.arpit.stashly;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandHandlerTest {

    private final CommandHandler handler = new CommandHandler();

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
}
