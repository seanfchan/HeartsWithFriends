package org.bitcoma.hearts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bitcoma.hearts.model.PassingCardsInfo;

public class Round {

    // True when playing 4 player hearts
    private static final byte CARDS_IN_DECK = 52;
    private byte numOfCardsInDeck;
    private byte numOfCardsInHand;
    private Map<Long, Byte> userIdToScoreInRound;
    private Map<Long, Byte> userIdToScoreInGame;
    private Map<Long, LinkedList<Card>> userIdToHand;

    // Stores all cards played in the round so far.
    private List<Card> allCardsPlayed;

    // Stores information about cards played out of order. Used to play cards in
    // order after the fact if it was a valid card.
    private Map<Long, Card> userIdToPendingCardPlayed;

    // Stores all the information about players passing cards.
    // This is needed so that all passing information is given at once instead
    // of little by little.
    // Note: This is null if it is a non-passing round or if passing has
    // completed
    private List<PassingCardsInfo> passingCardsInfo;

    // Defines which player passes to whom. Should be passed in by game
    // This is null if it is a non-passing round.
    private Map<Long, Long> userIdToUserIdPassingMap;

    // Defines the order of turns for the current trick
    private int userIdTurnIdx;

    // Defines the order players are sitting at the table
    private ArrayList<Long> userIdTableOrderList;

    private Long loserId;
    private Trick currentTrick;
    private IHeartsGameHandler handler;
    private boolean bHeartPlayed = false;

    public Round(ArrayList<Long> tableOrderList, Map<Long, Byte> userIdToScoreInGame,
            Map<Long, Long> userIdToUserIdPassingMap, IHeartsGameHandler handler) {
        if (userIdToScoreInGame == null) {
            throw new IllegalArgumentException("userIdToScoreInGame must be non-null");
        }

        this.userIdToScoreInGame = userIdToScoreInGame;
        this.handler = handler;
        this.userIdToUserIdPassingMap = userIdToUserIdPassingMap;
        userIdTableOrderList = tableOrderList;

        // userIdToUserIdPassingMap is null if this is a non-passing round
        if (this.userIdToUserIdPassingMap == null)
            passingCardsInfo = null;
        else
            passingCardsInfo = new LinkedList<PassingCardsInfo>();

        userIdToHand = new HashMap<Long, LinkedList<Card>>(getNumPlayers() + 3);
        userIdToScoreInRound = new HashMap<Long, Byte>(getNumPlayers() + 3);
        userIdTurnIdx = -1;
        userIdToPendingCardPlayed = new HashMap<Long, Card>(getNumPlayers() + 3);
        allCardsPlayed = new LinkedList<Card>();
        currentTrick = new Trick();

        // Initializing score
        int i = 0;
        for (Long userId : userIdToScoreInGame.keySet()) {
            userIdToScoreInRound.put(userId, (byte) 0);
            userIdToHand.put(userId, new LinkedList<Card>());
            i++;
        }

        // Shuffle and deal out the cards
        shuffle(userIdToScoreInGame.size());

        if (isPassingTurn()) {

            // Say round has started
            if (handler != null)
                handler.handleRoundStarted(this);

            // Have bot players pass their cards now.
            for (Long userId : userIdTableOrderList) {
                if (BotPlay.isBot(userId)) {
                    playCard(userId, getBotCardsToPlay(userId));
                }
            }
        } else {

            // No passing so set first player.
            Long firstPlayerId = getFirstTurnPlayerId();
            setPlayerTurnOrder(firstPlayerId);

            // Say round has started after we denote who is first
            if (handler != null)
                handler.handleRoundStarted(this);

            // Bot plays if they are first to play
            if (BotPlay.isBot(firstPlayerId)) {
                // Grab bots cards and play them
                playCard(firstPlayerId, getBotCardsToPlay(firstPlayerId));
            }
        }
    }

    private int getNumPlayers() {
        return userIdTableOrderList.size();
    }

