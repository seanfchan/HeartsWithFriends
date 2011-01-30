package org.bitcoma.heartsclient;

import java.util.concurrent.atomic.AtomicLong;

import org.bitcoma.hearts.model.transfered.GenericProtos.GenericResponse;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginResponse;
import org.bitcoma.hearts.model.transfered.OneMessageProtos.OneMessage;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartsClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(HeartsClientHandler.class);
    private final AtomicLong transferredBytes = new AtomicLong();

    /**
     * Creates a client-side handler.
     */
    public HeartsClientHandler() {
    }

    public long getTransferredBytes() {
        return transferredBytes.get();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

        OneMessage msg = (OneMessage) e.getMessage();

        switch (msg.getType()) {
        case JOIN_GAME_REQUEST:
            logger.info("Client: Join Request seen");
            break;
        case LOGIN_REQUEST:
            logger.info("Client: Login Request seen");
            break;
        case LOGIN_RESPONSE:
            logger.info("Client: Login Response seen");
            LoginResponse loginResponse = (LoginResponse) msg.getLoginResponse();
            logger.info("Login Response: user_id " + loginResponse.getUserId());
            break;
        case SIGNUP_REQUEST:
            logger.info("Client: Signup Request seen");
            break;
        case FIND_GAMES_REQUEST:
            logger.info("Client: Find Games Request seen");
            break;
        case FIND_GAME_ROOMS_REQUEST:
            logger.info("Client: Find Game Rooms Request seen");
            break;
        case GENERIC_RESPONSE:
            logger.info("Client: Generic Response seen");
            GenericResponse gr = (GenericResponse) msg.getGenericResponse();
            logger.info("GenericResponse Code: " + gr.getResponseCode());
            break;
        case FIND_GAMES_RESPONSE:
            logger.info("Client: Find Games Response seen");
            break;
        case FIND_GAME_ROOMS_RESPONSE:
            logger.info("Client: Find Game Rooms Response seen");
            break;

        // Unrecognized messages.
        default:
            logger.info("Unrecognized/unexpected message seen");
            break;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        // Close the connection when an exception is raised.
        logger.warn("Unexpected exception from downstream.", e.getCause());
        e.getChannel().close();
    }
}
