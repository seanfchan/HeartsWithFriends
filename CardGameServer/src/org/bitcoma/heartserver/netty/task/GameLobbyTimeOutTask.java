package org.bitcoma.heartserver.netty.task;

import org.bitcoma.heartserver.ServerState;
import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.model.database.User;
import org.bitcoma.heartserver.netty.model.transfered.JoinGameResponseHelper;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;

/**
 * Class represents a task to add bots to a game after a specified waiting
 * period has expired.
 * 
 * @author Jon
 */
public class GameLobbyTimeOutTask implements TimerTask {

    // Game to add bots
    private final GameInstance gameInstance;

    public GameLobbyTimeOutTask(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    @Override
    public void run(Timeout timeout) throws Exception {

        // Remove GameInstance from the waiting games map
        ServerState.waitingGames.remove(gameInstance.getId());

        // Remove timeout as it is no longer needed
        gameInstance.setTimeout(null);

        // Add bots to the game
        while (!gameInstance.isFull()) {
            User botToAdd = User.selectRandomBot(gameInstance.getUserIdToUserMap().keySet());
            gameInstance.addPlayer(botToAdd);
        }

        // Add GameInstance to the active games map.
        ServerState.activeGames.put(gameInstance.getId(), gameInstance);

        // Send the updates to the users.
        JoinGameResponseHelper.sendResponses(gameInstance);
    }
}
