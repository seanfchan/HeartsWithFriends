package org.bitcoma.heartserver.netty.handler;

import org.bitcoma.hearts.model.transfered.GenericProtos.GenericResponse;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest;
import org.bitcoma.hearts.model.transfered.LeaveGameProtos.LeaveGameRequest;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.hearts.model.transfered.OneMessageProtos.OneMessage;
import org.bitcoma.hearts.model.transfered.OneMessageWrapper;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PlayCardRequest;
import org.bitcoma.hearts.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.hearts.model.transfered.StartGameProtos.StartGameRequest;
import org.bitcoma.heartserver.HeartsServerApiImpl;
import org.bitcoma.heartserver.IHeartsServerApi;
import org.bitcoma.heartserver.ServerState;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.MessageLite;

public class HeartsServerHandler extends SimpleChannelHandler {

    private static Logger logger = LoggerFactory.getLogger(HeartsServerHandler.class);
    private IHeartsServerApi api = new HeartsServerApiImpl();

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        OneMessage msg = (OneMessage) e.getMessage();
        MessageLite response = null;

        switch (msg.getType()) {
        case JOIN_GAME_REQUEST:
            logger.info("Server: Join Game Request seen");
            JoinGameRequest joinRequest = msg.getJoinGameRequest();
            response = api.joinGame(joinRequest);
            // Write response to channel with matching message id
            e.getChannel().write(new OneMessageWrapper(msg.getMessageId(), response));
            break;

        case LEAVE_GAME_REQUEST:
            logger.info("Server: Leave Game Request seen");
            LeaveGameRequest leaveRequest = msg.getLeaveGameReqeuest();
            response = api.leaveGame(leaveRequest);
            // Write response to channel with matching message id
            e.getChannel().write(new OneMessageWrapper(msg.getMessageId(), response));
            break;

        case LOGIN_REQUEST:
            logger.info("Server: Login Request seen");
            LoginRequest loginRequest = msg.getLoginRequest();
            response = api.login(loginRequest, e.getChannel());
            // Write response to channel with matching message id
            e.getChannel().write(new OneMessageWrapper(msg.getMessageId(), response));
            break;

        case SIGNUP_REQUEST:
            logger.info("Server: Signup Request seen");
            SignupRequest signupRequest = msg.getSignupRequest();

            response = api.signup(signupRequest);
            // Write response to channel with matching message id
            e.getChannel().write(new OneMessageWrapper(msg.getMessageId(), response));
            break;

        case START_GAME_REQUEST:
            logger.info("Server: Start Game Request Seen");
            StartGameRequest startGameRequest = msg.getStartGameRequest();

            response = api.startGame(startGameRequest);
            // Write response to channel with matching message id
            e.getChannel().write(new OneMessageWrapper(msg.getMessageId(), response));
            break;

        case PLAY_CARD_REQUEST:
            logger.info("Server: Play Card Request Seen");
            PlayCardRequest playCardRequest = msg.getPlayCardRequest();

            response = api.playCard(playCardRequest);
            // Write response to channel with matching message id
            e.getChannel().write(new OneMessageWrapper(msg.getMessageId(), response));
            break;

        // Unrecognized messages.
        default:
            logger.info("Server: Unrecognized/unexpected message seen");
            GenericResponse genericResponse = GenericResponse.newBuilder()
                    .setResponseCode(GenericResponse.ResponseCode.UNEXPECTED_REQUEST).build();
            // Write response to channel with matching message id
            e.getChannel().write(new OneMessageWrapper(msg.getMessageId(), genericResponse));
            break;
        }
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        ServerState.numActiveConnections++;

        super.channelConnected(ctx, e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        ServerState.numActiveConnections--;
        api.resetState();

        super.channelDisconnected(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        api.resetState();

        Throwable throwable = e.getCause();
        logger.error("Server Exception caught {}: {}", throwable, throwable != null ? throwable.getMessage() : null);
        e.getChannel().close();
    }
}
