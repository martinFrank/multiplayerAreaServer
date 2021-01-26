package com.github.martinfrank.multiplayerserver.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptHandler{


    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptHandler.class);

    //FIXME into configuration
    private final ByteBuffer welcomeBuf = ByteBuffer.wrap("Welcome!\n".getBytes());

    public void accept(Selector selector, SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        String address = socketChannel.socket().getInetAddress() + ":" + socketChannel.socket().getPort();

        //FIXME: add id to key
        //key.attach(UUID.randomUUID());
        key.attach(socketChannel);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, address);

        //FIXME provide UUID to client
        socketChannel.write(welcomeBuf);
        welcomeBuf.rewind();
        LOGGER.info("accepted connection from {}", address);
    }

}
