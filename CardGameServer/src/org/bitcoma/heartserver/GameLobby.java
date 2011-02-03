package org.bitcoma.heartserver;

import javolution.util.FastMap;

import org.bitcoma.hearts.model.transfered.GameProtos.GameInfo;
import org.bitcoma.hearts.model.transfered.GameProtos.GameInfo.PlayerInfo;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse;
import org.bitcoma.hearts.model.transfered.OneMessageWrapper;
import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.game.GameInstance.State;
import org.bitcoma.heartserver.model.database.User;
import org.jboss.netty.channel.Channel;

public class GameLobby {

    /**
     * Looks for a available game in the set of waiting games.
     * 
     * @param user
     *            User to add to a game
     * @param waitingGames
     *            Set of available games to choose from
     * @param activeGames
     *            Set of active games to add to.
     * @return GameInstance that was joined, otherwise null.
     */
    public static GameInstance joinGame(User user, FastMap<Long, GameInstance> waitingGames,
            FastMap<Long, GameInstance> activeGames) {
        for (FastMap.Entry<Long, GameInstance> i = waitingGames.head(), end = waitingGames.tail(); i != end; i = i
                .getNext()) {
            GameInstance game = i.getValue();

            // Did we successfully add a player?
            if (game.addPlayer(user)) {

                FastMap<Long, User> userIdToUserMap = game.getUserIdToUserMap();
                // Loop through the player ids
                for (FastMap.Entry<Long, User> temp = userIdToUserMap.head(); temp != FastMap.Entry.NULL; temp = temp
                        .getNext()) {
                    Channel writePipe = ServerState.userIdToChannelMap.get(temp.getKey());

                    GameInfo.Builder tempGameInfo = GameInfo.newBuilder().setGameId(game.getId())
                            .setMaxNumberOfPlayers(game.getMaxPlayers());
                    // Construct players to send to the client
                    for (User u : game.getUserIdToUserMap().values()) {
                        tempGameInfo.addPlayers(PlayerInfo.newBuilder().setUserId(u.getLongId())
                                .setUserName(u.getString("user_name")).build());
                    }
                    // NOTE: message id is zero because we are sending to other
                    // clients.
                    writePipe.write(new OneMessageWrapper(0, JoinGameResponse.newBuilder().setGameInfo(tempGameInfo)
                            .build()));
                }

                // Need to switch from waiting to active if game full
                if (game.getCurrentNumPlayers() == game.getMaxPlayers()) {
                    waitingGames.remove(i.getKey());
                    activeGames.put(i.getKey(), i.getValue());
                }

                return game;
            }

        }
        return null;
    }

    /**
     * Creates a game instance.
     * 
     * @param waitingGames
     *            Set of games to add to.
     * @param activeGames
     *            Not currently used
     * @return GameInstance that was created, otherwise null
     */
    public static GameInstance createGame(User user, FastMap<Long, GameInstance> waitingGames,
            FastMap<Long, GameInstance> activeGames) {
        // TODO: @sean should really put the number of players in the request
        GameInstance game = new GameInstance(4, State.WAITING);
        game.addPlayer(user);

        FastMap.Entry<Long, GameInstance> temp;
        // Single-player game
        // if (game.getCurrentNumPlayers() == game.getMaxPlayers())
        // temp = activeGames.putEntry(game.getId(), game);
        // Multi-player game
        // else
        temp = waitingGames.putEntry(game.getId(), game);

        if (temp == FastMap.Entry.NULL)
            return null;

        return game;
    }
}
