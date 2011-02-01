package org.bitcoma.heartserver.netty.handler;

import org.bitcoma.hearts.model.transfered.GenericProtos.GenericResponse;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.hearts.model.transfered.OneMessageProtos.OneMessage;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PlayCardRequest;
import org.bitcoma.hearts.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.hearts.model.transfered.StartGameProtos.StartGameRequest;
import org.bitcoma.heartserver.HeartsServerApiImpl;
import org.bitcoma.heartserver.IHeartsServerApi;
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
            logger.info("Server: Join Request seen");
            JoinGameRequest joinRequest = msg.getJoinGameRequest();
            response = api.joinGame(joinRequest);
            e.getChannel().write(response);
            break;
        case LOGIN_REQUEST:
            logger.info("Server: Login Request seen");
            LoginRequest loginRequest = msg.getLoginRequest();
            response = api.login(loginRequest, e.getChannel());
            e.getChannel().write(response);
            break;
        case SIGNUP_REQUEST:
            logger.info("Server: Signup Request seen");
            SignupRequest signupRequest = msg.getSignupRequest();

            // Write the response to the channel
            response = api.signup(signupRequest);
            e.getChannel().write(response);
            break;

        case START_GAME_REQUEST:
            logger.info("Server: Start Game Request Seen");
            StartGameRequest startGameRequest = msg.getStartGameRequest();

            response = api.startGame(startGameRequest);
            e.getChannel().write(response);
            break;

        case PLAY_CARD_REQUEST:
            logger.info("Server: Play Card Request Seen");
            PlayCardRequest playCardRequest = msg.getPlayCardRequest();

            response = api.playCard(playCardRequest);
            e.getChannel().write(response);
            break;

        // NOTE: Currently not supported as this functionality is not
        // needed.
        // case FIND_GAMES_REQUEST:
        // logger.info("Server: Find Games Request seen");
        // FindGamesRequest findGamesRequest = msg.getFindGamesRequest();
        // response = api.findGames(findGamesRequest);
        // e.getChannel().write(response);
        // break;

        // case FIND_GAME_ROOMS_REQUEST:
        // System.out.println("Server: Find Game Rooms Request seen");
        // FindGameRoomsRequest findGameRoomsRequest = msg
        // .getFindGameRoomsRequest();
        // break;

        // Unrecognized messages.
        default:
            logger.info("Server: Unrecognized/unexpected message seen");
            GenericResponse genericResponse3 = GenericResponse.newBuilder()
                    .setResponseCode(GenericResponse.ResponseCode.UNEXPECTED_REQUEST).build();
            e.getChannel().write(genericResponse3);
            break;
        }
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        api.resetState();

        super.channelDisconnected(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {

        api.resetState();

        logger.error(e.getCause().getMessage());

    }

}
