package org.bitcoma.heartsclient;

import static org.jboss.netty.channel.Channels.pipeline;

import org.bitcoma.hearts.model.transfered.OneMessageProtos;
import org.bitcoma.hearts.netty.handler.OneMessageEncoder;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class HeartsClientPipelineFactory implements ChannelPipelineFactory {

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();

        // Enable stream loggers
        // pipeline.addLast("logger", new LoggingHandler(InternalLogLevel.INFO,
        // true));
        pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
        pipeline.addLast("protobufDecoder", new ProtobufDecoder(OneMessageProtos.OneMessage.getDefaultInstance()));

        pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast("protobufEncoder", new ProtobufEncoder());
        pipeline.addLast("oneMessageEncoder", new OneMessageEncoder());

        pipeline.addLast("mainHandler", new HeartsClientHandler());

        return pipeline;
    }

}
