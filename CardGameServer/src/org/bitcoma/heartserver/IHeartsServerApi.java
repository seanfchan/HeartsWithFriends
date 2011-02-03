package org.bitcoma.heartserver;

import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest;
import org.bitcoma.hearts.model.transfered.LeaveGameProtos.LeaveGameRequest;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PlayCardRequest;
import org.bitcoma.hearts.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.hearts.model.transfered.StartGameProtos.StartGameRequest;
import org.jboss.netty.channel.Channel;

import com.google.protobuf.MessageLite;

public interface IHeartsServerApi {

    MessageLite login(LoginRequest request, Channel channel);

    MessageLite signup(SignupRequest request);

    MessageLite joinGame(JoinGameRequest request);

    MessageLite leaveGame(LeaveGameRequest request);

    MessageLite startGame(StartGameRequest request);

    MessageLite playCard(PlayCardRequest request);

    /**
     * Resets all internal state of logged in user.
     */
    void resetState();

}
