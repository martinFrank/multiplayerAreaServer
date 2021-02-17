package com.github.martinfrank.multiplayerareaserver.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.martinfrank.multiplayerareaserver.MultiplayerAreaServerTicker;
import com.github.martinfrank.multiplayerprotocol.area.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicLong;

public class NonBlockingServer extends BasicNonBlockingServer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private MultiplayerAreaServerTicker multiplayerAreaServerTicker;
    private final AtomicLong counter = new AtomicLong();

    private static final Logger LOGGER = LoggerFactory.getLogger(NonBlockingServer.class);


    public NonBlockingServer(int port) throws IOException {
        super(port);
    }

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
            multiplayerAreaServerTicker.deregister(key);
        } else {
            String raw = stringBuilder.toString();
            message = key.attachment() + ": " + raw;
            Message msg = objectMapper.readValue(raw, Message.class);
            multiplayerAreaServerTicker.parse(msg);
        }
        LOGGER.debug("read: {}", message);
    }

    public void accept(Selector selector, SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        String address = socketChannel.socket().getInetAddress() + ":" + socketChannel.socket().getPort();

        IdAttachment idAttachment = new IdAttachment(address, counter.incrementAndGet());
        multiplayerAreaServerTicker.register(key);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, idAttachment);


//        //FIXME goes to AreaTicker
//        AreaTotal areaTotal = multiplayerAreaServerTicker.getAreaTotal();
//        String messageJson = MessageJsonFactory.create(areaTotal);
//        ByteBuffer messageBuffer = ByteBuffer.wrap(messageJson.getBytes());
//
//        socketChannel.write(messageBuffer);
        LOGGER.info("accepted connection from {}", address);
    }

    public void setAreaServerTicker(MultiplayerAreaServerTicker multiplayerAreaServerTicker) {
        this.multiplayerAreaServerTicker = multiplayerAreaServerTicker;
    }

}
