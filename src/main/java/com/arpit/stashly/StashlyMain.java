package com.arpit.stashly;

import java.io.IOException;

public class StashlyMain {

    public static void main(String[] args) throws IOException {
        int port = 6380;
        StashlyServer server = new StashlyServer(port, new CommandHandler(new Store()));
        server.start();
        System.out.println("Stashly is listening on port " + port);
        server.run();
    }
}
