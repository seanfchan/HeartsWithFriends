package org.bitcoma.heartserver.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.bitcoma.hearts.netty.handler.ByteCounterHandler;
import org.bitcoma.hearts.netty.handler.MessageCounterHandler;
import org.bitcoma.heartserver.netty.task.PerformanceMetricsTask;
import org.bitcoma.heartserver.netty.task.TimeOutTaskCreator;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

public class HeartsServer {

    public static void main(String[] args) throws Exception {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        OrderedMemoryAwareThreadPoolExecutor executor = new OrderedMemoryAwareThreadPoolExecutor(200, 0, 0);

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new HeartsServerPipelineFactory(executor));
        bootstrap.setOption("child.tcpNoDelay", true);

        // Bind and start to accept incoming connections.
        Channel channel = bootstrap.bind(new InetSocketAddress(7000));

        // Grab the Performance handlers to pass to performance task
        ByteCounterHandler byteCounter = HeartsServerPipelineFactory.BYTE_COUNTER;
        MessageCounterHandler msgHandler = HeartsServerPipelineFactory.MESSAGE_COUNTER;

        // Start up a recurring task to log throughput performance metrics.
        TimeOutTaskCreator.createTask(new PerformanceMetricsTask(byteCounter, msgHandler), PerformanceMetricsTask.DELAY,
                PerformanceMetricsTask.UNIT);

        // Wait for server to shutdown.
        channel.getCloseFuture().awaitUninterruptibly();

        // Close down all the timer tasks.
        TimeOutTaskCreator.stop();
    }
}
