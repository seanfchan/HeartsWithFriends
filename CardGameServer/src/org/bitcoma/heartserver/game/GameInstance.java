package org.bitcoma.heartserver.game;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.bitcoma.hearts.Card;
import org.bitcoma.heartserver.model.database.User;

public class GameInstance {
    public static enum State {
        ACTIVE, WAITING
    };

    public static long gameCounter = 0;
    private long id;
    private int readyNumPlayers;
    private int currentNumPlayers;
    private int maxPlayers;
    private State gameState;

    FastMap<Long, User> userIdToUserMap;
    FastMap<Long, FastList<Card>> userIdToHandMap;
    FastMap<Long, Byte> userIdToTotalScore;
    FastMap<Long, Byte> userIdToRoundScore;

    public GameInstance(int inputMaxPlayers, State inputState) {
        if (inputMaxPlayers <= 0)
            throw new IllegalArgumentException("Max Players has to be greater than zero.");

        id = getNextId();
        currentNumPlayers = 0;
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

        setCurrentNumPlayers(getCurrentNumPlayers() + 1);
        userIdToUserMap.put(user.getLongId(), user);

        if (getCurrentNumPlayers() == getMaxPlayers()) {
            setGameState(State.ACTIVE);

            // Only initialize this memory when the game is active.
            // Not needed until then
            userIdToHandMap = new FastMap<Long, FastList<Card>>(getMaxPlayers());
            userIdToTotalScore = new FastMap<Long, Byte>(getMaxPlayers());
            userIdToRoundScore = new FastMap<Long, Byte>(getMaxPlayers());
        }

        return true;
    }

    public synchronized boolean removePlayer(User user) {
        // Make sure there are players
        if (getCurrentNumPlayers() < 1)
            return false;

        setCurrentNumPlayers(getCurrentNumPlayers() - 1);
        userIdToUserMap.remove(user.getLongId());

        return true;
    }

    public synchronized int getCurrentNumPlayers() {
        return currentNumPlayers;
    }

    private synchronized void setCurrentNumPlayers(int currentNumPlayers) {
        this.currentNumPlayers = currentNumPlayers;
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

}
