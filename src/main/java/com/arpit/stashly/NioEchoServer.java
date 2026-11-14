package com.arpit.stashly;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioEchoServer {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(6381));
        server.configureBlocking(false);                       // never wait
        server.register(selector, SelectionKey.OP_ACCEPT);     // tell me about new guests
        System.out.println("NIO echo server on port 6381, thread: "
                + Thread.currentThread().getName());

        while (true) {
            selector.select();                                 // sleep until someone is ready
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();                                   // we are handling this one now
                try {
                    if (key.isAcceptable()) {
                        accept(server, selector);
                    } else if (key.isReadable()) {
                        echo(key);
                    }
                } catch (IOException e) {
                    close(key);
                }
            }
        }
    }

    private static void accept(ServerSocketChannel server, Selector selector) throws IOException {
        SocketChannel client = server.accept();
        if (client == null) {
            return;
        }
        client.configureBlocking(false);
        // each guest gets their own tray, stored as the "attachment" on the key
        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        System.out.println("Client connected: " + client.getRemoteAddress()
                + " (thread: " + Thread.currentThread().getName() + ")");
    }

    private static void echo(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();

        int count = client.read(buffer);                       // bytes go onto the tray
        if (count == -1) {                                     // the client left
            close(key);
            return;
        }
        buffer.flip();                                         // now take bytes off the tray
        while (buffer.hasRemaining()) {
            client.write(buffer);                              // send them back
        }
        buffer.clear();                                        // empty tray for next time
        System.out.println("Echoed " + count + " bytes (thread: "
                + Thread.currentThread().getName() + ")");
    }

    private static void close(SelectionKey key) {
        try {
            key.channel().close();
        } catch (IOException ignored) {
        }
        key.cancel();
        System.out.println("Client left");
    }
}