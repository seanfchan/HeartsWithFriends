package org.bitcoma.heartsclient;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.bitcoma.hearts.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.hearts.model.transfered.SignupProtos.SignupRequest;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class HeartsClient {

    public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
        if (args.length != 2) {
            System.err.println("Usage: " + HeartsClient.class.getSimpleName() + " <host> <port>");
            return;
        }

        // Parse options.
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);

        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new HeartsClientPipelineFactory());

        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

        // Wait until the connection is established
        Channel channel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return;
        }

        // FindGameRoomsRequest fgrr =
        // FindGameRoomsRequest.newBuilder().build();
        // channel.write(fgrr);
        //
        // FindGamesRequest fgr =
        // FindGamesRequest.newBuilder().setGameRoomId(27)
        // .build();
        // channel.write(fgr);
        //
        // JoinRequest jr = JoinRequest.newBuilder().setGameId(2).build();
        // channel.write(jr);
        //
        // LoginRequest lr = LoginRequest.newBuilder().setEmail("dude")
        // .setPassword("mypass").build();
        // channel.write(lr);
        //
        // SignupRequest sr = SignupRequest.newBuilder().setEmail("dude")
        // .setPassword("mypass").build();
        // channel.write(sr);
        //
        // GenericResponse gr = GenericResponse.newBuilder()
        // .setResponseCode(GenericResponse.ResponseCode.OK).build();
        // channel.write(gr);

        SignupRequest sr = SignupRequest.newBuilder().setEmail("dude").setPassword("mypass").build();
        channel.write(sr);

        LoginRequest lr = LoginRequest.newBuilder().setEmail("dude").setPassword("mypass").build();
        channel.write(lr);

        // Wait until the channel closes. To make sure responses are seen.
        future.getChannel().getCloseFuture().awaitUninterruptibly();

        // Shut down thread pools to exit.
        bootstrap.releaseExternalResources();
    }
}
