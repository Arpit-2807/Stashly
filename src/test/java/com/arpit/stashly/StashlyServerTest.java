package com.arpit.stashly;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StashlyServerTest {

    private StashlyServer server;
    private int port;

    @BeforeEach
    void startServer() throws Exception {
        server = new StashlyServer(0, new CommandHandler(new Store()));
        server.start();
        port = server.getPort();
        Thread door = new Thread(server::run);
        door.setDaemon(true);
        door.start();
    }

    @AfterEach
    void stopServer() throws Exception {
        server.stop();
    }

    // A tiny test client: connects, sends command, read reply lines
    private class Client implements AutoCloseable {
        private final Socket socket;
        private final BufferedReader in;

        Client() throws IOException {
            socket = new Socket("localhost", port);
            socket.setSoTimeout(3000); // never wait more than 3 second
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        }

        // sends one command in Resp and returns the FIRST line of the reply
        String ask(String... parts) throws IOException {
            StringBuilder sb = new StringBuilder("*" + parts.length + "\r\n");
            for (String p : parts) {
                sb.append("$").append(p.getBytes(StandardCharsets.UTF_8).length).append("\r\n").append(p).append("\r\n");
            }
            socket.getOutputStream().write(sb.toString().getBytes(StandardCharsets.UTF_8));
            socket.getOutputStream().flush();
            return in.readLine();
        }

        // Reads the next reply line( for replies that have two lines)
        String line() throws IOException {
            return in.readLine();
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }
    }

    @Test
    void secondClientIsServedWhileFirstClientIsConnectedAndSilent() throws Exception {
        try(Client quiet = new Client(); Client busy = new Client()) {
            // 'quiet' stays connected and says nothing, 'busy' must still get an answer.
            assertEquals("+PONG", busy.ask("PING"));
        }
    }

    @Test
    void clientsShareTheSameData() throws Exception {
        try (Client a = new Client(); Client b = new Client()) {
            assertEquals("+OK", a.ask("SET", "name", "arpit"));
            assertEquals("$5", b.ask("GET", "name"));
            assertEquals("arpit", b.line());
        }
    }

    @Test
    void manyClientsTogetherWithoutLosingAnyCount() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            futures.add(pool.submit(() -> {
                try (Client c = new Client()) {
                    for (int j = 0; j < 100; j++) {
                        c.ask("INCR", "counter");
                    }
                }
                return null;
            }));
        }
        for (Future<?> f : futures) {
            f.get();  // wait for each worker, and show an error
        }
        pool.shutdown();

        try (Client c = new Client()) {
            assertEquals("$4", c.ask("GET", "counter"));
            assertEquals("2000", c.line()); // 20 clients x 100 = 2000, none lost        }
        }
    }

}
