package org.bitcoma.heartsclient;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

import org.bitcoma.hearts.HeartsProtoHandler;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.hearts.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.hearts.netty.handler.HeartsClientHandler;
import org.bitcoma.heartsClient.netty.HeartsClientPipelineFactory;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartsClient {

    public static final Logger logger = LoggerFactory.getLogger(HeartsClient.class);

    public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
        if (args.length != 4) {
            System.err.println("Usage: " + HeartsClient.class.getSimpleName() + " <host> <port> <username> <password>");
            return;
        }

        logger.info("Starting up client");

        // Parse options.
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);
        final String username = args[2];
        final String password = args[3];

        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        // Use a basic handler for the messages used.
        List<HeartsProtoHandler> handlers = new LinkedList<HeartsProtoHandler>();
        HeartsClientPipelineFactory pipeline = new HeartsClientPipelineFactory(handlers);

        logger.info("Setting pipeline factory");

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(pipeline);

        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

        logger.info("Trying to connect");

        // Wait until the connection is established
        Channel channel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return;
        }

        logger.info("Connected successfully");

        // HACK: This is a tight coupling but only used for performance testing
        // so it shouldn't really matter too much
        HeartsClientHandler networkHandle = channel.getPipeline().get(HeartsClientHandler.class);
        HeartsProtoHandler handler = new SimpleHeartsProtoHandler(networkHandle);
        handlers.add(handler);

        // Try to sign up. This may fail or add the user doesn't really matter.
        // We will sign in right after this.
        SignupRequest sr = SignupRequest.newBuilder().setUserName(username).setEmail(username + "@mymail.com")
                .setPassword(password).build();
        networkHandle.writeMessage(sr);

        // Login as the user passed in.
        LoginRequest lr = LoginRequest.newBuilder().setIdentifier(username).setPassword(password).build();
        networkHandle.writeMessage(lr);

        // Wait until the channel closes. To make sure responses are seen.
        future.getChannel().getCloseFuture().awaitUninterruptibly();

        // Shut down thread pools to exit.
        bootstrap.releaseExternalResources();

        logger.info("Shutting down client");
    }
}
