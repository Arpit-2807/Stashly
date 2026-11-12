package com.arpit.stashly;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StashlyServer {

    private final int port;
    private final CommandHandler handler;
    private ServerSocket serverSocket;
    private volatile boolean running;

    public StashlyServer(int port, CommandHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    //Opes the port. Call this first
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
    }

    //The real port(useful when we asked for port 0)
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    //The door loop: wait for a client, hire a thread for it, go bac kto waiting
    public void run() {
        while(running) {
            try {
                Socket client = serverSocket.accept();
                new Thread(() -> handle(client)).start(); // One worker per client
            } catch (IOException e) {
                if(running) {
                    System.out.println("Accept failed: " + e.getMessage());
                }
            }
        }
    }

    public void stop() throws IOException {
        running = false;
        serverSocket.close(); // this wake up accept() so the loop can end
    }

    // This run inside the clients own Thread
    public void handle(Socket client) {
        String who = String.valueOf(client.getRemoteSocketAddress());
        System.out.println("Client connected: " + who);
        try(client) {
            InputStream in = new BufferedInputStream(client.getInputStream());
            OutputStream out = client.getOutputStream();
            List<String> command;
            while((command = RespParser.readCommand(in)) != null) {
                String reply = handler.run(command);
                out.write(reply.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("Client " + who + " problem: " + e.getMessage());
        }
        System.out.println("Client left: "+ who);
    }

}
