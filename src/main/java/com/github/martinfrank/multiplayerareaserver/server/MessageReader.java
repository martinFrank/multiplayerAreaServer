package com.github.martinfrank.multiplayerareaserver.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.martinfrank.multiplayerprotocol.area.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MessageReader {

    private final ByteBuffer buffer;
    private int bytesRead;
    private StringBuilder stringBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<Message<SelectionKeyId>> TYPE_REFERENCE = new TypeReference<Message<SelectionKeyId>>(){};

    public MessageReader(int bufferSize) {
        buffer = ByteBuffer.allocate(bufferSize);
        stringBuilder = new StringBuilder();
    }

    public void read(SocketChannel socketChannel) {
        buffer.clear();
        stringBuilder = new StringBuilder();
        bytesRead = 0;
        try {
            while ((bytesRead = socketChannel.read(buffer)) > 0) {
                buffer.flip();
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes);
                stringBuilder.append(new String(bytes));
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isClosed() {
        return bytesRead < 0;
    }

    public Message<SelectionKeyId> getMessage() {
        try {
            return objectMapper.readValue(stringBuilder.toString(), TYPE_REFERENCE);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
