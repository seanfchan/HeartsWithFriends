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

        this.handler = handler;

        createRound();

        do {
            // Game is over so tell those who are listening
            if (isGameOver()) {
                if (handler != null) {
                    handler.handleGameEnded(this);
                }
                break;
            } else if (currentRound.isRoundOver()) {
                // Round has ended so we need to start a new round
                if (handler != null) {
                    handler.handleRoundEnded(currentRound);
                }

                createRound();
            }
        } while (currentRound.isRoundOver() || isGameOver());
    }

    public synchronized int getNumPlayers() {
        return tableOrderList.size();
    }

    private void createRound() {
        roundNumber++;

        // Zero out previous passing rounds
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

        currentRound = new Round(tableOrderList, userIdToGameScore, userIdToUserIdPassingMap, handler);
    }

    // Returns list of winners
    public synchronized List<Long> findWinner() {
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
    public synchronized boolean isGameOver() {
        for (Long id : userIdToGameScore.keySet()) {
            if (userIdToGameScore.get(id) >= 100)
                return true;
        }
        return false;
    }

    // Get Hand of player by playerId
    public synchronized LinkedList<Card> getUserHand(Long id) {
        // If there is a player with this id in this game
        if (userIdToGameScore.containsKey(id))
            return currentRound.getUserIdToHand().get(id);
        else
            return null;
    }

    public synchronized List<Long> getTableOrderList() {
        return tableOrderList;
    }

    public synchronized Map<Long, Byte> getUserIdToGameScore() {
        return userIdToGameScore;
    }

    public synchronized Round getCurrentRound() {
        return currentRound;
    }

    public boolean replacePlayer(Long oldUserId, Long newUserId) {
        if (!userIdToGameScore.containsKey(oldUserId)) {
            return false;
        }

        boolean result = false;

        synchronized (this) {
            // Update internal game info and then update round.
            int oldTablePosition = tableOrderList.indexOf(oldUserId);
            tableOrderList.set(oldTablePosition, newUserId);

            byte gameScore = userIdToGameScore.remove(oldUserId);
            Long userToPassTo = userIdToUserIdPassingMap.remove(oldUserId);

            userIdToGameScore.put(newUserId, gameScore);
            userIdToUserIdPassingMap.put(newUserId, userToPassTo);

            if (currentRound != null) {
                result = currentRound.replacePlayer(oldUserId, newUserId);
            }
        }

        // Handler doesn't handle this case as it is needed only for server and
        // server specific info is needed.

        return result;
    }

    public synchronized boolean addToScore(Long userId, byte scoreToAdd) {
        if (currentRound != null) {
            currentRound.addToScore(userId, scoreToAdd);
            return true;
        }

        return false;
    }

    public synchronized boolean playCard(Long userId, List<Card> cardsToPlay) {
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
                        break;
                    } else if (currentRound.isRoundOver()) {
                        // Round has ended so we need to start a new round
                        if (handler != null) {
                            handler.handleRoundEnded(currentRound);
                        }

                        createRound();
                    }
                } while (currentRound.isRoundOver() || isGameOver());
            }
        }

        return result;
    }
}
