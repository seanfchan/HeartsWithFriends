package org.bitcoma.heartserver.test;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import javolution.util.FastMap;

import org.bitcoma.hearts.BotPlay;
import org.bitcoma.heartserver.GameLobby;
import org.bitcoma.heartserver.HeartsServerApiImpl;
import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.model.database.User;
import org.bitcoma.heartserver.netty.task.GameLobbyTimeOutTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GameLobbyTest {

    private FastMap<Long, GameInstance> waitingGames;
    private FastMap<Long, GameInstance> activeGames;

    // Needed to load metadata from DB for User class
    HeartsServerApiImpl api;

    private long timeoutDelayMs = (GameLobbyTimeOutTask.DELAY + 2) * 1000;

    @Before
    public void setUp() {
        api = new HeartsServerApiImpl();

        waitingGames = new FastMap<Long, GameInstance>().shared();
        activeGames = new FastMap<Long, GameInstance>().shared();

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

        GameInstance game = GameLobby.createGame(bot, waitingGames, activeGames);

        // Allow the timeout task time to execute
        Thread.sleep(timeoutDelayMs);

        assertTrue("Game should be full after timeout task", game.isFull());
        for (Long id : game.getUserIdToUserMap().keySet()) {
            assertTrue("All players should be bots.", BotPlay.isBot(id));
        }
    }
}
