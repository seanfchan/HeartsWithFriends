package org.bitcoma.hearts.netty.handler;

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;

/**
 * Simple handler that counts total number of bytes that pass through it.
 * Separate counters are used for written bytes and read bytes. Good for
 * performance metrics.
 * 
 * @author jon
 */
public class ByteCounterHandler extends SimpleChannelUpstreamHandler {

    private final AtomicLong writtenBytes;
    private final AtomicLong readBytes;

    public ByteCounterHandler() {
        this.writtenBytes = new AtomicLong();
        this.readBytes = new AtomicLong();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e.getMessage() instanceof ChannelBuffer) {
            this.readBytes.addAndGet(((ChannelBuffer) e.getMessage()).readableBytes());
        }

        super.messageReceived(ctx, e);
    }

    @Override
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
        super.writeComplete(ctx, e);
        this.writtenBytes.addAndGet(e.getWrittenAmount());
    }

    public long getWrittenBytes() {
        return writtenBytes.get();
    }

    public long getWrittenBytesAndClear() {
        return writtenBytes.getAndSet(0);
    }

    public long getReadBytes() {
        return readBytes.get();
    }

    public long getReadBytesAndClear() {
        return readBytes.getAndSet(0);
    }
}
