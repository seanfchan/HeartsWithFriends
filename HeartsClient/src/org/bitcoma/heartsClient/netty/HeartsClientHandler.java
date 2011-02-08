package org.bitcoma.heartsClient.netty;

import java.util.HashMap;
import java.util.List;

import org.bitcoma.hearts.model.transfered.OneMessageProtos.OneMessage;
import org.bitcoma.hearts.model.transfered.OneMessageWrapper;
import org.bitcoma.heartsClient.HeartsProtoHandler;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.google.protobuf.MessageLite;

public class HeartsClientHandler extends SimpleChannelUpstreamHandler {

    private List<HeartsProtoHandler> handlers = null;
    private Channel channel;
    private int messageId = 0;
    private HashMap<Integer, MessageLite> pendingRequests;

    /**
     * Creates a client-side handler.
     */
    public HeartsClientHandler(List<HeartsProtoHandler> handlers) {
        this.handlers = handlers;
        pendingRequests = new HashMap<Integer, MessageLite>();
    }

    // Note: synchronized at the NettyThread level
    public boolean writeMessage(MessageLite msg) {
        if (channel == null || msg == null)
            return false;
        else {
            ++messageId;

            synchronized (pendingRequests) {
                pendingRequests.put(messageId, msg);
            }

            OneMessageWrapper wrapper = new OneMessageWrapper(messageId, msg);
            channel.write(wrapper);
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

                MessageLite origRequest;
                synchronized (pendingRequests) {
                    origRequest = pendingRequests.remove(msg.getMessageId());
                }

                for (HeartsProtoHandler handler : handlers) {
                    handler.handleLoginResponse(msg.getLoginResponse(), origRequest);
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

        case PLAY_CARD_REQUEST:
            if (msg.hasPlayCardRequest()) {
                for (HeartsProtoHandler handler : handlers) {
                    handler.handlePlayCardRequest(msg.getPlayCardRequest());
                }
            }
            break;

        case GENERIC_RESPONSE:
            if (msg.hasGenericResponse()) {

                MessageLite origRequest;
                synchronized (pendingRequests) {
                    origRequest = pendingRequests.remove(msg.getMessageId());
                }

                for (HeartsProtoHandler handler : handlers) {
                    handler.handleGenericResponse(msg.getGenericResponse(), origRequest);
                }
            }
            break;

        case JOIN_GAME_RESPONSE:
            if (msg.hasJoinGameResponse()) {

                MessageLite origRequest;
                synchronized (pendingRequests) {
                    origRequest = pendingRequests.remove(msg.getMessageId());
                }

                for (HeartsProtoHandler handler : handlers) {
                    handler.handleJoinGameResponse(msg.getJoinGameResponse(), origRequest);
                }
            }
            break;

        case PLAY_SINGLE_CARD_RESPONSE:
            if (msg.hasPlaySingleCardResponse()) {

                MessageLite origRequest;
                synchronized (pendingRequests) {
                    origRequest = pendingRequests.remove(msg.getMessageId());
                }

                for (HeartsProtoHandler handler : handlers) {
                    handler.handlePlaySingleCardResponse(msg.getPlaySingleCardResponse(), origRequest);
                }
            }
            break;

        case PASS_CARDS_RESPONSE:
            if (msg.hasPassCardsResponse()) {

                MessageLite origRequest;
                synchronized (pendingRequests) {
                    origRequest = pendingRequests.remove(msg.getMessageId());
                }

                for (HeartsProtoHandler handler : handlers) {
                    handler.handlePassCardsResponse(msg.getPassCardsResponse(), origRequest);
                }
            }
            break;

        case GAME_ENDED_RESPONSE:
            if (msg.hasGameEndedResponse()) {

                MessageLite origRequest;
                synchronized (pendingRequests) {
                    origRequest = pendingRequests.remove(msg.getMessageId());
                }

                for (HeartsProtoHandler handler : handlers) {
                    handler.handleGameEndedResponse(msg.getGameEndedResponse(), origRequest);
                }
            }
            break;

        case ROUND_ENDED_RESPONSE:
            if (msg.hasRoundEndedResponse()) {

                MessageLite origRequest;
                synchronized (pendingRequests) {
                    origRequest = pendingRequests.remove(msg.getMessageId());
                }

                for (HeartsProtoHandler handler : handlers) {
                    handler.handleRoundEndedResponse(msg.getRoundEndedResponse(), origRequest);
                }
            }
            break;

        case ROUND_STARTED_RESPONSE:
            if (msg.hasRoundStartedResponse()) {

                MessageLite origRequest;
                synchronized (pendingRequests) {
                    origRequest = pendingRequests.remove(msg.getMessageId());
                }

                for (HeartsProtoHandler handler : handlers) {
                    handler.handleRoundStartedResponse(msg.getRoundStartedResponse(), origRequest);
                }
            }
            break;

        case TRICK_ENDED_RESPONSE:
            if (msg.hasTrickEndedResponse()) {

                MessageLite origRequest;
                synchronized (pendingRequests) {
                    origRequest = pendingRequests.remove(msg.getMessageId());
                }

                for (HeartsProtoHandler handler : handlers) {
                    handler.handleTrickEndedResponse(msg.getTrickEndedResponse(), origRequest);
                }
            }
            break;

        case SCORE_UPDATE_RESPONSE:
            if (msg.hasScoreUpdateResponse()) {

                MessageLite origRequest;
                synchronized (pendingRequests) {
                    origRequest = pendingRequests.remove(msg.getMessageId());
                }

                for (HeartsProtoHandler handler : handlers) {
                    handler.handleScoreUpdateResponse(msg.getScoreUpdateResponse(), origRequest);
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
