package com.github.martinfrank.multiplayerserver.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ReadHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadHandler.class);

    private final ByteBuffer buffer = ByteBuffer.allocate(256);

    public void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        StringBuilder stringBuilder = new StringBuilder();

        buffer.clear();
        int bytesRead;
        while ((bytesRead = socketChannel.read(buffer)) > 0) {
            buffer.flip();
            byte[] bytes = new byte[buffer.limit()];
            buffer.get(bytes);
            stringBuilder.append(new String(bytes));
            buffer.clear();
        }
        String message;
        if (bytesRead < 0) {
            message = key.attachment() + " left the server.\n";
            socketChannel.close();
        } else {
            message = key.attachment() + ": " + stringBuilder.toString();
        }
        LOGGER.debug("read: {}", message);

    }
}
