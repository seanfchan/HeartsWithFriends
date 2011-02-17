package org.bitcoma.heartserver.test;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.bitcoma.hearts.BotPlay;
import org.bitcoma.heartserver.GameLobby;
import org.bitcoma.heartserver.HeartsServerApiImpl;
import org.bitcoma.heartserver.ServerState;
import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.model.database.User;
import org.bitcoma.heartserver.netty.task.GameLobbyTimeOutTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GameLobbyTest {

    // Needed to load metadata from DB for User class
    HeartsServerApiImpl api;

    private long timeoutDelayMs = (GameLobbyTimeOutTask.DELAY + 3) * 1000;

    @Before
    public void setUp() {
        api = new HeartsServerApiImpl();

        api.connectDB();
    }

    @After
    public void tearDown() {
        api.disconnectDB();
    }

    @Test
    public void testJoinGame() {

    }

    @Test
    public void testCreateGame() throws InterruptedException {

        User bot = User.selectRandomBot(new LinkedList<Long>());

        GameInstance game = GameLobby.createGame(bot, ServerState.waitingGames, ServerState.activeGames);

        // Allow the timeout task time to execute
        Thread.sleep(timeoutDelayMs);

        assertTrue("Game should be full after timeout task", game.isFull());
        for (Long id : game.getUserIdToUserMap().keySet()) {
            assertTrue("All players should be bots.", BotPlay.isBot(id));
        }
    }
}
