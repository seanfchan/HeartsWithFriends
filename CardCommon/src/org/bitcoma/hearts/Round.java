package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.LinkedList;
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

    public Round(Map<Long, Byte> userIdToScoreInGame) {
        userIdToHand = new HashMap<Long, LinkedList<Card>>();
        userIdToScoreInRound = new HashMap<Long, Byte>();
        this.userIdToScoreInGame = userIdToScoreInGame;

        // Initializing score
        for (Long userId : userIdToScoreInGame.keySet()) {
            userIdToScoreInRound.put(userId, (byte) 0);
            userIdToHand.put(userId, new LinkedList<Card>());
        }
    }

    public void shuffle(int numOfPlayers) {
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
        for (byte i = (byte) (numOfCardsInDeck - 1); i > 0; --i) {
            swapIndex = (byte) (rand.nextInt(i) % numOfCardsInDeck);
            tempCard = deck.set(swapIndex, deck.get(i));
            deck.set(i, tempCard);
        }

        deal(deck);
    }

    public void deal(LinkedList<Card> deck) {
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
        LinkedList<Card> result = userIdToHand.get(userId);
        Card toRemove = null;
        for (Card c : result) {
            if (c.getSuit() == played.getSuit() && c.getRank() == played.getRank()) {
                toRemove = c;
                break;
            }
        }

        result.remove(toRemove);
        userIdToHand.put(userId, result);
    }

    // Play card(s) input: userid, list of cards
    public void playCard(Long id, LinkedList<Card> cardsToPlay, Trick currentTrick) {
        // Normal case of just playing cards
        if (cardsToPlay.size() != 3) {
            currentTrick.makeMove(id, cardsToPlay.getFirst());

            removeCard(id, cardsToPlay.getFirst());
        }
        // Giving/receiving 3 card in the beginning of a round
        else {
            for (Card c : cardsToPlay)
                removeCard(id, c);
        }
    }

    // Updating scores in Round and Game, meant to be run after every trick
    public void updateScores(Trick currentTrick) {
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
}
