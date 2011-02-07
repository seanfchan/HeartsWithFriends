package org.bitcoma.hearts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class BotPlayTest {

    @Test
    public void testIsBot() {
        for (int i = 1; i < 11; i++)
            assertTrue("Player id: " + i + " should be a bot", BotPlay.isBot((long) i));
    }

    @Test
    public void testRemoveThree() {
        fail("Not yet implemented");
    }

    @Test
    public void testPlayCard() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();
        Trick currentTrick = new Trick();

        botHand.add(new Card(Card.CLUBS, Card.TWO));
        botHand.add(new Card(Card.HEARTS, Card.TWO));
        botHand.add(new Card(Card.DIAMONDS, Card.THREE));
        botHand.add(new Card(Card.SPADES, Card.FOUR));
        botHand.add(new Card(Card.DIAMONDS, Card.ACE));

        Card botCardToPlay = BotPlay.playCard(currentTrick.getSuitOfTrick(), currentTrick.getPlayerIdToCardMap()
                .values(), botHand, allCardsPlayed);

        assertEquals("Should play two of clubs", new Card(Card.CLUBS, Card.TWO), botCardToPlay);
    }

    @Test
    public void testPlayCard1() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();
        List<Card> trickCards = new LinkedList<Card>();

        trickCards.add(new Card(Card.HEARTS, Card.EIGHT));

        botHand.add(new Card(Card.HEARTS, Card.TWO));
        botHand.add(new Card(Card.DIAMONDS, Card.THREE));
        botHand.add(new Card(Card.SPADES, Card.FOUR));
        botHand.add(new Card(Card.DIAMONDS, Card.ACE));

        Card botCardToPlay = BotPlay.playCard(Card.HEARTS, trickCards, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
        assertEquals("Should play heart", Card.HEARTS, botCardToPlay.getSuit());
        assertEquals("Should play two of hearts", Card.TWO, botCardToPlay.getRank());
    }

    @Test
    public void testPlayCard2() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();
        List<Card> trickCards = new LinkedList<Card>();

        trickCards.add(new Card(Card.CLUBS, Card.EIGHT));

        botHand.add(new Card(Card.HEARTS, Card.TWO));
        botHand.add(new Card(Card.DIAMONDS, Card.THREE));
        botHand.add(new Card(Card.SPADES, Card.FOUR));
        botHand.add(new Card(Card.DIAMONDS, Card.ACE));

        Card botCardToPlay = BotPlay.playCard(Card.CLUBS, trickCards, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
        assertEquals("Should play heart b/c they don't have a spade", Card.HEARTS, botCardToPlay.getSuit());
        assertEquals("Should play two of hearts", Card.TWO, botCardToPlay.getRank());
    }

    @Test
    public void testPlayCard3() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();
        List<Card> trickCards = new LinkedList<Card>();

        botHand.add(new Card(Card.HEARTS, Card.TWO));
        botHand.add(new Card(Card.DIAMONDS, Card.THREE));
        botHand.add(new Card(Card.SPADES, Card.FOUR));
        botHand.add(new Card(Card.DIAMONDS, Card.ACE));

        // Heart has been played
        allCardsPlayed.add(new Card(Card.HEARTS, Card.FIVE));

        // No trick cards and we go first. Should not play a club.
        Card botCardToPlay = BotPlay.playCard(Card.CLUBS, trickCards, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
    }

    @Test
    public void testPlayCard4() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();
        List<Card> trickCards = new LinkedList<Card>();

        trickCards.add(new Card(Card.SPADES, Card.FIVE));
        trickCards.add(new Card(Card.SPADES, Card.QUEEN));
        trickCards.add(new Card(Card.SPADES, Card.THREE));

        botHand.add(new Card(Card.SPADES, Card.ACE));
        botHand.add(new Card(Card.SPADES, Card.FOUR));
        botHand.add(new Card(Card.SPADES, Card.JACK));

        // Should play card below highest in trick if possible
        Card botCardToPlay = BotPlay.playCard(Card.SPADES, trickCards, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
        assertEquals("Should play Jack of Spades", new Card(Card.SPADES, Card.JACK), botCardToPlay);
    }

    @Test
    public void testWtfBug1() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();
        List<Card> trickCards = new LinkedList<Card>();

        trickCards.add(new Card(Card.SPADES, Card.TWO));
        trickCards.add(new Card(Card.SPADES, Card.SIX));
        trickCards.add(new Card(Card.SPADES, Card.THREE));

        botHand.add(new Card(Card.HEARTS, Card.TEN));
        botHand.add(new Card(Card.HEARTS, Card.TWO));
        botHand.add(new Card(Card.CLUBS, Card.KING));
        botHand.add(new Card(Card.CLUBS, Card.QUEEN));
        botHand.add(new Card(Card.CLUBS, Card.TEN));
        botHand.add(new Card(Card.SPADES, Card.TEN));
        botHand.add(new Card(Card.SPADES, Card.FOUR));
        botHand.add(new Card(Card.SPADES, Card.QUEEN));
        botHand.add(new Card(Card.HEARTS, Card.SEVEN));
        botHand.add(new Card(Card.DIAMONDS, Card.TEN));
        botHand.add(new Card(Card.DIAMONDS, Card.JACK));
        botHand.add(new Card(Card.DIAMONDS, Card.FOUR));

        // Should play card below highest in trick if possible
        Card botCardToPlay = BotPlay.playCard(Card.SPADES, trickCards, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
    }
}
