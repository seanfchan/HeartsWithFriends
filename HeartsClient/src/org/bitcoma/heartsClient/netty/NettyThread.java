package org.bitcoma.heartsClient.netty;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import org.bitcoma.heartsClient.HeartsProtoHandler;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.google.protobuf.MessageLite;

public class NettyThread extends Thread {

    public static int PORT = 7000;
    public static String HOST = "192.168.1.105";

    private static NettyThread instance;
    private List<HeartsProtoHandler> handlers;
    private HeartsClientHandler clientHandler;
    private boolean running;
    private Channel writeChannel = null;

    private NettyThread() {
        running = false;
        handlers = new LinkedList<HeartsProtoHandler>();
    }

    public static NettyThread getInstance() {
        if (instance == null)
            instance = new NettyThread();
        return instance;
    }

    public void registerHandler(HeartsProtoHandler handler) {
        synchronized (handlers) {
            handlers.add(handler);
        }
    }

    public void unregisterHandler(HeartsProtoHandler handler) {
        synchronized (handlers) {
            handlers.remove(handler);
        }
    }

    /**
     * Writes a message to the server.
     * 
     * @param msg
     *            Message to write to the server
     * @return true if write was successful, false otherwise
     */
    public boolean writeMessage(MessageLite msg) {
        if (clientHandler == null)
            return false;
        return clientHandler.writeMessage(msg);
    }

    @Override
    public synchronized void start() {
        if (!running) {
            running = true;
            super.start();
        }
    }

    /**
     * Closes channel associated with the socket thread. This should also
     * terminate the thread used to receive messages.
     */
    public synchronized void close() {
        if (running && writeChannel != null) {
            writeChannel.close();
        }
    }

    @Override
    public void run() {
        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new HeartsClientPipelineFactory(handlers));

        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(HOST, PORT));

        // Wait until the connection is established
        writeChannel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return;
        }

        clientHandler = writeChannel.getPipeline().get(HeartsClientHandler.class);

        // Wait until the channel closes. To make sure responses are seen.
        future.getChannel().getCloseFuture().awaitUninterruptibly();

        // Shut down thread pools to exit.
        bootstrap.releaseExternalResources();

        running = false;
        writeChannel = null;
    }

}
