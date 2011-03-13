package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Trick {

    public Trick() {
        playerIdToCardMap = new HashMap<Long, Card>();
        suitOfTrick = (byte) 0xFF;
    }

    public boolean isMoveValid(Long playerId, Card cardToPlay, List<Card> playerCards, Long firstPlayerId,
            boolean bHeartPlayed) {
        int numCardsPlayed = playerIdToCardMap.size();

        // Check if this player already went with this trick
        if (playerIdToCardMap.containsKey(playerId)) {
            System.err.println("Trick: Player already took turn.");
            return false;
        }

        // Check that we have the card we are playing
        if (!playerCards.contains(cardToPlay)) {
            System.err.println("Trick: Playing card not in hand.");
            return false;
        }

        // Check if you have two of clubs. This needs to be played.
        if (playerCards.contains(Card.TWO_CLUBS) && !cardToPlay.equals(Card.TWO_CLUBS)) {
            System.err.println("Trick: Not playing two of clubs.");
            return false;
        }

        // Set the suit with the first played card
        if (numCardsPlayed == 0) {

            // Not the first player
            if (playerId != firstPlayerId) {
                System.err.println("Trick: Playing when not your turn.");
                return false;
            }

            // Heart played as first card of trick
            if (cardToPlay.getSuit() == Card.HEARTS) {
                if (bHeartPlayed) {
                    // Playing heart after hearts are allowed
                } else {
                    for (Card c : playerCards) {
                        // Played a heart to lead with other suits in hand.
                        if (c.getSuit() != Card.HEARTS) {
                            System.err.println("Trick: Playing heart leading trick when other cards available.");
                            return false;
                        }
                    }

                    // Played heart with only hearts in hand
                }
            }
            return true;
        } else {
            // Card matches trick suit.
            if (cardToPlay.getSuit() == suitOfTrick)
                return true;
            else {
                // Do they have a card that can be played?
                for (Card c : playerCards) {
                    if (c.getSuit() == suitOfTrick) {
                        System.err.println("Trick: Card not matching suit when it should.");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public int getNumCards() {
        return playerIdToCardMap.size();
    }

    public void makeMove(Long playerId, Card card) {
        synchronized (playerIdToCardMap) {

            if (playerIdToCardMap.size() == 0)
                suitOfTrick = card.getSuit();

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

    public boolean replacePlayer(Long oldUserId, Long newUserId) {
        if (!playerIdToCardMap.containsKey(oldUserId)) {
            return false;
        }

        Card card = playerIdToCardMap.remove(oldUserId);
        playerIdToCardMap.put(newUserId, card);

        return true;
    }

    public Map<Long, Card> getPlayerIdToCardMap() {
        return playerIdToCardMap;
    }

    public byte getSuitOfTrick() {
        return suitOfTrick;
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

    public String toString() {
        return "Suit: " + Card.suitString(suitOfTrick) + " cards: " + playerIdToCardMap;
    }

    private byte suitOfTrick;
    private Map<Long, Card> playerIdToCardMap;
}
