package org.bitcoma.hearts.netty.handler;

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.WriteCompletionEvent;

/**
 * Simple handler that counts total number of messages that pass through it.
 * Good for performance metrics.
 * 
 * @author jon
 */
public class MessageCounterHandler extends SimpleChannelHandler {

    private final AtomicLong writtenMessages;
    private final AtomicLong readMessages;

    public MessageCounterHandler() {
        this.writtenMessages = new AtomicLong();
        this.readMessages = new AtomicLong();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        this.readMessages.incrementAndGet();
        super.messageReceived(ctx, e);
    }

    @Override
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
        this.writtenMessages.incrementAndGet();
        super.writeComplete(ctx, e);
    }

    public long getWrittenMessages() {
        return writtenMessages.get();
    }

    public long getWrittenMessagesAndClear() {
        return writtenMessages.getAndSet(0);
    }

    public long getReadMessages() {
        return readMessages.get();
    }

    public long getReadMessagesAndClear() {
        return readMessages.getAndSet(0);
    }
}