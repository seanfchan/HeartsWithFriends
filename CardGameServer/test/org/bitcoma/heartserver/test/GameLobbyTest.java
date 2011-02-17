package org.bitcoma.heartserver.test;

import static org.junit.Assert.assertEquals;
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
    public void testJoinGame() throws InterruptedException {

        GameInstance game = new GameInstance(4, GameInstance.State.WAITING);
        ServerState.waitingGames.put(game.getId(), game);

        LinkedList<Long> botIds = new LinkedList<Long>();
        User bot1 = User.selectRandomBot(botIds);
        botIds.add(bot1.getLongId());
        User bot2 = User.selectRandomBot(botIds);
        botIds.add(bot2.getLongId());
        User bot3 = User.selectRandomBot(botIds);
        botIds.add(bot3.getLongId());
        User bot4 = User.selectRandomBot(botIds);
        botIds.add(bot4.getLongId());

        GameInstance joinedGame = GameLobby.joinGame(bot1, ServerState.waitingGames, ServerState.activeGames);
        assertEquals("Game created and game joined should match.", game, joinedGame);
        assertEquals("Game should have one player", 1, joinedGame.getCurrentNumPlayers());

        joinedGame = GameLobby.joinGame(bot2, ServerState.waitingGames, ServerState.activeGames);
        assertEquals("Game created and game joined should match.", game, joinedGame);
        assertEquals("Game should have two players", 2, joinedGame.getCurrentNumPlayers());

        // Allow time for timer to run out and fill with bots.
        Thread.sleep(timeoutDelayMs);

        assertTrue("Game should be full after timeout task", game.isFull());
        for (Long id : game.getUserIdToUserMap().keySet()) {
            assertTrue("All players should be bots.", BotPlay.isBot(id));
        }

        assertTrue("Game should now be considered active.", ServerState.activeGames.containsKey(game.getId()));
        assertTrue("Game should be in the SYNCING_START state", GameInstance.State.SYNCING_START == game.getGameState());
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

        assertTrue("Game should now be considered active.", ServerState.activeGames.containsKey(game.getId()));
        assertTrue("Game should be in the SYNCING_START state", GameInstance.State.SYNCING_START == game.getGameState());
    }
}
