package org.bitcoma.heartserver;

import javolution.util.FastMap;

import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.game.GameInstance.State;
import org.bitcoma.heartserver.model.database.User;
import org.bitcoma.heartserver.netty.model.transfered.JoinGameResponseHelper;
import org.bitcoma.heartserver.netty.task.GameLobbyTimeOutTask;
import org.bitcoma.heartserver.netty.task.TimeOutTaskCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLobby {

    private static Logger logger = LoggerFactory.getLogger(GameLobby.class);

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
        for (Long gameId : waitingGames.keySet()) {
            GameInstance game = waitingGames.get(gameId);

            // Did we successfully add a player?
            if (game.addPlayer(user)) {

                logger.info("User: {} joined to GameInstance: {}", user, game);

                // Remove tasks to add bots
                // Need to remove as there is no way to just increase timer.
                game.setTimeout(null);

                // Need to switch from waiting to active if game full
                if (game.isFull()) {
                    logger.info("GameInstance: {} is full. Changing to active game.", game);

                    waitingGames.remove(gameId);
                    activeGames.put(gameId, game);
                }
                // Add task to add bots since game is not full
                else {
                    game.setTimeout(TimeOutTaskCreator.createTask(new GameLobbyTimeOutTask(game),
                            GameLobbyTimeOutTask.DELAY, GameLobbyTimeOutTask.UNIT));
                }

                JoinGameResponseHelper.sendResponses(game);

                return game;
            }
        }

        logger.error("Could not added User: {} to waiting game", user);

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

        logger.info("GameInstance: {} created for User: {}", game, user);

        FastMap.Entry<Long, GameInstance> temp;
        // Single-player game
        // if (game.getCurrentNumPlayers() == game.getMaxPlayers())
        // temp = activeGames.putEntry(game.getId(), game);
        // Multi-player game
        // else
        temp = waitingGames.putEntry(game.getId(), game);

        if (temp == FastMap.Entry.NULL)
            return null;

        // Add task to add bots
        game.setTimeout(TimeOutTaskCreator.createTask(new GameLobbyTimeOutTask(game), GameLobbyTimeOutTask.DELAY,
                GameLobbyTimeOutTask.UNIT));

        return game;
    }
}
