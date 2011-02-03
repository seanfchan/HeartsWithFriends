package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Game {

    Map<Long, Integer> playerIdToGameScore;
    Round currentRound;

    public Game(List<Long> playerIds) {

        playerIdToGameScore = new HashMap<Long, Integer>();

        for (Long id : playerIds) {
            playerIdToGameScore.put(id, 0);
        }

        // TODO: @sean check if the number of players match the max number of
        // players in a game need to fill with botplayers.

        currentRound = new Round(playerIdToGameScore);
    }

    // Returns list of winners
    public List<Long> findWinner() {
        int minScore = 100;
        List<Long> winner = new LinkedList<Long>();

        // Finds all winners in a round
        for (Long playerId : playerIdToGameScore.keySet()) {
            // There is no way this can go in the list
            if (playerIdToGameScore.get(playerId) > minScore)
                continue;

            // Found a score that is smaller, clearing winners list
            if (playerIdToGameScore.get(playerId) < minScore) {
                minScore = playerIdToGameScore.get(playerId);
                winner.clear();
            }

            winner.add(playerId);
        }
        return winner;
    }

    public boolean isGameOver() {
        Map<Long, LinkedList<Card>> userIdToHand = currentRound.getUserIdToHand();
        for (Long id : userIdToHand.keySet()) {
            if (!userIdToHand.get(id).isEmpty())
                return false;
        }
        return true;
    }

    // Get Hand of player by playerId
    public LinkedList<Card> getUserHand(Long id) {
        if (playerIdToGameScore.containsKey(id))
            return null;
        else
            return (new LinkedList<Card>(currentRound.getUserIdToHand().get(id)));
    }

    // Play card(s) input: userid, list of cards
    public void playCard(Long id, LinkedList<Card> cardsToPlay) {
        // TODO
    }

}
