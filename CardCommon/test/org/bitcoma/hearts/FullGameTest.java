package org.bitcoma.hearts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bitcoma.hearts.model.PassingCardsInfo;
import org.junit.Test;

public class FullGameTest implements IHeartsGameHandler {

    private static final int NUM_GAMES = 100;
    private List<Long> playerIds;
    private int roundCount;
    private int gameCount;
    Game game;
    boolean gameEnded;
    int roundStartedCount;
    int roundEndedCount;
    int trickEndedCount;

    @Test
    public void testFullBotGame() {

        playerIds = new LinkedList<Long>();
        playerIds.add((long) 1);
        playerIds.add((long) 2);
        playerIds.add((long) 3);
        playerIds.add((long) 4);

        for (gameCount = 0; gameCount < NUM_GAMES; gameCount++) {

            roundCount = 0;
            gameEnded = false;
            roundStartedCount = roundEndedCount = trickEndedCount = 0;
            game = new Game(playerIds, this);

            assertTrue("Game better have ended successfully.", gameEnded);
            assertEquals("Round started count should equal round count", roundStartedCount, roundCount);
            assertTrue("Round ended count should be equal round count or round count minus 1",
                    roundEndedCount >= roundCount - 1);

        }
    }

    public int getNumPlayers() {
        return playerIds.size();
    }

    @Override
    public void handleSingleCardPlayed(Long srcId, Card cardPlayed, Long nextPlayerId) {
        assertNotNull("Player playing card needs to be not null.", srcId);
        assertNotNull("Next player id needs to be not null.", nextPlayerId);
        assertTrue("Next player id needs to be in the game.", playerIds.contains(nextPlayerId));
        assertTrue("Player playing card needs to be in the game.", playerIds.contains(nextPlayerId));
    }

    @Override
    public void handleCardsPassed(List<PassingCardsInfo> passingCardInfo, Long firstPlayerId) {
        assertTrue("No passing in every fourth round.", roundCount % 4 != 0);

        // Check we are passing to the correct person.
        int incPerson;
        if (roundCount % 4 == 1)
            incPerson = 1;
        else if (roundCount % 4 == 2)
            incPerson = 3;
        else
            incPerson = 2;

        for (PassingCardsInfo pci : passingCardInfo) {
            assertTrue("Passing from person in game.", playerIds.contains(pci.srcId));
            assertTrue("Passing to person in game.", playerIds.contains(pci.dstId));

            int playerIdx = playerIds.indexOf(pci.srcId);
            Long expectedDst = playerIds.get((playerIdx + incPerson) % getNumPlayers());
            assertEquals("Passing to correct person.", expectedDst, (Long) pci.dstId);
        }

        assertNotNull("First player id better be a value.", firstPlayerId);
        assertTrue("First player better be in game.", playerIds.contains(firstPlayerId));

    }

    @Override
    public void handleScoreUpdate(Map<Long, Byte> userIdToGameScore, Map<Long, Byte> userIdToRoundScore) {

        checkPlayerIdsAreSame(userIdToGameScore.keySet());
        checkPlayerIdsAreSame(userIdToRoundScore.keySet());

        // Basic sanity checks on score.
        for (Long id : userIdToGameScore.keySet()) {
            assertTrue("Game score should be >= to round score.",
                    userIdToGameScore.get(id) >= userIdToRoundScore.get(id));
        }
    }

    @Override
    public void handleTrickEnded(Trick finishedTrick) {
        trickEndedCount++;

        Long loserId = finishedTrick.getLoser();

        assertNotNull("Loser id should be valid.", loserId);
        assertTrue("Loser id should be one of the players.", playerIds.contains(loserId));

        // Every player needs to make a move.
        Map<Long, Card> cardMap = finishedTrick.getPlayerIdToCardMap();
        checkPlayerIdsAreSame(cardMap.keySet());
    }

    @Override
    public void handleRoundEnded(Round finishedRound) {
        roundEndedCount++;

        assertTrue("Trick ended count should equal 13.", trickEndedCount == 13);
        assertTrue("Round should have ended in callback.", finishedRound.hasRoundEnded());

        if (roundCount % 4 != 0) {
            assertTrue("This should be a passing round.", finishedRound.isPassingRound());
            assertNotNull("Should have a passing map.", finishedRound.getUserIdToUserIdPassingMap());
            checkPlayerIdsAreSame(finishedRound.getUserIdToUserIdPassingMap().keySet());
        } else {
            assertFalse("This should not be a passing round.", finishedRound.isPassingRound());
            assertNull("Should not have a passing map.", finishedRound.getUserIdToUserIdPassingMap());
        }

        checkPlayerIdsAreSame(finishedRound.getUserIdToHand().keySet());
        checkPlayerIdsAreSame(finishedRound.getUserIdToScoreInRound().keySet());
    }

    @Override
    public void handleRoundStarted(Round startedRound) {
        roundCount++;
        roundStartedCount++;
        trickEndedCount = 0;

        if (roundCount % 4 != 0) {
            assertTrue("This should be a passing round.", startedRound.isPassingRound());
            assertNotNull("Should have a passing map.", startedRound.getUserIdToUserIdPassingMap());
            checkPlayerIdsAreSame(startedRound.getUserIdToUserIdPassingMap().keySet());
        } else {
            assertFalse("This should not be a passing round.", startedRound.isPassingRound());
            assertNull("Should not have a passing map.", startedRound.getUserIdToUserIdPassingMap());
        }

        checkPlayerIdsAreSame(startedRound.getUserIdToHand().keySet());
        checkPlayerIdsAreSame(startedRound.getUserIdToScoreInRound().keySet());
    }

    @Override
    public void handleGameEnded(Game finishedGame) {
        gameEnded = true;
        assertTrue("Game should have finished in gameEnded callback", finishedGame.isGameOver());

        int loserCount = 0;
        Map<Long, Byte> userScoreMap = finishedGame.getUserIdToGameScore();
        for (Long id : userScoreMap.keySet()) {
            if (userScoreMap.get(id) >= 100) {
                loserCount++;
            }
        }
        assertTrue("There has to be at least one player with 100+ points", loserCount > 0);

        checkPlayerIdsAreSame(userScoreMap.keySet());
        checkPlayerIdsAreSame(finishedGame.getTableOrderList());

        System.out.println("Game " + gameCount + " Summary: Num Rounds " + roundCount);
        for (Long id : userScoreMap.keySet()) {
            System.out.print(id + ": " + userScoreMap.get(id) + " ");
        }
        System.out.println();
    }

    public void checkPlayerIdsAreSame(Collection<Long> callbackPlayerIds) {

        assertTrue("Number of players should match.", callbackPlayerIds.size() == playerIds.size());

        // Check that all the players are the same.
        for (Long id : playerIds) {
            if (!callbackPlayerIds.contains(id)) {
                fail("Players should be the same at the end of the game");
            }
        }
    }

}
