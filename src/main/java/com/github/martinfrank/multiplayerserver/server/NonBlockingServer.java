package com.github.martinfrank.multiplayerserver.server;


import com.github.martinfrank.multiplayerserver.model.MapChanges;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.Iterator;

public class NonBlockingServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonBlockingServer.class);

    private final int port;
    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;
    private final AcceptHandler acceptHandler;
    private final ReadHandler readHandler;

    public static void main(String[] args) throws IOException {
        AcceptHandler acceptHandler = new AcceptHandler();
        ReadHandler readHandler = new ReadHandler();
        NonBlockingServer server = new NonBlockingServer(10523, acceptHandler, readHandler);
        new Thread(server).start();
    }

    public NonBlockingServer(int port, AcceptHandler acceptHandler, ReadHandler readHandler) throws IOException {
        this.port = port;
        this.acceptHandler = acceptHandler;
        this.readHandler = readHandler;
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Starting nio nonblocking server on port {}", port);
            Iterator<SelectionKey> selectionKeyIterator;
            SelectionKey key;

            while (serverSocketChannel.isOpen()) {
                selector.select();
                selectionKeyIterator = selector.selectedKeys().iterator();
                while (selectionKeyIterator.hasNext()) {
                    key = selectionKeyIterator.next();
                    selectionKeyIterator.remove();

                    if (key.isAcceptable()) {
                        acceptHandler.accept(selector, key);
                    }
                    if (key.isReadable()) {
                        readHandler.read(key);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("IOException, server of port {} terminating. Stack trace:", port);
            e.printStackTrace();
        }
    }

    private void broadcast(String message) throws IOException {
        LOGGER.debug("broadcasting {}", message);
        //FIXME why Buffer?
        ByteBuffer broadcastBuffer = ByteBuffer.wrap(message.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                socketChannel.write(broadcastBuffer);
                broadcastBuffer.rewind();
            }
        }
    }


    public void broadcast(MapChanges mapChanges) throws IOException {
        String changesAsString = mapChanges.createBroadCastMessage();
        broadcast(changesAsString);
    }
}
