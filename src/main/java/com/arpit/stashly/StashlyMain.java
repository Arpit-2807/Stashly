package com.arpit.stashly;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;


public class StashlyMain {

    public static void main(String[] args) throws IOException {
        int port = 6380;

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Stashly is listening on port " + port);

            while (true) {
                Socket client = server.accept();
                System.out.println("A client is connected");
                handle(client);
            }
        }
    }

    private static void handle(Socket client) throws IOException {
        try (client) {
            InputStream in = new BufferedInputStream(client.getInputStream());
            List<String> command;
            while ((command = RespParser.readCommand(in)) != null) {
                System.out.println("Command: " + command);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Client left");
    }
}
