package org.bitcoma.heartserver.netty.task;

import java.util.concurrent.TimeUnit;

import org.bitcoma.heartserver.ServerState;
import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.model.database.User;
import org.bitcoma.heartserver.netty.model.transfered.JoinGameResponseHelper;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class represents a task to add bots to a game after a specified waiting
 * period has expired.
 * 
 * @author Jon
 */
public class GameLobbyTimeOutTask implements TimerTask {

    // Values to use with TimeOutTaskCreator
    public static long DELAY = 30;
    public static TimeUnit UNIT = TimeUnit.SECONDS;

    private static Logger logger = LoggerFactory.getLogger(GameLobbyTimeOutTask.class);

    // Game to add bots
    private final GameInstance gameInstance;

    public GameLobbyTimeOutTask(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    @Override
    public void run(Timeout timeout) throws Exception {

        logger.info("Running timeout task: GameId {}", gameInstance.getId());

        // Remove GameInstance from the waiting games map
        // Check to make sure this still needs bots added
        if (ServerState.waitingGames.remove(gameInstance.getId()) != null || gameInstance.getTimeout() == null) {

            // Remove timeout as it is no longer needed
            gameInstance.setTimeout(null);

            int numBotsAdded = 0;

            // Add bots to the game
            while (!gameInstance.isFull()) {
                User botToAdd = User.selectRandomBot(gameInstance.getUserIdToUserMap().keySet());

                // Check that we actually add a bot.
                if (gameInstance.addPlayer(botToAdd))
                    numBotsAdded++;
            }

            // Add GameInstance to the active games map.
            ServerState.activeGames.put(gameInstance.getId(), gameInstance);

            if (numBotsAdded > 0) {
                logger.info("Timeout task added {} bots.", numBotsAdded);

                // Send the updates to the users.
                JoinGameResponseHelper.sendResponses(gameInstance);
            }
        }
    }
}
