package com.arpit.stashly;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class StashlyMain {

    public static void main(String[] args) throws IOException {
        int port = 6380;
        StashlyServer server = new StashlyServer(port, new CommandHandler(new Store()));
        server.start();
        System.out.println("Stashly is listening on port " + port);
        server.run();
    }
}
