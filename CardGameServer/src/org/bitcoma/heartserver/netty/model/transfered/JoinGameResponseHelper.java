package org.bitcoma.heartserver.netty.model.transfered;

import javolution.util.FastMap;

import org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo;
import org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.PlayerInfo;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse;
import org.bitcoma.heartserver.ServerState;
import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.model.database.User;

/**
 * Used to encapsulate common logic in encoding JoinGameResponses
 * 
 * @author Jon
 * 
 */
public class JoinGameResponseHelper {

    /**
     * Convenience method to send join game responses to all clients given game
     * state.
     * 
     * @param gameInstance
     *            GameInstance with all the user info.
     */
    public static void sendResponses(GameInstance gameInstance) {

        FastMap<Long, User> userIdToUserMap = gameInstance.getUserIdToUserMap();
        // Loop through the player ids
        for (Long id : userIdToUserMap.keySet()) {

            GameInfo.Builder tempGameInfo = GameInfo.newBuilder().setGameId(gameInstance.getId())
                    .setMaxNumberOfPlayers(gameInstance.getMaxPlayers());
            // Construct players to send to the client
            for (User u : userIdToUserMap.values()) {
                tempGameInfo.addPlayers(PlayerInfo.newBuilder().setUserId(u.getLongId())
                        .setUserName(u.getString("user_name")).build());
            }

            ServerState.sendToClient(id, JoinGameResponse.newBuilder().setGameInfo(tempGameInfo).build());
        }
    }

}
