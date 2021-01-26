package com.github.martinfrank.multiplayerserver.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MultiPlayerClient implements Runnable {

    static ByteBuffer buffer = ByteBuffer.allocate(256);

    private final SocketChannel socketChannel;
    private final Selector selector;
    public MultiPlayerClient(String server, int port) throws IOException {
        InetSocketAddress address = new InetSocketAddress(server, 10523);
        socketChannel = SocketChannel.open(address);
        socketChannel.configureBlocking(false);
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args) throws IOException {
        MultiPlayerClient client = new MultiPlayerClient("192.168.0.69", 10523);
        new Thread(client).start();
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel ch = (SocketChannel) key.channel();
        StringBuilder sb = new StringBuilder();

        buffer.clear();
        int read = 0;
        while ((read = ch.read(buffer)) > 0) {
            buffer.flip();
            byte[] bytes = new byte[buffer.limit()];
            buffer.get(bytes);
            sb.append(new String(bytes));
            buffer.clear();
        }
        String msg;
        if (read < 0) {
            msg = key + " left the chat.\n";
            ch.close();
        } else {
            msg = sb.toString();
        }
        System.out.println(msg);
    }

    private void handleWrite(SelectionKey key) throws IOException, InterruptedException {
        String msg = "fizzbuzz";
        ByteBuffer msgBuf = ByteBuffer.wrap(msg.getBytes());

        if (key.isValid() && key.channel() instanceof SocketChannel) {
            SocketChannel sch = (SocketChannel) key.channel();
            sch.write(msgBuf);
            msgBuf.rewind();
        }
        Thread.sleep(1000);
    }

    @Override
    public void run() {

        try {
            Iterator<SelectionKey> keyIterator;
            SelectionKey key;
            while (socketChannel.isOpen()) {
                selector.select();
                keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isReadable()) {
                        handleRead(key);
                    }

                }
            }
        }catch (IOException e){

        }
    }
}
