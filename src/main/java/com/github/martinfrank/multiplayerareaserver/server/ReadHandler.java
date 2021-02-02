package com.github.martinfrank.multiplayerareaserver.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.martinfrank.multiplayerareaserver.model.AreaModel;
import com.github.martinfrank.multiplayerprotocol.area.BaseMessageParser;
import com.github.martinfrank.multiplayerprotocol.area.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ReadHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadHandler.class);

    private ProtocolMessageParser protocolMessageParser = new ProtocolMessageParser();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AreaModel areaModel;

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
        //FIXME - wir schicken keine Strings, wir schicken datapackete - byte[] orientiert
        //ABER: vorerst schicken wir JSon  Strings, damit wir die Datagramme leichter lesen/schreiben können
        String message;
        if (bytesRead < 0) {
            message = key.attachment() + " left the server.\n";
            socketChannel.close();
        } else {
            String raw = stringBuilder.toString();
            message = key.attachment() + ": " + raw;
            Message msg = objectMapper.readValue(raw, Message.class);
            areaModel.parse(msg);
            protocolMessageParser.parse(msg);

        }
        LOGGER.debug("read: {}", message);

    }

    public void setAreaModel(AreaModel areaMap) {
        this.areaModel = areaMap;
    }
}
