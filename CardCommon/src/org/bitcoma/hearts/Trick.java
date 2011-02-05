package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trick {

    public Trick() {
        playerIdToCardMap = new HashMap<Long, Card>();
        suitOfTrick = (byte) 0xFF;
    }

    public boolean isMoveValid(Card cardToPlay, List<Card> playerCards) {
        int numCardsPlayed;
        synchronized (playerIdToCardMap) {
            numCardsPlayed = playerIdToCardMap.size();
        }

        // Set the suit with the first played card
        if (numCardsPlayed == 0) {
            suitOfTrick = cardToPlay.getSuit();
            return true;
        } else {
            // Card matches trick suit.
            if (cardToPlay.getSuit() == suitOfTrick)
                return true;
            else {
                // Do they have a card that can be played?
                for (Card c : playerCards) {
                    if (c.getSuit() == suitOfTrick)
                        return false;
                }
            }
        }

        return true;
    }

    public void makeMove(Long playerId, Card card) {
        synchronized (playerIdToCardMap) {
            playerIdToCardMap.put(playerId, card);
        }
    }

    /*
     * Returns the id of the player that lost this trick
     */
    public Long getLoser() {
        Long playerId;

        synchronized (playerIdToCardMap) {
            playerId = playerIdToCardMap.keySet().iterator().next();
            byte maxRank = 0;

            for (Long key : playerIdToCardMap.keySet()) {
                Card c = playerIdToCardMap.get(key);
                if (c.getSuit() == suitOfTrick) {
                    if (c.getRank() > maxRank) {
                        playerId = key;
                        maxRank = c.getRank();
                    }
                }
            }
        }

        return playerId;
    }

    public Map<Long, Card> getPlayerIdToCardMap() {
        return playerIdToCardMap;
    }

    public int computeScore() {
        int totalScore = 0;
        synchronized (playerIdToCardMap) {
            for (Long key : playerIdToCardMap.keySet()) {
                Card c = playerIdToCardMap.get(key);
                totalScore += c.getPoints();
            }
        }

        return totalScore;
    }

    private byte suitOfTrick;
    private Map<Long, Card> playerIdToCardMap;
}
