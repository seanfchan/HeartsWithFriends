package org.bitcoma.heartsclient;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bitcoma.hearts.BotPlay;
import org.bitcoma.hearts.Card;
import org.bitcoma.hearts.HeartsProtoHandler;
import org.bitcoma.hearts.Trick;
import org.bitcoma.hearts.model.transfered.GameProtos.GameEndedResponse;
import org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo;
import org.bitcoma.hearts.model.transfered.GenericProtos.GenericResponse;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest;
import org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse;
import org.bitcoma.hearts.model.transfered.LeaveGameProtos.LeaveGameRequest;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginRequest;
import org.bitcoma.hearts.model.transfered.LoginProtos.LoginResponse;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PassCardsResponse;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PlayCardRequest;
import org.bitcoma.hearts.model.transfered.PlayCardProtos.PlaySingleCardResponse;
import org.bitcoma.hearts.model.transfered.ReplacePlayerProtos.ReplacePlayerResponse;
import org.bitcoma.hearts.model.transfered.RoundProtos.RoundEndedResponse;
import org.bitcoma.hearts.model.transfered.RoundProtos.RoundStartedResponse;
import org.bitcoma.hearts.model.transfered.ScoreUpdateProtos.ScoreUpdateResponse;
import org.bitcoma.hearts.model.transfered.ScoreUpdateProtos.ScoreUpdateResponse.ScoreInfo;
import org.bitcoma.hearts.model.transfered.SignupProtos.SignupRequest;
import org.bitcoma.hearts.model.transfered.StartGameProtos.StartGameRequest;
import org.bitcoma.hearts.model.transfered.TrickProtos.TrickEndedResponse;
import org.bitcoma.hearts.netty.handler.HeartsClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.MessageLite;

public class SimpleHeartsProtoHandler extends HeartsProtoHandler {

    private static final Logger logger = LoggerFactory.getLogger(SimpleHeartsProtoHandler.class);

    private Long userId;
    private Long gameId;
    private Long currentPlayerTurnId;
    private GameInfo currentGamePlayerInfo;

    private List<Card> playerHand;
    private List<Card> allCardsPlayed;
    private Trick currentTrick;

    private Map<Long, Integer> userIdToGameScore;
    private Map<Long, Integer> userIdToRoundScore;

    private List<Card> pendingCardsPlayed;

    private int roundCount;

    private HeartsClientHandler networkHandle;

    public SimpleHeartsProtoHandler(HeartsClientHandler networkHandle) {
        this.networkHandle = networkHandle;
    }

    private boolean isYourTurn() {
        return userId != null && userId == currentPlayerTurnId;
    }

    private void updatePendingCardsToPlay(Card cardToPlay) {
        LinkedList<Card> temp = new LinkedList<Card>();
        temp.add(cardToPlay);
        updatePendingCardsToPlay(temp);
    }

    private void updatePendingCardsToPlay(Collection<Card> cardsToPlay) {
        pendingCardsPlayed.clear();
        pendingCardsPlayed.addAll(cardsToPlay);
    }

    @Override
    public void handleGenericResponse(GenericResponse response, MessageLite origRequest) {

        if (origRequest == null) {
            logger.error("Generic response with no original request");
            return;
        }

        if (origRequest instanceof SignupRequest) {
            if (response.getResponseCode() == GenericResponse.ResponseCode.OK)
                logger.info("Signup request properly accepted");
            else
                logger.error("Signup request not accepted: " + response.getResponseCode());
        } else if (origRequest instanceof JoinGameRequest) {
            logger.error("Unable to join game successfully: " + response.getResponseCode());
        } else if (origRequest instanceof LeaveGameRequest) {
            logger.error("Unable to leave game successfully: " + response.getResponseCode());
        } else if (origRequest instanceof PlayCardRequest) {
            if (response.getResponseCode() == GenericResponse.ResponseCode.OK)
                logger.info("Play Card request successful");
            else
                logger.error("Unable to play card successfully: " + response.getResponseCode());
        } else if (origRequest instanceof SignupRequest) {
            if (response.getResponseCode() == GenericResponse.ResponseCode.OK)
                logger.info("Signup was successful");
            else
                logger.info("Unable to signup successfully:" + response.getResponseCode());
        } else if (origRequest instanceof LoginRequest) {
            logger.error("Unable to login successfully: " + response.getResponseCode());
        } else if (origRequest instanceof StartGameRequest) {
            if (response.getResponseCode() == GenericResponse.ResponseCode.OK)
                logger.info("Sent start game request successfully");
            else
                logger.error("Unable to start game successfully:" + response.getResponseCode());
        }
        // This case should not happen. Only these requests are expected to be
        // sent.
        else {
            logger.error("Sent a request that is not expected by the server: " + origRequest);
        }
    }

