package org.bitcoma.heartserver.netty.handlers;

import org.bitcoma.hearts.model.transfered.FindGameRoomsProtos.FindGameRoomsRequest;
import org.bitcoma.hearts.model.transfered.FindGameRoomsProtos.FindGameRoomsResponse;
import org.bitcoma.hearts.model.transfered.FindGamesProtos.FindGamesRequest;
import org.bitcoma.hearts.model.transfered.FindGamesProtos.FindGamesResponse;
import org.bitcoma.hearts.model.transfered.GenericProtos.GenericResponse;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginResponse;
import org.bitcoma.hearts.model.transfered.OneMessageProtos.OneMessage;
import org.bitcoma.hearts.model.transfered.ResetPasswordProtos.ResetPasswordRequest;
import org.bitcoma.hearts.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.hearts.model.transfered.StartGameProtos.StartGameRequest;
import org.bitcoma.hearts.model.transfered.StartGameProtos.StartGameResponse;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class OneMessageEncoder extends OneToOneEncoder {

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof FindGameRoomsRequest) {
            return OneMessage.newBuilder().setType(OneMessage.Type.FIND_GAME_ROOMS_REQUEST)
                    .setFindGameRoomsRequest((FindGameRoomsRequest) msg).build();
        } else if (msg instanceof FindGameRoomsResponse) {
            return OneMessage.newBuilder().setType(OneMessage.Type.FIND_GAME_ROOMS_RESPONSE)
                    .setFindGameRoomsResponse((FindGameRoomsResponse) msg).build();
        } else if (msg instanceof FindGamesRequest) {
            return OneMessage.newBuilder().setType(OneMessage.Type.FIND_GAMES_REQUEST)
                    .setFindGamesRequest((FindGamesRequest) msg).build();
        } else if (msg instanceof FindGamesResponse) {
            return OneMessage.newBuilder().setType(OneMessage.Type.FIND_GAMES_RESPONSE)
                    .setFindGamesResponse((FindGamesResponse) msg).build();
        } else if (msg instanceof JoinGameRequest) {
            return OneMessage.newBuilder().setType(OneMessage.Type.JOIN_GAME_REQUEST)
                    .setJoinGameRequest((JoinGameRequest) msg).build();
        } else if (msg instanceof JoinGameResponse) {
            return OneMessage.newBuilder().setType(OneMessage.Type.JOIN_GAME_RESPONSE)
                    .setJoinGameResponse((JoinGameResponse) msg).build();
        } else if (msg instanceof StartGameRequest) {
            return OneMessage.newBuilder().setType(OneMessage.Type.START_GAME_REQUEST)
                    .setStartGameRequest((StartGameRequest) msg).build();
        } else if (msg instanceof StartGameResponse) {
            return OneMessage.newBuilder().setType(OneMessage.Type.START_GAME_RESPONSE)
                    .setStartGameResponse((StartGameResponse) msg).build();
        } else if (msg instanceof LoginRequest) {
            return OneMessage.newBuilder().setType(OneMessage.Type.LOGIN_REQUEST).setLoginRequest((LoginRequest) msg)
                    .build();
        } else if (msg instanceof SignupRequest) {
            return OneMessage.newBuilder().setType(OneMessage.Type.SIGNUP_REQUEST)
                    .setSignupRequest((SignupRequest) msg).build();
        } else if (msg instanceof GenericResponse) {
            return OneMessage.newBuilder().setType(OneMessage.Type.GENERIC_RESPONSE)
                    .setGenericResponse((GenericResponse) msg).build();
        } else if (msg instanceof ResetPasswordRequest) {
            return OneMessage.newBuilder().setType(OneMessage.Type.RESET_PASSWORD_REQUEST)
                    .setResetPasswordRequest((ResetPasswordRequest) msg).build();
        } else if (msg instanceof LoginResponse) {
            return OneMessage.newBuilder().setType(OneMessage.Type.LOGIN_RESPONSE)
                    .setLoginResponse((LoginResponse) msg).build();
        } else {
            return null;
        }
    }
}
