package org.bitcoma.hearts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bitcoma.hearts.model.PassingCardsInfo;

public class Round {

    // True when playing 4 player hearts
    private final byte cardsInADeck = 52;
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
    private List<PassingCardsInfo> passingCardsInfo;

    // Defines which player passes to whom. Should be passed in by game
    private Map<Long, Long> userIdToUserIdPassingMap;

    // Defines the order of turns for the current trick
    private List<Long> userIdTurnOrderList;

    // Defines the order players are sitting at the table
    private long[] userIdTableOrderArray;

    private Long loserId;
    private Trick currentTrick;
    private IHeartsGameHandler handler;
    private boolean bHeartPlayed = false;

    public Round(Map<Long, Byte> userIdToScoreInGame, Map<Long, Long> userIdToUserIdPassingMap,
            IHeartsGameHandler handler) {
        if (userIdToScoreInGame == null) {
            throw new IllegalArgumentException("userIdToScoreInGame must be non-null");
        }
        if (userIdToUserIdPassingMap == null) {
            throw new IllegalArgumentException("userIdToUserIdPassingMap must be non-null");
        }

        this.userIdToScoreInGame = userIdToScoreInGame;
        this.handler = handler;
        this.userIdToUserIdPassingMap = userIdToUserIdPassingMap;

        userIdToHand = new HashMap<Long, LinkedList<Card>>();
        userIdToScoreInRound = new HashMap<Long, Byte>();
        passingCardsInfo = new LinkedList<PassingCardsInfo>();
        userIdTurnOrderList = new LinkedList<Long>();
        userIdTableOrderArray = new long[userIdToScoreInGame.size()];
        userIdToPendingCardPlayed = new HashMap<Long, Card>();
        allCardsPlayed = new LinkedList<Card>();
        currentTrick = new Trick();

        // Initializing score
        int i = 0;
        for (Long userId : userIdToScoreInGame.keySet()) {
            userIdToScoreInRound.put(userId, (byte) 0);
            userIdToHand.put(userId, new LinkedList<Card>());
            userIdTableOrderArray[i] = userId;
            i++;
        }

        // Shuffle and deal out the cards
        shuffle(userIdToScoreInGame.size());

        if (handler != null)
            handler.handleRoundStarted(this);

        // Have bot players pass their cards now.
        for (long userId : userIdTableOrderArray) {
            if (BotPlay.isBot(userId)) {
                List<Card> botCards = BotPlay.removeThree(userIdToHand.get(userId));
                playCard(userId, botCards);
            }
        }

    }

    private int getNumPlayers() {
        return userIdToScoreInGame.size();
    }

    private void shuffle(int numOfPlayers) {
        LinkedList<Card> deck = new LinkedList<Card>();
        for (byte i = 0; i < cardsInADeck; ++i) {
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

    private void deal(LinkedList<Card> deck) {
        int start = 0;
        for (Long userId : userIdToHand.keySet()) {
            userIdToHand.get(userId).addAll(deck.subList(start, start + numOfCardsInHand));
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

    public boolean hasRoundEnded() {
        for (Long userId : userIdToHand.keySet()) {
            if (userIdToHand.get(userId).size() != 0)
                return false;
        }
        return true;
    }

    public void addCard(Long userId, Card cardToAdd) {
        // This is really simple compared to the code previously here.
        LinkedList<Card> result = userIdToHand.get(userId);
        if (result != null)
            result.add(cardToAdd);
    }

    public void removeCard(Long userId, Card cardToRemove) {
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
    public boolean playCard(Long id, List<Card> cardsToPlay) {

        boolean result = playCardInternal(id, cardsToPlay);

        if (userIdTurnOrderList.size() > 0) {
            Long nextPlayerId = userIdTurnOrderList.get(0);

            // Next player is a bot so make their turn
            if (BotPlay.isBot(nextPlayerId)) {

                // Grab bots cards and play them
                Card botCard = BotPlay.playCard(currentTrick.getSuitOfTrick(), currentTrick.getPlayerIdToCardMap()
                        .values(), userIdToHand.get(nextPlayerId), allCardsPlayed);
                List<Card> botCards = new LinkedList<Card>();
                botCards.add(botCard);
                playCard(nextPlayerId, botCards);
            }
            // Next player has already played their card. So play it now to keep
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
                    if (userIdTurnOrderList.size() > 0 && userIdTurnOrderList.get(0) == id) {
                        userIdTurnOrderList.remove(0);
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

                    if (handler != null) {
                        handler.handleSingleCardPlayed(id, cardToPlay);
                    }

                    // Trick has ended
                    if (currentTrick.getNumCards() == userIdToScoreInRound.size()) {
                        updateScores(currentTrick);

                        // Send updates to handle since trick has ended.
                        if (handler != null) {
                            handler.handleScoreUpdate(userIdToScoreInGame, userIdToScoreInRound);
                            handler.handleTrickEnded(currentTrick);
                        }

                        // Reset player turn order based on who lost the trick
                        setPlayerTurnOrder(currentTrick.getLoser());

                        currentTrick = new Trick();
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
                    setPlayerTurnOrder(getFirstPlayerId());

                    if (handler != null)
                        handler.handleCardsPassed(passingCardsInfo, getFirstPlayerId());

                    // Free up some memory
                    passingCardsInfo.clear();
                    passingCardsInfo = null;

                    // TODO: @jon if it is a bots turn first then have them play
                    // there cards here.

                    // Bot player got 2 of spades. Have them make the first move
                    if (userIdTurnOrderList.size() > 0) {
                        long nextUserId = userIdTurnOrderList.get(0);
                        if (BotPlay.isBot(nextUserId)) {
                            // Grab bots cards and play them
                            Card botCard = BotPlay.playCard(currentTrick.getSuitOfTrick(), currentTrick
                                    .getPlayerIdToCardMap().values(), userIdToHand.get(nextUserId), allCardsPlayed);
                            List<Card> botCards = new LinkedList<Card>();
                            botCards.add(botCard);
                            playCard(nextUserId, botCards);
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }

    private void setPlayerTurnOrder(Long firstPlayerId) {
        userIdTurnOrderList.clear();
        userIdTurnOrderList.add(firstPlayerId);

        // Loop around the table add add the rest of the players
        int idx = Arrays.binarySearch(userIdTableOrderArray, firstPlayerId);
        while (userIdTurnOrderList.size() < getNumPlayers()) {
            idx = (idx + 1) % getNumPlayers();
            userIdTurnOrderList.add(userIdTableOrderArray[idx]);
        }
    }

    // Updating scores in Round and Game, meant to be run after every trick
    private void updateScores(Trick currentTrick) {
        Long loser = currentTrick.getLoser();
        userIdToScoreInRound.put(loser, (byte) (userIdToScoreInRound.get(loser) + currentTrick.computeScore()));

        // If round ends update game score
        if (hasRoundEnded()) {
            userIdToScoreInGame.put(loser, (byte) (userIdToScoreInGame.get(loser) + userIdToScoreInRound.get(loser)));
        }
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

    public Map<Long, Byte> getUserIdToScoreInRound() {
        return userIdToScoreInRound;
    }

    public Long getFirstPlayerId() {
        Card twoOfClubs = new Card(Card.CLUBS, Card.TWO);

        for (Long id : userIdToHand.keySet()) {
            List<Card> hand = userIdToHand.get(id);

            if (hand.contains(twoOfClubs))
                return id;
        }

        // This should never happen
        return null;
    }
}
