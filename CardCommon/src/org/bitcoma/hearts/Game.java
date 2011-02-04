package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Game {

    Map<Long, Byte> userIdToGameScore;
    Round currentRound;

    public Game(List<Long> playerIds) {

        userIdToGameScore = new HashMap<Long, Byte>();

        for (Long id : playerIds) {
            userIdToGameScore.put(id, (byte) 0);
        }

        // TODO: @sean check if the number of players match the max number of
        // players in a game need to fill with botplayers.

        currentRound = new Round(userIdToGameScore);
    }

    // Returns list of winners
    public List<Long> findWinner() {
        int minScore = 100;
        List<Long> winner = new LinkedList<Long>();

        // Finds all winners in a round
        for (Long playerId : userIdToGameScore.keySet()) {
            // There is no way this can go in the list
            if (userIdToGameScore.get(playerId) > minScore)
                continue;

            // Found a score that is smaller, clearing winners list
            if (userIdToGameScore.get(playerId) < minScore) {
                minScore = userIdToGameScore.get(playerId);
                winner.clear();
            }

            winner.add(playerId);
        }
        return winner;
    }

    // Looking for players with scores greater than or equal to 100
    public boolean isGameOver() {

        for (Long id : userIdToGameScore.keySet()) {
            if (userIdToGameScore.get(id) >= 100)
                return true;
        }
        return false;
    }

    // Get Hand of player by playerId
    public LinkedList<Card> getUserHand(Long id) {
        // If there is a player with this id in this game
        if (userIdToGameScore.containsKey(id))
            return (new LinkedList<Card>(currentRound.getUserIdToHand().get(id)));
        else
            return null;
    }

    public void startNewRound() {
        currentRound = new Round(this.userIdToGameScore);
    }

    public void removeCard(Long userId, Card played) {
        currentRound.removeCard(userId, played);
    }

    public void updateScores(Trick currentTrick) {
        currentRound.updateScores(currentTrick);
    }

    public Map<Long, Byte> getUserIdToScoreInRound() {
        return currentRound.getUserIdToScoreInRound();
    }
}
