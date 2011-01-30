package org.bitcoma.heartserver;

import org.bitcoma.heartserver.model.transfered.JoinGameProtos.JoinGameRequest;
import org.bitcoma.heartserver.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.heartserver.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.heartserver.model.transfered.StartGameProtos.StartGameRequest;
import org.jboss.netty.channel.Channel;

import com.google.protobuf.MessageLite;

public interface IHeartsServerApi {

    MessageLite login(LoginRequest request, Channel channel);

    MessageLite signup(SignupRequest request);

    MessageLite joinGame(JoinGameRequest request);

    MessageLite startGame(StartGameRequest request);

    /**
     * Resets all internal state of logged in user.
     */
    void resetState();

}
