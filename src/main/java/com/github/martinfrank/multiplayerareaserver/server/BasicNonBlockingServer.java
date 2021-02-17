package com.github.martinfrank.multiplayerareaserver.server;

import com.github.martinfrank.multiplayerareaserver.MultiplayerAreaServerTicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public abstract class BasicNonBlockingServer implements Runnable, BroadcastServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicNonBlockingServer.class);
    private final int port;
    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;


    public BasicNonBlockingServer(int port) throws IOException {
        this.port = port;
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    }

    @Override
    public void run() {

        LOGGER.info("Starting nio nonblocking server on port {}", port);
        Iterator<SelectionKey> selectionKeyIterator;
        SelectionKey key;
        try {
            while (serverSocketChannel.isOpen()) {
                selector.select();
                selectionKeyIterator = selector.selectedKeys().iterator();
                while (selectionKeyIterator.hasNext()) {
                    key = selectionKeyIterator.next();
                    selectionKeyIterator.remove();

                    if (key.isAcceptable()) {
                        accept(selector, key);
                    }
                    if (key.isReadable()) {
                        read(key);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("IOException", e);
            e.printStackTrace();
        }
    }

    public abstract void read(SelectionKey key) throws IOException;

    public abstract void accept(Selector selector, SelectionKey key) throws IOException;

    @Override
    public void broadcast(String message) {
        LOGGER.debug("broadcasting {}", message);
        try {
            ByteBuffer broadcastBuffer = ByteBuffer.wrap(message.getBytes());
            for (SelectionKey key : selector.keys()) {
                if (key.isValid() && key.channel() instanceof SocketChannel) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    socketChannel.write(broadcastBuffer);
                    broadcastBuffer.rewind();
                }
            }
        } catch (IOException e) {
            LOGGER.error("IOException", e);
            e.printStackTrace();
        }

    }

    @Override
    public void singlecast(String message, SelectionKey key) {
        try {
            ByteBuffer messgaeBuffer = ByteBuffer.wrap(message.getBytes());
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                socketChannel.write(messgaeBuffer);
                messgaeBuffer.rewind();
            }
        } catch (IOException e) {
            LOGGER.error("IOException", e);
            e.printStackTrace();
        }

    }

    public abstract void setAreaServerTicker(MultiplayerAreaServerTicker multiplayerAreaServerTicker);
}
