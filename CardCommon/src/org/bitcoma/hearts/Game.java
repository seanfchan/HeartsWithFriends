package org.bitcoma.hearts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Game {

    private Map<Long, Byte> userIdToGameScore;
    private Round currentRound;
    private int roundNumber = 0;
    private IHeartsGameHandler handler;
    private Map<Long, Long> userIdToUserIdPassingMap;
    private ArrayList<Long> tableOrderList;

    public Game(Collection<Long> playerIds, IHeartsGameHandler handler) {

        tableOrderList = new ArrayList<Long>(playerIds.size());
        tableOrderList.addAll(playerIds);

        userIdToGameScore = new HashMap<Long, Byte>(getNumPlayers() + 3);

        for (Long id : playerIds) {
            if (userIdToGameScore.containsKey(id)) {
                throw new IllegalArgumentException("Player ids in game need to all be unique!");
            }
            userIdToGameScore.put(id, (byte) 0);
        }

        // TODO: @sean check if the number of players match the max number of
        // players in a game need to fill with botplayers.
        this.handler = handler;

        createRound();

        do {
            // Game is over so tell those who are listening
            if (isGameOver()) {
                if (handler != null) {
                    handler.handleGameEnded(this);
                }
                break;
            } else if (currentRound.hasRoundEnded()) {
                // Round has ended so we need to start a new round
                if (handler != null) {
                    handler.handleRoundEnded(currentRound);
                }

                createRound();
            }
        } while (currentRound.hasRoundEnded());
    }

    public int getNumPlayers() {
        return tableOrderList.size();
    }

    private void createRound() {
        roundNumber++;

        // Free up some memory
        if (userIdToUserIdPassingMap != null)
            userIdToUserIdPassingMap.clear();
        userIdToUserIdPassingMap = null;

        // Create passing order based on round number.
        // Every fourth round there is no passing
        if (roundNumber % 4 != 0) {
            userIdToUserIdPassingMap = new HashMap<Long, Long>(getNumPlayers() + 3);
            int nextInc;
            if (roundNumber % 4 == 1)
                nextInc = 1; // Pass left
            else if (roundNumber % 4 == 2)
                nextInc = 3; // Pass right
            else
                nextInc = 2; // Pass across

            for (int i = 0; i < tableOrderList.size(); i++) {
                int idx = i;
                int nextIdx = (i + nextInc) % tableOrderList.size();
                userIdToUserIdPassingMap.put(tableOrderList.get(idx), tableOrderList.get(nextIdx));
            }
        }

        currentRound = new Round(tableOrderList, userIdToGameScore, userIdToUserIdPassingMap, this.handler);
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

    public List<Long> getTableOrderList() {
        return tableOrderList;
    }

    public Map<Long, Byte> getUserIdToGameScore() {
        return userIdToGameScore;
    }

    public boolean playCard(Long userId, List<Card> cardsToPlay) {
        boolean result = false;
        if (currentRound != null) {
            result = currentRound.playCard(userId, cardsToPlay);

            if (result) {
                do {
                    // Game is over so tell those who are listening
                    if (isGameOver()) {
                        if (handler != null) {
                            handler.handleGameEnded(this);
                        }
                    } else if (currentRound.hasRoundEnded()) {
                        // Round has ended so we need to start a new round
                        if (handler != null) {
                            handler.handleRoundEnded(currentRound);
                        }

                        createRound();
                    }
                } while (currentRound.hasRoundEnded());
            }
        }

        return result;
    }
}
