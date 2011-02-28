package org.bitcoma.heartserver.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javolution.util.FastMap;

import org.bitcoma.hearts.BotPlay;
import org.bitcoma.hearts.Card;
import org.bitcoma.hearts.Game;
import org.bitcoma.hearts.IHeartsGameHandler;
import org.bitcoma.hearts.Round;
import org.bitcoma.hearts.Trick;
import org.bitcoma.hearts.model.PassingCardsInfo;
import org.bitcoma.hearts.model.transfered.GameProtos.GameEndedResponse;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PassCardsResponse;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PlaySingleCardResponse;
import org.bitcoma.hearts.model.transfered.PlayerStructProtos.PlayerInfo;
import org.bitcoma.hearts.model.transfered.ReplacePlayerProtos.ReplacePlayerResponse;
import org.bitcoma.hearts.model.transfered.RoundProtos.RoundEndedResponse;
import org.bitcoma.hearts.model.transfered.RoundProtos.RoundStartedResponse;
import org.bitcoma.hearts.model.transfered.ScoreUpdateProtos.ScoreUpdateResponse;
import org.bitcoma.hearts.model.transfered.ScoreUpdateProtos.ScoreUpdateResponse.ScoreInfo;
import org.bitcoma.hearts.model.transfered.TrickProtos.TrickEndedResponse;
import org.bitcoma.heartserver.ServerState;
import org.bitcoma.heartserver.model.database.User;
import org.bitcoma.heartserver.netty.task.PassCardsTimeOutTask;
import org.bitcoma.heartserver.netty.task.PlaySingleCardTimeOutTask;
import org.bitcoma.heartserver.netty.task.TimeOutTaskCreator;
import org.jboss.netty.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameInstance implements IHeartsGameHandler {
    public static enum State {
        PLAYING, SYNCING_START, WAITING, FINISHED
    };

    public static AtomicLong gameCounter = new AtomicLong(1);
    private long id;
    private int readyNumPlayers;
    private int maxPlayers;
    private State gameState;
    private Game gameInfo;

    private static final Logger logger = LoggerFactory.getLogger(GameInstance.class);

    /**
     * Used for two different reasons. If the game is in waiting state, then
     * this is the task for adding bots. If the game is in playing state, then
     * this is the task to penalize an inactive player
     */
    private Timeout timeout;

    FastMap<Long, User> userIdToUserMap;

    /**
     * Used to track the number of infractions due to timeouts of player
     * actions. Note: Count is reset once a player makes a valid turn. (ie
     * players penalized more for repeated infractions)
     */
    FastMap<Long, Byte> userIdToInfractionCount;

    public GameInstance(int inputMaxPlayers, State inputState) {
        if (inputMaxPlayers <= 0)
            throw new IllegalArgumentException("Max Players has to be greater than zero.");

        id = gameCounter.getAndIncrement();
        readyNumPlayers = 0;
        maxPlayers = inputMaxPlayers;
        gameState = inputState;

        userIdToUserMap = new FastMap<Long, User>(inputMaxPlayers).shared();
    }

    public synchronized boolean addPlayer(User user) {
        // Make sure there is room for the player and player isn't in game
        // already
        if (isFull() || userIdToUserMap.containsKey(user.getLongId()))
            return false;

        userIdToUserMap.put(user.getLongId(), user);

        if (isFull()) {
            setGameState(State.SYNCING_START);

            // Add bots to number of ready players
            for (Long id : userIdToUserMap.keySet()) {
                if (BotPlay.isBot(id))
                    addReadyPlayer();
            }

        }

        return true;
    }

    public synchronized boolean removePlayer(User user) {
        // Make sure there are players
        if (getCurrentNumPlayers() < 1)
            return false;

        // Game in progress
        if (getGameState() != State.WAITING) {
            User playerToAdd = User.selectRandomBot(userIdToUserMap.keySet());
            replacePlayer(user, playerToAdd);
        }
        // Still waiting for game to be full
        else {
            userIdToUserMap.remove(user.getLongId());
        }

        return true;
    }

    public synchronized int getCurrentNumPlayers() {
        return userIdToUserMap.size();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isFull() {
        return getCurrentNumPlayers() >= getMaxPlayers();
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
                userIdToInfractionCount = new FastMap<Long, Byte>(getCurrentNumPlayers()).shared();

                // Zero out the infraction counts initially
                for (Long id : userIdToUserMap.keySet()) {
                    userIdToInfractionCount.put(id, (byte) 0);
                }

                gameInfo = new Game(userIdToUserMap.keySet(), this);
            }
            return true;
        }
        return false;
    }

    public synchronized int getReadyNumPlayers() {
        return readyNumPlayers;
    }

    private void replacePlayer(User oldUser, User newUser) {
        if (oldUser == null || newUser == null) {
            throw new IllegalArgumentException("Users must be non-null in replace player");
        }

        if (!userIdToUserMap.containsKey(oldUser.getLongId())) {
            throw new IllegalArgumentException("Removing a player that isn't present!");
        }

        if (getGameState() == State.PLAYING || getGameState() == State.SYNCING_START) {

            // If only bot players are left then we need to remove this game as
            // it is just wasting cycles.
            boolean humansLeft = false;
            for (Long id : userIdToUserMap.keySet()) {
                // Other human players besides who is removed.
                if (id != oldUser.getLongId() && !BotPlay.isBot(id)) {
                    humansLeft = true;
                    break;
                }
            }

            // No other humans in game and we are replacing with a bot. So
            // remove the game.
            if (!humansLeft && BotPlay.isBot(newUser.getLongId())) {

                logger.info("Removing Game: {} b/c it only has bots.", getId());
                ServerState.activeGames.remove(getId());

                // Just to make sure.
                gameInfo = null;
                userIdToInfractionCount = null;
                userIdToUserMap = null;

                return;
            }
        }

        userIdToUserMap.remove(oldUser.getLongId());
        Byte infractions = userIdToInfractionCount.remove(oldUser.getLongId());

        userIdToUserMap.put(newUser.getLongId(), newUser);
        userIdToInfractionCount.put(newUser.getLongId(), infractions);

        // Left while synchronizing the start of the game.
        // Need to mark the bot as a ready player to ensure game starts
        if (BotPlay.isBot(newUser.getLongId()) && getGameState() == State.SYNCING_START) {
            addReadyPlayer();
        }

        // Tell clients about the player update
        PlayerInfo removedPlayer = PlayerInfo.newBuilder().setUserId(oldUser.getLongId())
                .setUserName(oldUser.getString("user_name")).build();
        PlayerInfo addedPlayer = PlayerInfo.newBuilder().setUserId(newUser.getLongId())
                .setUserName(newUser.getString("user_name")).build();
        ReplacePlayerResponse response = ReplacePlayerResponse.newBuilder().setAddedPlayer(addedPlayer)
                .setRemovedPlayer(removedPlayer).build();
        for (Long id : userIdToUserMap.keySet()) {
            ServerState.sendToClient(id, response);
        }

        if (gameInfo != null) {
            // NOTE: This will play the new bots cards if the new player is a
            // bot and it is their turn. So we need to inform clients 1st
            gameInfo.replacePlayer(oldUser.getLongId(), newUser.getLongId());
        }
    }

    public Long getId() {
        return id;
    }

    public byte getInfractionCount(Long userId) {
        if (userIdToInfractionCount == null)
            return 0;
        return userIdToInfractionCount.get(userId);
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

    /**
     * Default usage of playing a card. You were not forced to play a card and
     * will not be penalized
     * 
     * @param userId
     *            Player id of who is playing the card
     * @param cardsToPlay
     *            List of cards to play this turn. (1 for normal play, 3 for
     *            passing)
     * @return true if card(s) played were valid and accepted.
     */
    public boolean playCard(Long userId, List<Card> cardsToPlay) {
        return playCard(userId, cardsToPlay, false);
    }

    /**
     * Function to play a card for a user. Allowed to mark if this turn was
     * forced (timeout) so we can mark the infractions for added punishment
     * later.
     * 
     * @param userId
     *            Player id of who is playing the card
     * @param cardsToPlay
     *            List of cards to play this turn. (1 for normal play, 3 for
     *            passing)
     * @param wasForced
     *            true if this was a card selected for player and punishment is
     *            in order, false otherwise
     * @return true if card(s) played were valid and accepted.
     */
    public boolean playCard(Long userId, List<Card> cardsToPlay, boolean wasForced) {
        if (gameInfo != null) {
            boolean result = gameInfo.playCard(userId, cardsToPlay);

            // Mark player as punished.
            if (wasForced) {
                byte infractionCount = userIdToInfractionCount.get(userId);
                userIdToInfractionCount.put(userId, (byte) (infractionCount + 1));
            }
            // Clear infraction count as this was player initiated.
            else {
                userIdToInfractionCount.put(userId, (byte) 0);
            }

            return result;
        } else {
            return false;
        }
    }

    public boolean addToScore(Long userId, byte scoreToAdd) {
        if (gameInfo != null) {
            return gameInfo.addToScore(userId, scoreToAdd);
        } else {
            return false;
        }
    }

    public Round getCurrentRound() {
        if (gameInfo != null) {
            return gameInfo.getCurrentRound();
        } else {
            return null;
        }
    }

    public void setTimeout(Timeout timeout, boolean cancelPrevious) {
        if (cancelPrevious && this.timeout != null) {
            this.timeout.cancel();
        }
        this.timeout = timeout;
    }

    public void setTimeout(Timeout timeout) {
        setTimeout(timeout, true);
    }

    public Timeout getTimeout() {
        return timeout;
    }

    @Override
    public void handleSingleCardPlayed(Long srcId, Card cardPlayed, Long nextPlayerId) {

        logger.info("Handling single card played. Game: {}", getId());

        // Remove timer task here for passing single card
        setTimeout(null);

        // Send card played to all clients
        PlaySingleCardResponse.Builder builder = PlaySingleCardResponse
                .newBuilder()
                .setCardPlayed(
                        org.bitcoma.hearts.model.transfered.CardProtos.Card.newBuilder()
                                .setValue(cardPlayed.getValue())).setSrcUserId(srcId);

        // If the round is over then this is null
        if (nextPlayerId != null)
            builder.setNextPlayerId(nextPlayerId);

        for (Long userId : userIdToUserMap.keySet()) {
            ServerState.sendToClient(userId, builder.build());
        }

        if (!BotPlay.isBot(nextPlayerId)) {
            // Add timer task here for passing single card (nextPlayerId)
            PlaySingleCardTimeOutTask task = new PlaySingleCardTimeOutTask(this, nextPlayerId);
            setTimeout(TimeOutTaskCreator.createTask(task, PlaySingleCardTimeOutTask.DELAY,
                    PlaySingleCardTimeOutTask.UNIT));
        }
    }

    @Override
    public void handleCardsPassed(List<PassingCardsInfo> passingCardInfo, Long firstPlayerId) {

        logger.info("Handling cards passed. Game: {}", getId());

        // Remove timer task here for passing cards.
        setTimeout(null);

        // Send cards to client that is destination
        PassCardsResponse.Builder builder = PassCardsResponse.newBuilder();
        for (PassingCardsInfo pic : passingCardInfo) {
            builder.setSrcUserId(pic.srcId);
            builder.setDstUserId(pic.dstId);
            builder.setFirstPlayerId(firstPlayerId);

            for (Card c : pic.cards) {
                builder.addCardsPassed(org.bitcoma.hearts.model.transfered.CardProtos.Card.newBuilder()
                        .setValue(c.getValue()).build());
            }
            ServerState.sendToClient(pic.dstId, builder.build());
        }

        if (!BotPlay.isBot(firstPlayerId)) {
            // Add timer task here for playing a single card for first player id
            PlaySingleCardTimeOutTask task = new PlaySingleCardTimeOutTask(this, firstPlayerId);
            setTimeout(TimeOutTaskCreator.createTask(task, PlaySingleCardTimeOutTask.DELAY,
                    PlaySingleCardTimeOutTask.UNIT));
        }
    }

    @Override
    public void handleScoreUpdate(Map<Long, Byte> userIdToGameScore, Map<Long, Byte> userIdToRoundScore) {

        logger.info("Handling score update. Game: {}", getId());

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

        logger.info("Handling trick ended. Game: {}", getId());

        TrickEndedResponse response = TrickEndedResponse.newBuilder().setLoserId(finishedTrick.getLoser()).build();

        for (Long userId : userIdToUserMap.keySet()) {
            ServerState.sendToClient(userId, response);
        }
    }

    @Override
    public void handleRoundEnded(Round finishedRound) {

        logger.info("Handling round ended. Game: {}", getId());

        setTimeout(null);

        RoundEndedResponse response = RoundEndedResponse.newBuilder().build();

        for (Long userId : userIdToUserMap.keySet()) {
            ServerState.sendToClient(userId, response);
        }
    }

    @Override
    public void handleRoundStarted(Round startedRound) {

        logger.info("Handling round started. Game: {}", getId());

        setTimeout(null);

        Map<Long, LinkedList<Card>> handMap = startedRound.getUserIdToHand();
        Map<Long, Long> passingMap = startedRound.getUserIdToUserIdPassingMap();

        // Send player hands to each individual client
        for (Long userId : handMap.keySet()) {
            RoundStartedResponse.Builder builder = RoundStartedResponse.newBuilder();

            // Show who you are passing to if this is a passing round
            if (startedRound.isPassingTurn()) {
                builder.setPassedToUserId(passingMap.get(userId));
            }
            // Tell who's turn is first if this is a non-passing round
            else {
                builder.setFirstPlayerId(startedRound.getCurrentTurnPlayerId());
            }

            builder.setUserId(userId);
            for (Card c : handMap.get(userId)) {
                builder.addCards(org.bitcoma.hearts.model.transfered.CardProtos.Card.newBuilder()
                        .setValue(c.getValue()).build());
            }
            ServerState.sendToClient(userId, builder.build());
        }

        if (startedRound.isPassingTurn()) {
            // Create PassCardsTimeOutTask here to time when players are
            // done selecting there cards.
            PassCardsTimeOutTask task = new PassCardsTimeOutTask(this, startedRound);
            setTimeout(TimeOutTaskCreator.createTask(task, PassCardsTimeOutTask.DELAY, PassCardsTimeOutTask.UNIT));
        }
    }

    @Override
    public void handleGameEnded(Game finishedGame) {

        logger.info("Handling Game Ended. Game: {}", getId());

        setTimeout(null);

        // Remove game from set of active games as this game is now over.
        ServerState.activeGames.remove(getId());

        // Mark game as finished
        setGameState(State.FINISHED);

        // TODO: @jon maybe display user statistics about # of games won,
        // ranking, etc...
        GameEndedResponse response = GameEndedResponse.newBuilder().build();

        for (Long userId : userIdToUserMap.keySet()) {
            ServerState.sendToClient(userId, response);
        }

        // TODO: @jon remove game from each individuals current game variable.
        // How to do this?
    }

}
