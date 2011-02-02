package org.bitcoma.heartsClient;

import org.bitcoma.hearts.model.transfered.FindGameRoomsProtos.FindGameRoomsRequest;
import org.bitcoma.hearts.model.transfered.FindGameRoomsProtos.FindGameRoomsResponse;
import org.bitcoma.hearts.model.transfered.FindGamesProtos.FindGamesRequest;
import org.bitcoma.hearts.model.transfered.FindGamesProtos.FindGamesResponse;
import org.bitcoma.hearts.model.transfered.GenericProtos.GenericResponse;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginResponse;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PlayCardRequest;
import org.bitcoma.hearts.model.transfered.ResetPasswordProtos.ResetPasswordRequest;
import org.bitcoma.hearts.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.hearts.model.transfered.StartGameProtos.StartGameRequest;
import org.bitcoma.hearts.model.transfered.StartGameProtos.StartGameResponse;

import com.google.protobuf.MessageLite;

public abstract class HeartsProtoHandler {

    private boolean warnUnexpected;

    public HeartsProtoHandler() {
        this(true);
    }

    public HeartsProtoHandler(boolean warnUnexpectedResponses) {
        warnUnexpected = warnUnexpectedResponses;
    }

    /**
     * Default handler used for all Messages.
     * 
     * @param msg
     *            Message received by the server and sent to handler
     */
    protected void defaultHandler(MessageLite msg) {
        if (warnUnexpected)
            handleUnexpectedMessage(msg);
    }

    /**
     * Method used to communicate messages that are unexpected or unimplemented
     * when they should be.
     * 
     * @param msg
     *            Message received my the server and sent to handler.
     */
    abstract public void handleUnexpectedMessage(MessageLite msg);

    /**
     * Method used to show exceptions from networking code to the handlers so
     * they can take necessary action.
     * 
     * @param throwable
     *            Cause of the exception
     */
    abstract public void handleThrowable(Throwable throwable);

    public void handleSignupRequest(SignupRequest request) {
        defaultHandler(request);
    }

    public void handleLoginRequest(LoginRequest request) {
        defaultHandler(request);
    }

    public void handleLoginResponse(LoginResponse response, MessageLite origRequest) {
        defaultHandler(response);
    }

    public void handleGenericResponse(GenericResponse response, MessageLite origRequest) {
        defaultHandler(response);
    }

    public void handleJoinGameRequest(JoinGameRequest request) {
        defaultHandler(request);
    }

    public void handleJoinGameResponse(JoinGameResponse response, MessageLite origRequest) {
        defaultHandler(response);
    }

    public void handlePlayCardRequest(PlayCardRequest request) {
        defaultHandler(request);
    }

    public void handleResetPasswordRequest(ResetPasswordRequest request) {
        defaultHandler(request);
    }

    public void handleStartGameRequest(StartGameRequest request) {
        defaultHandler(request);
    }

    public void handleStartGameResponse(StartGameResponse response, MessageLite origRequest) {
        defaultHandler(response);
    }

    public void handleFindGamesRequest(FindGamesRequest request) {
        defaultHandler(request);
    }

    public void handleFindGamesResponse(FindGamesResponse response, MessageLite origRequest) {
        defaultHandler(response);
    }

    public void handleFindGameRoomsRequest(FindGameRoomsRequest request) {
        defaultHandler(request);
    }

    public void handleFindGameRoomsResponse(FindGameRoomsResponse response, MessageLite origRequest) {
        defaultHandler(response);
    }

}
