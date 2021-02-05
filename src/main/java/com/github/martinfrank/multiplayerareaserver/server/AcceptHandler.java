package com.github.martinfrank.multiplayerareaserver.server;

import com.github.martinfrank.multiplayerareaserver.MultiplayerAreaServerTicker;
import com.github.martinfrank.multiplayerprotocol.area.AreaTotal;
import com.github.martinfrank.multiplayerprotocol.area.MessageJsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicLong;

public class AcceptHandler{


    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptHandler.class);
    private final AtomicLong counter = new AtomicLong();

    //FIXME into configuration
    private final ByteBuffer welcomeBuf = ByteBuffer.wrap("Welcome!\n".getBytes());

    private MultiplayerAreaServerTicker multiplayerAreaServerTicker;

    public void accept(Selector selector, SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        String address = socketChannel.socket().getInetAddress() + ":" + socketChannel.socket().getPort();

        //FIXME: add id to key
        long id = counter.incrementAndGet();
        key.attach(id);
        multiplayerAreaServerTicker.registerKey(key);
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, address);

        //FIXME provide UUID to client
        AreaTotal areaTotal = multiplayerAreaServerTicker.getAreaTotal();
        String messageJson = MessageJsonFactory.create(areaTotal);
        ByteBuffer messageBuffer = ByteBuffer.wrap(messageJson.getBytes());

        socketChannel.write(messageBuffer);
        LOGGER.info("accepted connection from {}", address);
    }

    public void setAreaModel(MultiplayerAreaServerTicker multiplayerAreaServerTicker) {
        this.multiplayerAreaServerTicker = multiplayerAreaServerTicker;
    }


}
