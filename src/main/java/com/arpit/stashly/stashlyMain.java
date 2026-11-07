package com.arpit.stashly;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class stashlyMain {

    public static void main(String[] args) throws IOException {
        int port = 6380;

        try(ServerSocket server = new ServerSocket(port) ){
            System.out.println("Stashly is listening on port" + port);

            while(true){
                Socket client = server.accept();
                System.out.println("A client is connected");
                handle(client);
            }
        }
    }

    private static void handle(Socket client) throws IOException {
        try(client) {
            InputStream in = client.getInputStream();
            byte[] buffer = new byte[1024];
            int count;
            while((count = in.read(buffer)) != -1) {
                String text = new String(buffer, 0, count, StandardCharsets.UTF_8);
                System.out.println("Recieved" + text.replace("/r" , "//r").replace("/n" , "//n"));
            }
        }
        System.out.println("Client left");
    }

}
