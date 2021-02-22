package com.github.martinfrank.multiplayerareaserver.server;


import com.fasterxml.jackson.core.type.TypeReference;
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

    private final MessageReader messageReader = new MessageReader(1024);
    private MultiplayerAreaServerTicker multiplayerAreaServerTicker;
    private final AtomicLong counter = new AtomicLong();

    private static final Logger LOGGER = LoggerFactory.getLogger(NonBlockingServer.class);

    public NonBlockingServer(int port) throws IOException {
        super(port);
    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        SelectionKeyId selectionKeyId = (SelectionKeyId) key.attachment();
        messageReader.read(socketChannel);

        if(messageReader.isClosed() ){
            LOGGER.debug("selectionKeyId: {} left the server", selectionKeyId);
            socketChannel.close();
            multiplayerAreaServerTicker.deregister(selectionKeyId);
            return;
        }
        Message<SelectionKeyId> message = messageReader.getMessage();
        if(message != null){
            LOGGER.debug("selectionKeyId: {} received message {}", selectionKeyId, message);
            message.context = selectionKeyId;
            multiplayerAreaServerTicker.parse(message);
        }
    }

    public void accept(Selector selector, SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        String address = socketChannel.socket().getInetAddress() + ":" + socketChannel.socket().getPort();
        SelectionKeyId selectionKeyId = new SelectionKeyId(address, counter.incrementAndGet());
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, selectionKeyId);
        LOGGER.info("accepted connection from {}", address);
    }

    public void setAreaServerTicker(MultiplayerAreaServerTicker multiplayerAreaServerTicker) {
        this.multiplayerAreaServerTicker = multiplayerAreaServerTicker;
    }

}