    @Override
    public void handleJoinGameResponse(JoinGameResponse response, MessageLite origRequest) {
        roundCount = 0;
        currentGamePlayerInfo = null;
        gameId = null;

        if (response.hasGameInfo()) {

            currentGamePlayerInfo = response.getGameInfo();
            gameId = response.getGameInfo().getGameId();

            logger.info("Received Join Game response with {}/{} players filled",
                    currentGamePlayerInfo.getPlayersCount(), currentGamePlayerInfo.getMaxNumberOfPlayers());

            // Is the game full? Then say that we are ready
            if (currentGamePlayerInfo.getPlayersCount() == currentGamePlayerInfo.getMaxNumberOfPlayers()) {
                // Send a start game request
                StartGameRequest request = StartGameRequest.newBuilder().setGameId(gameId).build();
                networkHandle.writeMessage(request);
            }
        } else
            logger.error("Join Game response doesn't have the proper fields");

    }

    @Override
    public void handleLoginResponse(LoginResponse response, MessageLite origRequest) {

        userId = null;

        if (origRequest == null || !(origRequest instanceof LoginRequest)) {
            logger.error("Login response without a proper request");
            return;
        }

        logger.info("Proper login response received. UserId: {}", response.getUserId());

        if (response.hasUserId()) {
            // save our user id and try to join a game.
            userId = response.getUserId();

            // Send a join game request here
            JoinGameRequest request = JoinGameRequest.newBuilder().setGameId((long) 0).build();
            networkHandle.writeMessage(request);
        } else
            logger.error("Login response doesn't have the proper fields");
    }

    @Override
    public void handleGameEndedResponse(GameEndedResponse response) {
        // Game has ended - We can stop the client here if we want.
        logger.info("Game has ended successfully");

        // Close the channel and disconnect as we are now done with this test.
        networkHandle.shutdown();
    }

    @Override
    public void handlePlaySingleCardResponse(PlaySingleCardResponse response) {
        if (!response.hasSrcUserId() || !response.hasCardPlayed()) {
            logger.error("Play single card response without proper data.");
            return;
        }

        Card cardPlayed = new Card((byte) response.getCardPlayed().getValue());

        logger.info("Single card played: Player: {} Card: {}", response.getSrcUserId(), cardPlayed);

        allCardsPlayed.add(cardPlayed);
        currentTrick.makeMove(response.getSrcUserId(), cardPlayed);
        currentPlayerTurnId = response.getNextPlayerId();

        // Remove the card from your hand when you take a turn.
        if (response.getSrcUserId() == userId) {
            playerHand.remove(cardPlayed);

            System.out.println("PlayerHand: " + playerHand);
        }

        if (isYourTurn() && playerHand.size() > 0) {
            Card c = BotPlay.playCard(currentTrick, playerHand, allCardsPlayed);

            updatePendingCardsToPlay(c);

            // Play single card here.
            org.bitcoma.hearts.model.transfered.CardProtos.Card cardToPlay = org.bitcoma.hearts.model.transfered.CardProtos.Card
                    .newBuilder().setValue(c.getValue()).build();
            PlayCardRequest request = PlayCardRequest.newBuilder().addCards(cardToPlay).build();
            networkHandle.writeMessage(request);
        }
    }

    @Override
    public void handleReplacePlayerResponse(ReplacePlayerResponse response) {
        // Replace the player in current game info.
    }

