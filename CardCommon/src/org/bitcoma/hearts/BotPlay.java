package org.bitcoma.hearts;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

//SMARTIE PANT-STATICA!
public class BotPlay {

    public static boolean isBot(Long playerId) {
        if (playerId > 0 && playerId <= 10) {
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
            if (c.getSuit() == Card.SPADES && c.getRank() == Card.QUEEN)
                return c;

        return null;
    }

    private static boolean hasTwoClubs(Collection<Card> cards) {
        return (getSuitCards(Card.CLUBS, cards).contains(new Card(Card.CLUBS, Card.TWO)));
    }

    public static Card playCard(byte suitOfTrick, Collection<Card> trickCards, Collection<Card> cards,
            Collection<Card> allPlayed) {
        System.out.println("Bot's cards are " + cards);
        Card selected = null;

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
            Iterator<Card> cardIter = cards.iterator();
            int cardI = 0;

            while (cardIter.hasNext()) {
                Card c = cardIter.next();
                sortedCards[cardI] = c;
                cardI++;

            }

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
                // however if the highest card is a Queen of Spades, check if
                // King and Ace have been played already
                if (sortedCards[sortedCards.length - 1].getSuit() == Card.SPADES
                        && sortedCards[sortedCards.length - 1].getRank() == Card.QUEEN) {
                    Iterator<Card> allPlayedIter = allPlayed.iterator();

                    boolean kingSPlayed = false;
                    boolean aceSPlayed = false;

                    while (allPlayedIter.hasNext()) {
                        Card temp = allPlayedIter.next();
                        if (temp.getRank() == Card.KING && temp.getSuit() == Card.SPADES)
                            kingSPlayed = true;
                        if (temp.getRank() == Card.ACE && temp.getSuit() == Card.SPADES)
                            aceSPlayed = true;
                    }
                    // both true, feel free to play the QUEEN
                    if (kingSPlayed && aceSPlayed)
                        return sortedCards[sortedCards.length - 1];
                    else {
                        // play the next highest card
                        // we know for sure that the bot has at least two cards
                        // still left.
                        int start = sortedCards.length - 2;
                        while (sortedCards[start].getSuit() == Card.HEARTS && start > 0)
                            start--;
                        if (start == 0) {
                            // bot only has hearts
                            return sortedCards[sortedCards.length - 2];
                        }
                        if (start > 0)
                            return sortedCards[start];
                        if (start < 0)
                            System.out.println("This should never happen");
                    }
                } else {
                    // just play the highest card
                    return sortedCards[sortedCards.length - 1];
                }
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
                // trick cards played already.
                // sort the suit cards
                Card[] sortedCards = new Card[suitCards.size()];
                for (int i = 0; i < suitCards.size(); i++)
                    sortedCards[i] = suitCards.get(i);
                Card.sortCards(sortedCards);
                boolean lookMore = false;
                for (int i = sortedCards.length - 1; i >= 0; i--) {
                    Iterator<Card> trickIter = trickCards.iterator();
                    lookMore = false;
                    while (trickIter.hasNext()) {
                        if (sortedCards[i].getRank() < trickIter.next().getRank()) {
                            // keep going
                        } else {
                            // this was the highest card that is higher than
                            // the trick cards played.
                            // so let's try a lower card
                            lookMore = true;
                            break;
                        }
                        if (!lookMore)
                            return sortedCards[i];
                    }
                }
                // none of the cards were lower than all of the trick cards
                // played so far. So let's play the lowest card
                return sortedCards[0];

            } else {
                /*
                 * If you do not have the suit being played then you have
                 * already lost the trick. So throw down cards in this order.
                 * Queen of Spades, any hearts (high to low), any other suit
                 * (high to low).
                 */
                suitCards = getSuitCards(Card.SPADES, cards);

                Card queenOfSpades = findQueen(cards);
                if (queenOfSpades != null) {
                    return queenOfSpades;
                }

                suitCards = getSuitCards(Card.HEARTS, cards);
                // have no hearts cards
                if (suitCards.size() == 0) {
                    LinkedList<Card> suitCards1 = getSuitCards(Card.DIAMONDS, cards);
                    suitCards = suitCards1;
                    suitCards1 = getSuitCards(Card.CLUBS, cards);
                    for (Card s : suitCards1) {
                        suitCards.add(s);
                    }
                    suitCards1 = getSuitCards(Card.SPADES, cards);
                    for (Card s : suitCards1) {
                        suitCards.add(s);
                    }
                    Card[] arrCards = new Card[suitCards.size()];
                    for (int count = 0; count < suitCards.size(); count++) {
                        arrCards[count] = suitCards.get(count);
                    }
                    return arrCards[arrCards.length - 1];

                }
                // have hearts to throw
                else {
                    Card[] arrCards = new Card[suitCards.size()];
                    for (int count = 0; count < suitCards.size(); count++) {
                        arrCards[count] = suitCards.get(count);
                    }

                    Card.sortCards(arrCards);
                    return arrCards[arrCards.length - 1];
                }
            }

        }
        System.out.println("Never reach this situation");
        return selected;
    }
}
