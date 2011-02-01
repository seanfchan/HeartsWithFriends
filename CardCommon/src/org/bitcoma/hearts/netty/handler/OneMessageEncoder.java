package org.bitcoma.hearts.netty.handler;

import org.bitcoma.hearts.model.transfered.OneMessageWrapper;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class OneMessageEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        return OneMessageWrapper.wrapMessage(msg);
    }
}