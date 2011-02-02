package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class BotPlay {
    HashMap<Card, Double> matrix = new HashMap<Card, Double>();
    int myScore = 0;
    HashMap<Integer, LinkedList<Card>> trickMemory = new HashMap<Integer, LinkedList<Card>>();
    LinkedList<Card> givenAway = new LinkedList<Card>();
    int[] suitRep = new int[4]; // because we only have 4 suits.

    boolean myTurn = false;

    public BotPlay(LinkedList<Card> cards, boolean skipInit) {

        for (Card c : cards) {
            matrix.put(c, new Double(0));
        }
        removeThree();

        // we expect receiveThree function to be called here.

        // TODO @madiha: Wait to compute probabilities until the bot has
        // received all three card.

        // we would skip initialization if the bot replaced a person in the
        // middle of the game, as then
        // there is no way to have full memory of what is going on.
        if (!skipInit)
            initialize();

    }

    public void suitVariety() {
        // computes the variety of suits for the existing cards.
        Iterator<Card> cardIter = matrix.keySet().iterator();

        // set to zero first
        for (int i = 0; i < suitRep.length; i++)
            suitRep[i] = 0;

        while (cardIter.hasNext()) {
            Card considered = cardIter.next();
            if (considered.getSuit() == Card.SPADES)
                suitRep[Card.SPADES]++;
            else if (considered.getSuit() == Card.HEARTS)
                suitRep[Card.HEARTS]++;
            else if (considered.getSuit() == Card.CLUBS)
                suitRep[Card.CLUBS]++;
            else
                suitRep[Card.DIAMONDS]++;
        }
    }

    public int mostRepresentedSuit() {
        suitVariety();
        int max = 0;
        int best = -1;
        for (int i = 0; i < suitRep.length; i++) {
            if (suitRep[i] > max) {
                max = suitRep[i];
                best = i;
            }
        }
        return best;
    }

    public LinkedList<Card> getSuitCards(int s) {
        // gives you cards of the suit
        LinkedList<Card> result = new LinkedList<Card>();
        Iterator<Card> cardIter = matrix.keySet().iterator();
        while (cardIter.hasNext()) {
            Card considered = cardIter.next();
            if (considered.getSuit() == s) {
                result.add(considered);
            }
        }
        return result;
    }

    public void removeThree() {
        // at the beginning of the round, the bot has to get rid of three cards.
        /*
         * Rules the bot follows: 1. Definitely removes the Queen of Spades if
         * it has that. 2. Checks how many cards of each suit it has and makes
         * sure to have as much suit variety as possible. 3. Remove high cards
         * from the suit.
         */
        Iterator<Card> cardIter = matrix.keySet().iterator();
        while (cardIter.hasNext()) {
            Card considered = cardIter.next();
            if (considered.getSuit() == Card.SPADES && considered.getRank() == Card.QUEEN) {
                // explicit search for queen of spades
                givenAway.add(considered);
                matrix.remove(considered); // removed queen of spades from the
                                           // matrix.
                break;
            }
        }

        // Discard the card of the most represented suit and one that has the
        // highest value.
        while (givenAway.size() < 3) {
            int bestSuit = mostRepresentedSuit();
            LinkedList<Card> suitCards = getSuitCards(bestSuit);
            int bestRank = 0;
            Card removeMe = null;
            // find highest value in the suit
            for (Card c : suitCards) {
                if (c.getRank() > bestRank) {
                    bestRank = c.getRank();
                    removeMe = c;
                }
            }
            givenAway.add(removeMe);
        }

    }

    public void receiveThree(LinkedList<Card> recvd) {

    }

    public void initialize() {
        // function sets up probabilities for all the cards the bot has.
        int length = matrix.size();

        boolean hasTwoClubs = false;
        boolean hasQueenSpades = false;

        Iterator<Card> cardIter = matrix.keySet().iterator();
        while (cardIter.hasNext()) {
            Card temp = cardIter.next();
            if (temp.getSuit() == Card.CLUBS && temp.getRank() == Card.TWO) {
                matrix.remove(temp);

                hasTwoClubs = true;

            }

            if (temp.getSuit() == Card.SPADES && temp.getRank() == Card.QUEEN) {
                // very low probability - this is shit!
                matrix.remove(temp);

                hasQueenSpades = true;
            }
        }

        double remProb = 1;
        int remCards = length;

        if (hasTwoClubs) {
            remProb -= new Double(0.5);
            remCards--;
        }
        if (hasQueenSpades) {
            remProb -= new Double(0.0001);
            remCards--;
        }

        // depending on the values of the remaining cards, split probabilities.
        Card[] remainingCards = (Card[]) matrix.keySet().toArray();
        Card.sortCards(remainingCards);

        int sumOfValues = 0;
        for (Card c : remainingCards)
            sumOfValues += c.getRank();

        // each card get a probability proportional to its rank regardless of
        // the suit.
        for (Card c : matrix.keySet()) {
            double prob = (double) (c.getRank()) / (double) sumOfValues;
            matrix.put(c, prob);
        }

        // add back if the queen of spades and two of clubs were removed based
        // on the boolean
        if (hasQueenSpades) {
            matrix.put(new Card(Card.SPADES, Card.QUEEN), new Double(0.0001));
        }

        if (hasTwoClubs) {
            matrix.put(new Card(Card.CLUBS, Card.TWO), new Double(0.5));
        }

    }

    public Card playCard(byte suitOfTrick, LinkedList<Card> soFarTrick) {
        // based on the existing cards that the bot has, it will decide what it
        // can play.
        // Decision made on the maximization of probability.
        // also using the memory from the prior tricks
        if (soFarTrick.size() == 0) {
            // bot is starting this trick
        } else {
            // continuing a trick
        }

        return null;
    }

    public void postPlayState() {
        // re-normalizing probabilites based on memory and cards remaining. Sort
        // of like in initialize
    }

    public void addToMemory(LinkedList<Card> trickPlayed) {
        if (trickMemory.size() == 4) {
            // already full, remove the first list, oldest memory.

        } else {
            // just add the new cards to the memory.
        }
    }

    public int getMyScore() {
        return myScore;
    }

    public void setMyScore(int myScore) {
        this.myScore = myScore;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }
}