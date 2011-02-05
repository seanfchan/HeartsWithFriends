package org.bitcoma.heartserver.game;

import java.util.List;

import javolution.util.FastMap;

import org.bitcoma.hearts.Card;
import org.bitcoma.hearts.Game;
import org.bitcoma.heartserver.model.database.User;

public class GameInstance {
    public static enum State {
        ACTIVE, WAITING
    };

    public static long gameCounter = 0;
    private long id;
    private int readyNumPlayers;
    private int maxPlayers;
    private State gameState;

    private Game gameInfo;

    FastMap<Long, User> userIdToUserMap;

    public GameInstance(int inputMaxPlayers, State inputState) {
        if (inputMaxPlayers <= 0)
            throw new IllegalArgumentException("Max Players has to be greater than zero.");

        id = getNextId();
        readyNumPlayers = 0;
        maxPlayers = inputMaxPlayers;
        gameState = inputState;

        userIdToUserMap = new FastMap<Long, User>(inputMaxPlayers);
    }

    private synchronized Long getNextId() {
        if (gameCounter == Long.MAX_VALUE)
            gameCounter = 0;

        return gameCounter++;
    }

    public synchronized boolean addPlayer(User user) {
        // Make sure there is room for the player
        if (getCurrentNumPlayers() >= getMaxPlayers())
            return false;

        userIdToUserMap.put(user.getLongId(), user);

        if (getCurrentNumPlayers() == getMaxPlayers()) {
            setGameState(State.ACTIVE);

            gameInfo = new Game(userIdToUserMap.keySet());
        }

        return true;
    }

    public synchronized boolean removePlayer(User user) {
        // Make sure there are players
        if (getCurrentNumPlayers() < 1)
            return false;

        userIdToUserMap.remove(user.getLongId());

        return true;
    }

    public synchronized int getCurrentNumPlayers() {
        return userIdToUserMap.size();
    }

    public synchronized State getGameState() {
        return gameState;
    }

    private synchronized void setGameState(State state) {
        this.gameState = state;
    }

    public synchronized boolean addReadyPlayer() {
        if (readyNumPlayers < getMaxPlayers()) {
            readyNumPlayers++;
            return true;
        }
        return false;
    }

    public synchronized int getReadyNumPlayers() {
        return readyNumPlayers;
    }

    public Long getId() {
        return id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public FastMap<Long, User> getUserIdToUserMap() {
        return userIdToUserMap;
    }

    public List<Card> getUserHand(Long id) {
        if (gameInfo != null) {
            return gameInfo.getUserHand(id);
        } else {
            return null;
        }
    }

}
