package com.github.martinfrank.multiplayerareaserver.server;


import com.github.martinfrank.multiplayerareaserver.model.AreaModel;
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

public class NonBlockingServer implements Runnable, BroadcastServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonBlockingServer.class);

    private final int port;
    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;
    private final AcceptHandler acceptHandler;
    private final ReadHandler readHandler;

    public static void main(String[] args) throws IOException {
        NonBlockingServer server = new NonBlockingServer(10523);
        new Thread(server).start();
    }

    public NonBlockingServer(int port) throws IOException {
        this.port = port;
        acceptHandler = new AcceptHandler();
        readHandler = new ReadHandler();
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

    @Override
    public void broadcast(String message) {
        LOGGER.debug("broadcasting {}", message);
        //FIXME why Buffer?

        ByteBuffer broadcastBuffer = ByteBuffer.wrap(message.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel socketChannel = (SocketChannel) key.channel();
                try {
                    socketChannel.write(broadcastBuffer);
                    broadcastBuffer.rewind();
                } catch (IOException e) {
                    //FIXME handle exception
                }
            }
        }

    }


    public void setMessageQueue(AreaModel areaModel) {
        readHandler.setAreaModel(areaModel);
        acceptHandler.setAreaModel(areaModel);
    }

}
