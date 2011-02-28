package org.bitcoma.hearts;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

//SMARTIE PANT-STATICA!
public class BotPlay {

    public static boolean isBot(Long playerId) {
        if (playerId != null && playerId > 0 && playerId <= 10) {
            return true;
        }
        return false;
    }

    private static int[] suitVariety(Collection<Card> cards) {
        // computes the variety of suits for the existing cards.
        Iterator<Card> cardIter = cards.iterator();
        int[] suitRep = new int[4];

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
        return suitRep;
    }

    private static int mostRepresentedSuit(Collection<Card> cards) {
        int[] suitRep = suitVariety(cards);
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

    private static LinkedList<Card> getSuitCards(int s, Collection<Card> cards) {
        // gives you cards of the suit
        LinkedList<Card> result = new LinkedList<Card>();
        Iterator<Card> cardIter = cards.iterator();
        while (cardIter.hasNext()) {
            Card considered = cardIter.next();
            if (considered.getSuit() == s) {
                result.add(considered);
            }
        }
        return result;
    }

    public static LinkedList<Card> removeThree(Collection<Card> cards) {
        // at the beginning of the round, the bot has to get rid of three cards.
        /*
         * Rules the bot follows: 1. Definitely removes the Queen of Spades if
         * it has that. 2. Checks how many cards of each suit it has and makes
         * sure to have as much suit variety as possible. 3. Remove high cards
         * from the suit. TODO: @MADIHA weights to be added.
         */
        LinkedList<Card> cardsCopy = new LinkedList<Card>(cards);

        // Just to make sure we don't use it again.
        cards = null;
        LinkedList<Card> givenAway = new LinkedList<Card>();
        Iterator<Card> cardIter = cardsCopy.iterator();
        while (cardIter.hasNext()) {
            Card considered = cardIter.next();
            if (considered.getSuit() == Card.SPADES && considered.getRank() == Card.QUEEN) {
                // explicit search for queen of spades
                cardsCopy.remove(considered);
                givenAway.add(considered);
                break;
            }
        }

        // Discard the card of the most represented suit and one that has the
        // highest value.
        while (givenAway.size() < 3) {
            int bestSuit = mostRepresentedSuit(cardsCopy);
            LinkedList<Card> suitCards = getSuitCards(bestSuit, cardsCopy);
            int bestRank = 0;
            Card removeMe = null;
            // find highest value in the suit
            for (Card c : suitCards) {
                if (c.getRank() > bestRank) {
                    bestRank = c.getRank();
                    removeMe = c;
                }
            }
            cardsCopy.remove(removeMe);
            givenAway.add(removeMe);
        }
        return givenAway;
    }

    private static Card findQueen(Collection<Card> cards) {
        for (Card c : cards)
            if (c.equals(Card.QUEEN_SPADES))
                return c;

        return null;
    }

    private static boolean hasTwoClubs(Collection<Card> cards) {
        return cards.contains(Card.TWO_CLUBS);
    }

    public static Card playCard(Trick trick, Collection<Card> cards, Collection<Card> allPlayed) {

        Collection<Card> trickCards = trick.getPlayerIdToCardMap().values();
        byte suitOfTrick = trick.getSuitOfTrick();

        if (trickCards.size() == 0) {

            // check if have Two of clubs to not break rules
            LinkedList<Card> clubCards = getSuitCards(Card.CLUBS, cards);
            for (Card c : clubCards) {
                if (c.getRank() == Card.TWO)
                    return c;
            }
            // if this the last card - no computation, just throw it.
            if (cards.size() == 1) {
                return cards.iterator().next();
            }

            Card[] sortedCards = new Card[cards.size()];
            sortedCards = cards.toArray(sortedCards);

            // sort all the cards by value and throw the highest value if hearts
            // has not been played so far.
            Card.sortCards(sortedCards);

            if (getSuitCards(Card.HEARTS, allPlayed).size() != 0) {
                // play lower cards to avoid getting points because hearts has
                // been played
                // TODO: check with Jon - if the lowest card is a heart, should
                // it be skipped?
                int index = 0;
                while (index < sortedCards.length && sortedCards[index].getSuit() == Card.HEARTS) {
                    index++;
                }
                // if you have all hearts left - then you play a lower heart
                if (index == sortedCards.length)
                    index = 0;
                return sortedCards[index];
            } else {

                // play higher cards as you won't get points
                // No hearts played and we lead the trick. So we have to
                // make sure to not play a heart or queen of spades unless that
                // is the only
                // card we have in our hand.
                for (int i = sortedCards.length - 1; i >= 0; --i) {
                    if (sortedCards[i].getSuit() != Card.HEARTS && !sortedCards[i].equals(Card.QUEEN_SPADES))
                        return sortedCards[i];
                }

                // Play queen if we have it. REQUIRED.
                Card queenOfSpades = findQueen(cards);
                if (queenOfSpades != null)
                    return queenOfSpades;

                // just play the lowest card since we only have hearts and
                // this will enable others to give us points.
                return sortedCards[0];
            }

        } else {
            // continuing a trick
            /*
             * If you have the suit of the trick.. goal is not lose and so any
             * card of a low value from that suit works! However if the suit is
             * spades and you have the queen and a higher spade is on the table,
             * you play it.
             */
            LinkedList<Card> suitCards = getSuitCards(suitOfTrick, cards);

            if (suitCards.size() != 0) {

                // Play queen of spades if there is a spade higher than queen
                // and we have queen
                Card queenOfSpades = findQueen(cards);
                if (suitOfTrick == Card.SPADES && queenOfSpades != null) {
                    boolean aboveQueenSpades = false;
                    for (Card c : trickCards) {
                        if (c.getSuit() == Card.SPADES && c.getRank() > Card.QUEEN) {
                            aboveQueenSpades = true;
                            break;
                        }
                    }

                    if (aboveQueenSpades) {
                        return queenOfSpades;
                    }
                }

                // find highest card of the suit but possibly lower than the
                // highest trick cards played already.
                // sort the suit cards
                Card[] sortedCards = new Card[suitCards.size()];
                sortedCards = suitCards.toArray(sortedCards);
                Card.sortCards(sortedCards);

                // sort the trick cards:
                Iterator<Card> trickCheck = trickCards.iterator();
                Card[] sortTrick = new Card[trickCards.size()];
                int tCount = 0;
                while (trickCheck.hasNext()) {
                    sortTrick[tCount] = trickCheck.next();
                    tCount++;
                }
                Card.sortCards(sortTrick);

                for (int i = sortedCards.length - 1; i >= 0; i--) {
                    for (int j = sortTrick.length - 1; j >= 0; j--) {
                        if (sortedCards[i].getRank() < sortTrick[j].getRank())
                            return sortedCards[i];
                    }

                }
                // none of the cards were lower than all of the trick cards
                // played so far. So let's play the lowest card
                return sortedCards[0];

            } else {
                /*
                 * If you do not have the suit being played then you will not
                 * lose this trick. So throw down cards in this order. Queen of
                 * Spades, any hearts (high to low), any other suit (high to
                 * low).
                 */
                Card queenOfSpades = findQueen(cards);
                if (queenOfSpades != null) {
                    return queenOfSpades;
                }

                suitCards = getSuitCards(Card.HEARTS, cards);
                // have no hearts cards
                if (suitCards.size() == 0) {
                    // Throw down ace/king of spades first to try and not get
                    // Queen of Spades, then any other high card

                    // Sort cards by rank first
                    Card[] sortedCards = new Card[cards.size()];
                    sortedCards = cards.toArray(sortedCards);
                    Card.sortCards(sortedCards);

                    // Check all kings and aces
                    for (int i = sortedCards.length - 1; i >= 0 && sortedCards[i].getRank() >= Card.KING; --i) {
                        byte suit = sortedCards[i].getSuit();
                        byte rank = sortedCards[i].getRank();
                        if (suit == Card.SPADES && (rank == Card.ACE || rank == Card.KING))
                            return sortedCards[i];
                    }

                    // Throw down highest rank card.
                    return sortedCards[sortedCards.length - 1];
                }
                // have hearts to throw, so throw highest hearts first
                else {
                    Card[] arrCards = new Card[suitCards.size()];
                    arrCards = suitCards.toArray(arrCards);

                    Card.sortCards(arrCards);
                    return arrCards[arrCards.length - 1];
                }
            }
        }
    }
}
