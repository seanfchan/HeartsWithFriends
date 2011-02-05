package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Round {

    // True when playing 4 player hearts
    private final byte cardsInADeck = 52;
    private byte numOfCardsInDeck;
    private byte numOfCardsInHand;
    private Map<Long, Byte> userIdToScoreInRound;
    private Map<Long, Byte> userIdToScoreInGame;
    private Map<Long, LinkedList<Card>> userIdToHand;
    private Long loserId = null;
    private Trick currentTrick = null;
    private IHeartsHandler handler;

    public Round(Map<Long, Byte> userIdToScoreInGame, IHeartsHandler handler) {
        userIdToHand = new HashMap<Long, LinkedList<Card>>();
        userIdToScoreInRound = new HashMap<Long, Byte>();
        this.userIdToScoreInGame = userIdToScoreInGame;
        currentTrick = new Trick();
        this.handler = handler;

        // Initializing score
        for (Long userId : userIdToScoreInGame.keySet()) {
            userIdToScoreInRound.put(userId, (byte) 0);
            userIdToHand.put(userId, new LinkedList<Card>());
        }

        // Shuffle and deal out the cards
        shuffle(userIdToScoreInGame.size());
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
                System.out.println("SOMEHTING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            numOfCardsInHand = 17;
            break;

        case 4:

            // remove nothing.
            numOfCardsInHand = 13;
            break;

        case 5:

            // remove extra cards
            if (!deck.remove(new Card(Card.DIAMONDS, Card.TWO)))
                System.out.println("SOMEHTING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.CLUBS, Card.TWO)))
                System.out.println("SOMEHTING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            numOfCardsInHand = 10;
            break;

        case 6:

            // Remove extra cards
            if (!deck.remove(new Card(Card.DIAMONDS, Card.TWO)))
                System.out.println("SOMEHTING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.DIAMONDS, Card.THREE)))
                System.out.println("SOMEHTING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.CLUBS, Card.THREE)))
                System.out.println("SOMEHTING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.CLUBS, Card.FOUR)))
                System.out.println("SOMEHTING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            numOfCardsInHand = 8;
            break;

        case 7:

            if (!deck.remove(new Card(Card.DIAMONDS, Card.TWO)))
                System.out.println("SOMEHTING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.DIAMONDS, Card.THREE)))
                System.out.println("SOMEHTING WENT HORRIBLY WRONG! ABORT ABORT!!!");

            if (!deck.remove(new Card(Card.CLUBS, Card.THREE)))
                System.out.println("SOMEHTING WENT HORRIBLY WRONG! ABORT ABORT!!!");

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

    public void removeCard(Long userId, Card played) {
        // This is really simple compared to the code previously here.
        LinkedList<Card> result = userIdToHand.get(userId);
        if (result != null)
            result.remove(played);
    }

    /**
     * External API for playing cards from game.
     * 
     * @param id
     * @param cardsToPlay
     * @return
     */
    public boolean playCard(Long id, List<Card> cardsToPlay) {
        // TODO: @someone need to check the next player is a bot and use
        // internal API to make their turns

        return playCardInternal(id, cardsToPlay);
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
            if (numCardsPlayed == 1) {
                Card cardToPlay = cardsToPlay.get(0);
                if (currentTrick.isMoveValid(cardToPlay, userIdToHand.get(id))) {

                    // Move was valid
                    removeCard(id, cardToPlay);

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

                        currentTrick = new Trick();
                    }
                    return true;

                } else {
                    return false;
                }
            } else if (numCardsPlayed == 3) {

                // Does the player have all the cards trying to pass?
                List<Card> hand = userIdToHand.get(id);
                for (Card c : cardsToPlay) {
                    if (!hand.contains(c))
                        return false;
                }

                // TODO: @someone give other player the cards

                // Remove cards from your hand
                for (Card c : cardsToPlay)
                    removeCard(id, c);

                if (handler != null) {
                    // TODO: @someone Change second parameter to who receives
                    // the cards.
                    handler.handleCardsPassed(id, (long) 0, cardsToPlay);
                }
            }
        }

        return false;
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
