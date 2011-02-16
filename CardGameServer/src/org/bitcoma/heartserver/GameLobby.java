package org.bitcoma.heartserver;

import javolution.util.FastMap;

import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.game.GameInstance.State;
import org.bitcoma.heartserver.model.database.User;
import org.bitcoma.heartserver.netty.model.transfered.JoinGameResponseHelper;
import org.jboss.netty.util.Timeout;

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

                JoinGameResponseHelper.sendResponses(game);

                // Remove tasks to add bots
                // Need to remove as there is no way to just increase timer.
                Timeout timeout = game.getTimeout();
                if (timeout != null) {
                    timeout.cancel();
                    game.setTimeout(null);
                }

                // Need to switch from waiting to active if game full
                if (game.isFull()) {
                    waitingGames.remove(i.getKey());
                    activeGames.put(i.getKey(), i.getValue());
                }
                // Add task to add bots since game is not full
                else {
                    // TODO: @Jon add timer task to add bots.
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

        // TODO: @Jon Add timer task to add bots

        return game;
    }
}
