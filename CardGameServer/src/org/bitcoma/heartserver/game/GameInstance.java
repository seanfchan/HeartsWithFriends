package org.bitcoma.heartserver.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.bitcoma.hearts.Card;
import org.bitcoma.hearts.Game;
import org.bitcoma.hearts.IHeartsGameHandler;
import org.bitcoma.hearts.Round;
import org.bitcoma.hearts.Trick;
import org.bitcoma.hearts.model.PassingCardsInfo;
import org.bitcoma.hearts.model.transfered.GameProtos.GameEndedResponse;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PassCardsResponse;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PlaySingleCardResponse;
import org.bitcoma.hearts.model.transfered.RoundProtos.RoundEndedResponse;
import org.bitcoma.hearts.model.transfered.RoundProtos.RoundStartedResponse;
import org.bitcoma.hearts.model.transfered.ScoreUpdateProtos.ScoreUpdateResponse;
import org.bitcoma.hearts.model.transfered.ScoreUpdateProtos.ScoreUpdateResponse.ScoreInfo;
import org.bitcoma.hearts.model.transfered.TrickProtos.TrickEndedResponse;
import org.bitcoma.heartserver.ServerState;
import org.bitcoma.heartserver.model.database.User;

public class GameInstance implements IHeartsGameHandler {
    public static enum State {
        PLAYING, SYNCING_START, WAITING
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
            setGameState(State.SYNCING_START);
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

            if (readyNumPlayers == getMaxPlayers()) {
                setGameState(State.PLAYING);

                gameInfo = new Game(userIdToUserMap.keySet(), this);
            }
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

    @Override
    public void handleSingleCardPlayed(Long srcId, Card cardPlayed) {
        // Send card played to all clients
        PlaySingleCardResponse response = PlaySingleCardResponse
                .newBuilder()
                .setCardPlayed(
                        org.bitcoma.hearts.model.transfered.CardProtos.Card.newBuilder()
                                .setValue(cardPlayed.getValue())).setSrcUserId(srcId).build();

        for (Long userId : userIdToUserMap.keySet()) {
            ServerState.sendToClient(userId, response);
        }
    }

    @Override
    public void handleCardsPassed(List<PassingCardsInfo> passingCardInfo, Long firstPlayerId) {

        // Send cards to client that is destination
        PassCardsResponse.Builder response = PassCardsResponse.newBuilder();
        for (PassingCardsInfo pic : passingCardInfo) {
            response.setSrcUserId(pic.srcId);
            response.setDstUserId(pic.dstId);
            response.setFirstPlayerId(firstPlayerId);

            for (Card c : pic.cards) {
                response.addCardsPassed(org.bitcoma.hearts.model.transfered.CardProtos.Card.newBuilder()
                        .setValue(c.getValue()).build());
            }
            ServerState.sendToClient(pic.dstId, response);
        }

    }

    @Override
    public void handleScoreUpdate(Map<Long, Byte> userIdToGameScore, Map<Long, Byte> userIdToRoundScore) {

        // Send score updates to all clients
        ScoreUpdateResponse.Builder builder = ScoreUpdateResponse.newBuilder();
        for (Long userId : userIdToGameScore.keySet()) {
            byte roundScore = userIdToRoundScore.get(userId);
            byte gameScore = userIdToGameScore.get(userId);
            builder.addUserScores(ScoreInfo.newBuilder().setUserId(userId).setGameScore(gameScore)
                    .setRoundScore(roundScore).build());
        }

        for (Long userId : userIdToUserMap.keySet()) {
            ServerState.sendToClient(userId, builder.build());
        }
    }

    @Override
    public void handleTrickEnded(Trick finishedTrick) {
        TrickEndedResponse response = TrickEndedResponse.newBuilder().setLoserId(finishedTrick.getLoser()).build();

        for (Long userId : userIdToUserMap.keySet()) {
            ServerState.sendToClient(userId, response);
        }
    }

    @Override
    public void handleRoundEnded(Round finishedRound) {
        RoundEndedResponse response = RoundEndedResponse.newBuilder().build();

        for (Long userId : userIdToUserMap.keySet()) {
            ServerState.sendToClient(userId, response);
        }
    }

    @Override
    public void handleRoundStarted(Round startedRound) {

        Map<Long, LinkedList<Card>> handMap = startedRound.getUserIdToHand();
        Map<Long, Long> passingMap = startedRound.getUserIdToUserIdPassingMap();

        // Send player hands to each individual client
        for (Long userId : handMap.keySet()) {
            RoundStartedResponse.Builder builder = RoundStartedResponse.newBuilder();

            // Show who you are passing to.
            if (passingMap != null)
                builder.setPassedToUserId(passingMap.get(userId));

            builder.setUserId(userId);
            for (Card c : handMap.get(userId)) {
                builder.addCards(org.bitcoma.hearts.model.transfered.CardProtos.Card.newBuilder()
                        .setValue(c.getValue()).build());
            }
            ServerState.sendToClient(userId, builder.build());
        }

    }

    @Override
    public void handleGameEnded(Game finishedGame) {
        GameEndedResponse response = GameEndedResponse.newBuilder().build();

        for (Long userId : userIdToUserMap.keySet()) {
            ServerState.sendToClient(userId, response);
        }
    }

}