    @Override
    public void handlePassCardsResponse(PassCardsResponse response) {

        if (!response.hasSrcUserId() || !response.hasDstUserId() || !response.hasFirstPlayerId()
                || response.getCardsPassedCount() == 0) {
            logger.error("Pass Cards response doesn't have proper data required.");
            return;
        }

        if (response.getDstUserId() != userId) {
            logger.error("Destination of passed cards should only be the current user.");
            return;
        }

        logger.info("Cards have been passed");

        List<Card> cardsPassed = new LinkedList<Card>();
        for (org.bitcoma.hearts.model.transfered.CardProtos.Card c : response.getCardsPassedList()) {
            cardsPassed.add(new Card((byte) c.getValue()));
        }

        // Add cards to our hand
        playerHand.addAll(cardsPassed);
        currentPlayerTurnId = response.getFirstPlayerId();
        currentTrick = new Trick();

        // Remove old cards from your hand
        playerHand.removeAll(pendingCardsPlayed);

        System.out.println("PlayerHand: " + playerHand);

        // Take your turn since you have Two of clubs
        if (isYourTurn()) {
            Card c = BotPlay.playCard(currentTrick, playerHand, allCardsPlayed);

            updatePendingCardsToPlay(c);

            // Play single card here.
            org.bitcoma.hearts.model.transfered.CardProtos.Card cardToPlay = org.bitcoma.hearts.model.transfered.CardProtos.Card
                    .newBuilder().setValue(c.getValue()).build();
            PlayCardRequest request = PlayCardRequest.newBuilder().addCards(cardToPlay).build();
            networkHandle.writeMessage(request);
        }
    }

    @Override
    public void handleRoundEndedResponse(RoundEndedResponse response) {
        logger.info("Round has ended.");
        playerHand.clear();
    }

    @Override
    public void handleRoundStartedResponse(RoundStartedResponse response) {

        // Sanity check
        if (!response.hasUserId() || response.getCardsCount() <= 0) {
            logger.error("Round started without proper data sent");
            return;
        }

        if (response.getUserId() != userId) {
            logger.error("Cards should only be sent for current user");
            return;
        }

        roundCount++;
        logger.info("Round {} has started", roundCount);

        // Check which round it is so we know when the game started.
        if (roundCount == 1) {
            userIdToGameScore = new HashMap<Long, Integer>();
            allCardsPlayed = new LinkedList<Card>();
            pendingCardsPlayed = new LinkedList<Card>();
        }

        userIdToRoundScore = new HashMap<Long, Integer>();

        playerHand = new LinkedList<Card>();
        for (org.bitcoma.hearts.model.transfered.CardProtos.Card c : response.getCardsList()) {
            playerHand.add(new Card((byte) c.getValue()));
        }

        System.out.println("PlayerHand: " + playerHand);

        // Pass cards here
        if (response.hasPassedToUserId()) {
            List<Card> cardsToPlay = BotPlay.removeThree(playerHand);

            updatePendingCardsToPlay(cardsToPlay);

            PlayCardRequest.Builder builder = PlayCardRequest.newBuilder();
            for (Card c : cardsToPlay) {
                // Play single card here.
                org.bitcoma.hearts.model.transfered.CardProtos.Card cardToPlay = org.bitcoma.hearts.model.transfered.CardProtos.Card
                        .newBuilder().setValue(c.getValue()).build();
                builder.addCards(cardToPlay);
            }
            // Pass cards here.
            networkHandle.writeMessage(builder.build());
        }
        // This is a non passing round
        else {

            if (!response.hasFirstPlayerId()) {
                logger.error("Missing expected first player id");
                return;
            }

            currentTrick = new Trick();
            currentPlayerTurnId = response.getFirstPlayerId();

            // Play a single card here if you have two of clubs
            if (isYourTurn()) {
                Card c = BotPlay.playCard(currentTrick, playerHand, allCardsPlayed);

                updatePendingCardsToPlay(c);

                // Play single card here.
                org.bitcoma.hearts.model.transfered.CardProtos.Card cardToPlay = org.bitcoma.hearts.model.transfered.CardProtos.Card
                        .newBuilder().setValue(c.getValue()).build();
                PlayCardRequest request = PlayCardRequest.newBuilder().addCards(cardToPlay).build();
                networkHandle.writeMessage(request);
            }

        }

    }

    @Override
    public void handleScoreUpdateResponse(ScoreUpdateResponse response) {

        if (response.getUserScoresCount() <= 0) {
            logger.error("Score update response sent without all data.");
            return;
        }

        for (ScoreInfo si : response.getUserScoresList()) {
            userIdToGameScore.put(si.getUserId(), si.getGameScore());
            userIdToRoundScore.put(si.getUserId(), si.getRoundScore());
        }
    }

    @Override
    public void handleTrickEndedResponse(TrickEndedResponse response) {
        currentTrick = new Trick();
    }

    @Override
    public void handleUnexpectedMessage(MessageLite msg) {
        logger.error("Unexpected message received!!!");
    }

    @Override
    public void handleThrowable(Throwable throwable) {
        logger.error("Exception thrown in simple hearts client: {}", throwable);
    }

}
