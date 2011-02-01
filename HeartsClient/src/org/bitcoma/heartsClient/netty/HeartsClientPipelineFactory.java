package org.bitcoma.heartsClient.netty;

import static org.jboss.netty.channel.Channels.pipeline;

import java.util.List;

import org.bitcoma.hearts.model.transfered.OneMessageProtos;
import org.bitcoma.hearts.netty.handler.OneMessageEncoder;
import org.bitcoma.heartsClient.HeartsProtoHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.logging.InternalLogLevel;

public class HeartsClientPipelineFactory implements ChannelPipelineFactory {

    List<HeartsProtoHandler> handlers = null;

    public HeartsClientPipelineFactory(List<HeartsProtoHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();

        // Enable stream loggers
        pipeline.addLast("logger", new LoggingHandler(InternalLogLevel.INFO, true));
        pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
        pipeline.addLast("protobufDecoder", new ProtobufDecoder(OneMessageProtos.OneMessage.getDefaultInstance()));

        pipeline.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast("protobufEncoder", new ProtobufEncoder());
        pipeline.addLast("oneMessageEncoder", new OneMessageEncoder());

        pipeline.addLast("mainHandler", new HeartsClientHandler(handlers));

        return pipeline;
    }

}
