package org.bitcoma.heartsClient.netty;

import java.util.List;

import org.bitcoma.hearts.model.transfered.OneMessageProtos.OneMessage;
import org.bitcoma.heartsClient.HeartsProtoHandler;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.google.protobuf.MessageLite;

public class HeartsClientHandler extends SimpleChannelUpstreamHandler {

    private List<HeartsProtoHandler> handlers = null;
    private Channel channel;

    /**
     * Creates a client-side handler.
     */
    public HeartsClientHandler(List<HeartsProtoHandler> handlers) {
        this.handlers = handlers;
    }

    public boolean writeMessage(MessageLite msg) {
        if (channel == null || msg == null)
            return false;
        else {
            ChannelFuture future = channel.write(msg);
            return true;
        }
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channel = e.getChannel();
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channel = null;
        super.channelClosed(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

        OneMessage msg = (OneMessage) e.getMessage();
        switch (msg.getType()) {
        case JOIN_GAME_REQUEST:
            if (msg.hasJoinGameRequest()) {
                for (HeartsProtoHandler handler : handlers) {
                    handler.handleJoinGameRequest(msg.getJoinGameRequest());
                }
            }
            break;
        case LOGIN_REQUEST:
            if (msg.hasLoginRequest()) {
                for (HeartsProtoHandler handler : handlers) {
                    handler.handleLoginRequest(msg.getLoginRequest());
                }
            }
            break;
        case LOGIN_RESPONSE:
            if (msg.hasLoginResponse()) {
                for (HeartsProtoHandler handler : handlers) {
                    handler.handleLoginResponse(msg.getLoginResponse());
                }
            }
            break;
        case SIGNUP_REQUEST:
            if (msg.hasSignupRequest()) {
                for (HeartsProtoHandler handler : handlers) {
                    handler.handleSignupRequest(msg.getSignupRequest());
                }
            }
            break;
        case FIND_GAMES_REQUEST:
            if (msg.hasFindGameRoomsRequest()) {
                for (HeartsProtoHandler handler : handlers) {
                    handler.handleFindGamesRequest(msg.getFindGamesRequest());
                }
            }
            break;
        case FIND_GAMES_RESPONSE:
            if (msg.hasFindGamesResponse()) {
                for (HeartsProtoHandler handler : handlers) {
                    handler.handleFindGamesResponse(msg.getFindGamesResponse());
                }
            }
            break;
        case FIND_GAME_ROOMS_REQUEST:
            if (msg.hasFindGameRoomsRequest()) {
                for (HeartsProtoHandler handler : handlers) {
                    handler.handleFindGameRoomsRequest(msg.getFindGameRoomsRequest());
                }
            }
            break;
        case FIND_GAME_ROOMS_RESPONSE:
            if (msg.hasFindGameRoomsResponse()) {
                for (HeartsProtoHandler handler : handlers) {
                    handler.handleFindGameRoomsResponse(msg.getFindGameRoomsResponse());
                }
            }
            break;
        case GENERIC_RESPONSE:
            if (msg.hasGenericResponse()) {
                for (HeartsProtoHandler handler : handlers) {
                    handler.handleGenericResponse(msg.getGenericResponse());
                }
            }
            break;

        // Unrecognized messages.
        default:
            for (HeartsProtoHandler handler : handlers) {
                handler.handleUnexpectedMessage(null);
            }
            break;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        for (HeartsProtoHandler handler : handlers) {
            handler.handleThrowable(e.getCause());
        }

        // Close the connection when an exception is raised.
        e.getChannel().close();
    }
}