    private void shuffle(int numOfPlayers) {
        List<Card> deck = new ArrayList<Card>(CARDS_IN_DECK);
        for (byte i = 0; i < CARDS_IN_DECK; ++i) {
            deck.add(new Card(i));
        }

        switch (numOfPlayers) {
        case 3:

            // removing extra cards
            if (!deck.remove(new Card(Card.DIAMONDS, Card.TWO)))
                System.out.println("SOMETHING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            numOfCardsInHand = 17;
            break;

        case 4:

            // remove nothing.
            numOfCardsInHand = 13;
            break;

        case 5:

            // remove extra cards
            if (!deck.remove(new Card(Card.DIAMONDS, Card.TWO)))
                System.out.println("SOMETHING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.CLUBS, Card.TWO)))
                System.out.println("SOMETHING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            numOfCardsInHand = 10;
            break;

        case 6:

            // Remove extra cards
            if (!deck.remove(new Card(Card.DIAMONDS, Card.TWO)))
                System.out.println("SOMETHING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.DIAMONDS, Card.THREE)))
                System.out.println("SOMETHING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.CLUBS, Card.THREE)))
                System.out.println("SOMETHING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.CLUBS, Card.FOUR)))
                System.out.println("SOMETHING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            numOfCardsInHand = 8;
            break;

        case 7:

            if (!deck.remove(new Card(Card.DIAMONDS, Card.TWO)))
                System.out.println("SOMETHING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.DIAMONDS, Card.THREE)))
                System.out.println("SOMETHING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.CLUBS, Card.THREE)))
                System.out.println("SOMETHING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            numOfCardsInHand = 7;
            break;
        }

        numOfCardsInDeck = (byte) (numOfCardsInHand * numOfPlayers);

        Random rand = new Random();

        int swapIndex = 0;
        Card tempCard = null;
        for (int i = numOfCardsInDeck - 1; i > 0; --i) {
            swapIndex = rand.nextInt(i) % numOfCardsInDeck;
            tempCard = deck.set(swapIndex, deck.get(i));
            deck.set(i, tempCard);
        }

        deal(deck);
    }

    private void deal(List<Card> deck) {
        int start = 0;
        for (Long userId : userIdToHand.keySet()) {
            List<Card> subList = deck.subList(start, start + numOfCardsInHand);
            Collections.sort(subList);

            userIdToHand.get(userId).addAll(subList);
            start += numOfCardsInHand;
        }
    }

    public boolean hasGameLoser(Map<Long, Byte> userIdToScoreInRound, Map<Long, Byte> userIdToScoreInGame) {
        for (Long userId : userIdToScoreInRound.keySet()) {
            if ((int) (userIdToScoreInGame.get(userId) + userIdToScoreInRound.get(userId)) >= 100) {
                loserId = userId;
                return true;
            }
        }

        return false;
    }

    public boolean isRoundOver() {
        if (allCardsPlayed.size() == numOfCardsInDeck)
            return true;
        else
            return false;
    }

    public boolean isGameOver() {
        for (Byte score : userIdToScoreInGame.values()) {
            if (score >= 100) {
                return true;
            }
        }

        return false;
    }

    private void addCard(Long userId, Card cardToAdd) {
        // This is really simple compared to the code previously here.
        LinkedList<Card> result = userIdToHand.get(userId);
        if (result != null)
            result.add(cardToAdd);
    }

    private void removeCard(Long userId, Card cardToRemove) {
        // This is really simple compared to the code previously here.
        LinkedList<Card> result = userIdToHand.get(userId);
        if (result != null)
            result.remove(cardToRemove);
    }

    /**
     * External API for playing cards from game.
     * 
     * @param id
     * @param cardsToPlay
     * @return
     */
    public synchronized boolean playCard(Long id, List<Card> cardsToPlay) {

        boolean result = playCardInternal(id, cardsToPlay);

        Long nextPlayerId = getCurrentTurnPlayerId();

        if (nextPlayerId != null && !isRoundOver() && !isGameOver()) {
            // Next player is a bot so make their turn
            if (BotPlay.isBot(nextPlayerId)) {
                // Grab bots cards and play them
                playCard(nextPlayerId, getBotCardsToPlay(nextPlayerId));
            }
            // Next player has already played their card. So play it now to
            // keep
            // order.
            else if (userIdToPendingCardPlayed.containsKey(nextPlayerId)) {
                List<Card> playerCards = new LinkedList<Card>();
                playerCards.add(userIdToPendingCardPlayed.remove(nextPlayerId));
                playCard(nextPlayerId, playerCards);
            }
        }

        return result;
    }

    /**
     * Method actually plays the cards. Used for bots and real players
     * 
     * @param id
     * @param cardsToPlay
     * @return
     */
    private boolean playCardInternal(Long id, List<Card> cardsToPlay) {
        int numCardsPlayed = cardsToPlay.size();
        if (numCardsPlayed == 3 || numCardsPlayed == 1) {

            // Single card played
            if (numCardsPlayed == 1 && passingCardsInfo == null) {
                Card cardToPlay = cardsToPlay.get(0);
                if (currentTrick.isMoveValid(cardToPlay, userIdToHand.get(id), bHeartPlayed)) {

                    // Check if it is the players turn
                    if (getCurrentTurnPlayerId() == id) {
                        userIdTurnIdx = (userIdTurnIdx + 1) % getNumPlayers();
                    } else {
                        // Card is valid, but out of order. Save here to play in
                        // order after.
                        userIdToPendingCardPlayed.put(id, cardToPlay);
                        return true;
                    }

                    // Move was valid and card is played.
                    removeCard(id, cardToPlay);
                    allCardsPlayed.add(cardToPlay);
                    currentTrick.makeMove(id, cardToPlay);

                    // Optimize checking for a heart has been played.
                    if (cardToPlay.getSuit() == Card.HEARTS)
                        bHeartPlayed = true;

                    // Trick has ended
                    if (currentTrick.getNumCards() == userIdToScoreInRound.size()) {

                        // Trick has ended so nextPlayerId is loser of trick or
                        // null if the round is over
                        Long nextPlayerId = isRoundOver() || isGameOver() ? null : currentTrick.getLoser();
                        if (handler != null) {
                            handler.handleSingleCardPlayed(id, cardToPlay, nextPlayerId);
                        }

                        // Update scores and send updates to clients
                        updateScores(currentTrick);

                        // Send updates to handle since trick has ended.
                        if (handler != null) {
                            handler.handleTrickEnded(currentTrick);
                        }

                        // Reset player turn order based on who lost the trick
                        setPlayerTurnOrder(currentTrick.getLoser());

                        currentTrick = new Trick();
                    } else {
                        // Trick is still in session so use next player in list
                        // for nextPlayerId
                        if (handler != null) {
                            if (userIdTurnIdx >= 0) {
                                Long nextPlayerId = isRoundOver() || isGameOver() ? null : getCurrentTurnPlayerId();
                                handler.handleSingleCardPlayed(id, cardToPlay, nextPlayerId);
                            } else
                                System.err
                                        .println("Should be another player in turn list as trick is not over. This is BAD!!!");
                        }
                    }

                    return true;

                } else {
                    // Invalid move played
                    System.err.println("Player: " + id + " playing an invalid card: " + cardToPlay);
                    return false;
                }

            } else if (numCardsPlayed == 3 && passingCardsInfo.size() < getNumPlayers()) {
                // NOTE: Cards are not passed until all players have passed
                // cards.
                // This is deliberate to stop cheating.

                List<Card> cardsPlayedCopy = new LinkedList<Card>(cardsToPlay);

                // Does the player have all the cards trying to pass?
                List<Card> hand = userIdToHand.get(id);
                for (Card c : cardsToPlay) {
                    // Remove card from copy.
                    cardsPlayedCopy.remove(c);

                    // Does not have the card they are playing
                    if (!hand.contains(c)) {
                        System.err.println("Player: " + id + " trying to play a card they do not have.");
                        return false;
                    }

                    // Playing two instances of the same card.
                    if (cardsPlayedCopy.contains(c)) {
                        System.err.println("Player: " + id + " trying to pass same card multiple times.");
                        return false;
                    }
                }

                // Give the cards to the other player based on what was passed
                // in from Game.
                Long dstId = userIdToUserIdPassingMap.get(id);
                passingCardsInfo.add(new PassingCardsInfo(id, dstId, cardsToPlay));

                // Everyone has finished passing cards so now we distribute
                // them.
                if (passingCardsInfo.size() == getNumPlayers()) {

                    for (PassingCardsInfo pic : passingCardsInfo) {
                        for (Card c : pic.cards) {
                            // Remove cards from source player's hand
                            removeCard(pic.srcId, c);
                            // Add cards to the destination player's hand
                            addCard(pic.dstId, c);
                        }
                    }

                    // Set up the order that players should be playing in.
                    setPlayerTurnOrder(getFirstTurnPlayerId());

                    if (handler != null)
                        handler.handleCardsPassed(passingCardsInfo, getFirstTurnPlayerId());

                    // Free up some memory
                    passingCardsInfo.clear();
                    passingCardsInfo = null;

                    Long nextUserId = getCurrentTurnPlayerId();

                    // Bot player got 2 of spades. Have them make the first
                    // move
                    if (BotPlay.isBot(nextUserId) && !isGameOver() && !isRoundOver()) {
                        // Grab bots cards and play them
                        playCard(nextUserId, getBotCardsToPlay(nextUserId));
                    }
                }

                return true;
            }
        }

        return false;
    }

    private void setPlayerTurnOrder(Long firstTurnPlayerId) {
        if (firstTurnPlayerId == null)
            userIdTurnIdx = -1;
        else
            userIdTurnIdx = userIdTableOrderList.indexOf(firstTurnPlayerId);
    }

    // Updating scores in Round and Game, meant to be run after every trick
    private void updateScores(Trick currentTrick) {
        Long loser = currentTrick.getLoser();
        int scoreFromTrick = currentTrick.computeScore();

        addToScore(loser, (byte) scoreFromTrick);
    }

    public void addToScore(Long playerId, byte scoreToAdd) {
        if (playerId != null) {
            userIdToScoreInRound.put(playerId, (byte) (userIdToScoreInRound.get(playerId) + scoreToAdd));
            userIdToScoreInGame.put(playerId, (byte) (userIdToScoreInGame.get(playerId) + scoreToAdd));

            if (handler != null) {
                handler.handleScoreUpdate(userIdToScoreInGame, userIdToScoreInRound);
            }
        }
    }

    public boolean replacePlayer(Long oldUserId, Long newUserId) {
        if (!userIdToHand.containsKey(oldUserId)) {
            return false;
        }

        boolean result = false;

        // Update internal structures and then update trick

        // Table order list is already updated from Game
        // User id to score in game already updated in Game
        LinkedList<Card> hand = userIdToHand.remove(oldUserId);
        userIdToHand.put(newUserId, hand);

        if (userIdToPendingCardPlayed.containsKey(oldUserId)) {
            Card cardPlayed = userIdToPendingCardPlayed.get(oldUserId);
            userIdToPendingCardPlayed.put(newUserId, cardPlayed);
        }

        Byte roundScore = userIdToScoreInRound.get(oldUserId);
        userIdToScoreInRound.put(newUserId, roundScore);

        if (currentTrick != null) {
            result = currentTrick.replacePlayer(oldUserId, newUserId);
        }

        // Play bots cards here if new player is a bot and it is
        // there turn as everyone is waiting on them.
        if (getCurrentTurnPlayerId() == newUserId) {
            // Next player is a bot so make their turn
            if (BotPlay.isBot(newUserId) && !isGameOver() && !isRoundOver()) {
                playCard(newUserId, getBotCardsToPlay(newUserId));
            }
        }

        return result;
    }

    public Long getLoserId() {
        return loserId;
    }

    public Map<Long, LinkedList<Card>> getUserIdToHand() {
        return userIdToHand;
    }

    public Map<Long, Long> getUserIdToUserIdPassingMap() {
        return userIdToUserIdPassingMap;
    }

    /**
     * Determines if this round has passing in it.
     * 
     * @return true if there was passing this round
     */
    public boolean isPassingRound() {
        return getUserIdToUserIdPassingMap() != null;
    }

    /**
     * Determines if the current turn should be passing
     * 
     * @return true if the current turn is a passing turn.
     */
    public boolean isPassingTurn() {
        return getPassingCardsInfo() != null;
    }

    public Map<Long, Byte> getUserIdToScoreInRound() {
        return userIdToScoreInRound;
    }

    public List<PassingCardsInfo> getPassingCardsInfo() {
        return passingCardsInfo;
    }

    public List<Card> getAllCardsPlayed() {
        return allCardsPlayed;
    }

    public Trick getCurrentTrick() {
        return currentTrick;
    }

    public Long getFirstTurnPlayerId() {
        for (Long id : userIdToHand.keySet()) {
            List<Card> hand = userIdToHand.get(id);

            if (hand.contains(Card.TWO_CLUBS))
                return id;
        }

        // This should never happen
        return null;
    }

    public Long getCurrentTurnPlayerId() {
        if (userIdTurnIdx >= 0) {
            return userIdTableOrderList.get(userIdTurnIdx);
        } else {
            return null;
        }
    }

    private List<Card> getBotCardsToPlay(Long userId) {
        if (isPassingTurn()) {
            return BotPlay.removeThree(userIdToHand.get(userId));
        } else {
            // Grab bots cards and play them
            Card botCard = BotPlay.playCard(currentTrick, userIdToHand.get(userId), allCardsPlayed);
            List<Card> botCards = new LinkedList<Card>();
            botCards.add(botCard);
            return botCards;
        }
    }
}
