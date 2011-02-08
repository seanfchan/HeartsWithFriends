package org.bitcoma.hearts.model.transfered;

import org.bitcoma.hearts.model.transfered.FindGameRoomsProtos.FindGameRoomsRequest;
import org.bitcoma.hearts.model.transfered.FindGameRoomsProtos.FindGameRoomsResponse;
import org.bitcoma.hearts.model.transfered.FindGamesProtos.FindGamesRequest;
import org.bitcoma.hearts.model.transfered.FindGamesProtos.FindGamesResponse;
import org.bitcoma.hearts.model.transfered.GameProtos.GameEndedResponse;
import org.bitcoma.hearts.model.transfered.GenericProtos.GenericResponse;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginResponse;
import org.bitcoma.hearts.model.transfered.OneMessageProtos.OneMessage;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PassCardsResponse;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PlayCardRequest;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PlaySingleCardResponse;
import org.bitcoma.hearts.model.transfered.ResetPasswordProtos.ResetPasswordRequest;
import org.bitcoma.hearts.model.transfered.RoundProtos.RoundEndedResponse;
import org.bitcoma.hearts.model.transfered.RoundProtos.RoundStartedResponse;
import org.bitcoma.hearts.model.transfered.ScoreUpdateProtos.ScoreUpdateResponse;
import org.bitcoma.hearts.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.hearts.model.transfered.StartGameProtos.StartGameRequest;
import org.bitcoma.hearts.model.transfered.TrickProtos.TrickEndedResponse;

public class OneMessageWrapper {

    private int messageId;
    private Object data;

    public OneMessageWrapper(int messageId, Object data) {
        setMessageId(messageId);
        setData(data);
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    // Takes an object and wraps in the OneMessage union for sending over the
    // wire. Placed here as client and server will need this functionality.
    public static Object wrapMessage(Object message) {

        Object msg = message;
        int messageId = 0;

        // If we specify message id from OneMessageWrapper then use it.
        // Otherwise just use a message id of 0. Ease of use on server.
        if (message instanceof OneMessageWrapper) {
            OneMessageWrapper wrapper = (OneMessageWrapper) message;
            msg = wrapper.getData();
            messageId = wrapper.getMessageId();
        }

        OneMessage.Builder builder = OneMessage.newBuilder().setMessageId(messageId);

        if (msg instanceof FindGameRoomsRequest) {
            return builder.setType(OneMessage.Type.FIND_GAME_ROOMS_REQUEST)
                    .setFindGameRoomsRequest((FindGameRoomsRequest) msg).build();
        } else if (msg instanceof FindGameRoomsResponse) {
            return builder.setType(OneMessage.Type.FIND_GAME_ROOMS_RESPONSE)
                    .setFindGameRoomsResponse((FindGameRoomsResponse) msg).build();
        } else if (msg instanceof FindGamesRequest) {
            return builder.setType(OneMessage.Type.FIND_GAMES_REQUEST).setFindGamesRequest((FindGamesRequest) msg)
                    .build();
        } else if (msg instanceof FindGamesResponse) {
            return builder.setType(OneMessage.Type.FIND_GAMES_RESPONSE).setFindGamesResponse((FindGamesResponse) msg)
                    .build();
        } else if (msg instanceof JoinGameRequest) {
            return builder.setType(OneMessage.Type.JOIN_GAME_REQUEST).setJoinGameRequest((JoinGameRequest) msg).build();
        } else if (msg instanceof JoinGameResponse) {
            return builder.setType(OneMessage.Type.JOIN_GAME_RESPONSE).setJoinGameResponse((JoinGameResponse) msg)
                    .build();
        } else if (msg instanceof StartGameRequest) {
            return builder.setType(OneMessage.Type.START_GAME_REQUEST).setStartGameRequest((StartGameRequest) msg)
                    .build();
        } else if (msg instanceof LoginRequest) {
            return builder.setType(OneMessage.Type.LOGIN_REQUEST).setLoginRequest((LoginRequest) msg).build();
        } else if (msg instanceof SignupRequest) {
            return builder.setType(OneMessage.Type.SIGNUP_REQUEST).setSignupRequest((SignupRequest) msg).build();
        } else if (msg instanceof GenericResponse) {
            return builder.setType(OneMessage.Type.GENERIC_RESPONSE).setGenericResponse((GenericResponse) msg).build();
        } else if (msg instanceof ResetPasswordRequest) {
            return builder.setType(OneMessage.Type.RESET_PASSWORD_REQUEST)
                    .setResetPasswordRequest((ResetPasswordRequest) msg).build();
        } else if (msg instanceof LoginResponse) {
            return builder.setType(OneMessage.Type.LOGIN_RESPONSE).setLoginResponse((LoginResponse) msg).build();
        } else if (msg instanceof PlayCardRequest) {
            return builder.setType(OneMessage.Type.PLAY_CARD_REQUEST).setPlayCardRequest((PlayCardRequest) msg).build();
        } else if (msg instanceof GameEndedResponse) {
            return builder.setType(OneMessage.Type.GAME_ENDED_RESPONSE).setGameEndedResponse((GameEndedResponse) msg)
                    .build();
        } else if (msg instanceof RoundEndedResponse) {
            return builder.setType(OneMessage.Type.ROUND_ENDED_RESPONSE)
                    .setRoundEndedResponse((RoundEndedResponse) msg).build();
        } else if (msg instanceof RoundStartedResponse) {
            return builder.setType(OneMessage.Type.ROUND_STARTED_RESPONSE)
                    .setRoundStartedResponse((RoundStartedResponse) msg).build();
        } else if (msg instanceof TrickEndedResponse) {
            return builder.setType(OneMessage.Type.TRICK_ENDED_RESPONSE)
                    .setTrickEndedResponse((TrickEndedResponse) msg).build();
        } else if (msg instanceof ScoreUpdateResponse) {
            return builder.setType(OneMessage.Type.SCORE_UPDATE_RESPONSE)
                    .setScoreUpdateResponse((ScoreUpdateResponse) msg).build();
        } else if (msg instanceof PlaySingleCardResponse) {
            return builder.setType(OneMessage.Type.PLAY_SINGLE_CARD_RESPONSE)
                    .setPlaySingleCardResponse((PlaySingleCardResponse) msg).build();
        } else if (msg instanceof PassCardsResponse) {
            return builder.setType(OneMessage.Type.PASS_CARDS_RESPONSE).setPassCardsResponse((PassCardsResponse) msg)
                    .build();
        } else {
            return null;
        }
    }

}
