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

        Card botCardToPlay = BotPlay.playCard(currentTrick, botHand, allCardsPlayed);

        assertEquals("Should play two of clubs", new Card(Card.CLUBS, Card.TWO), botCardToPlay);
    }

    @Test
    public void testPlayCard1() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();

        Trick trick = new Trick();
        trick.makeMove((long) 0, new Card(Card.HEARTS, Card.EIGHT));

        botHand.add(new Card(Card.HEARTS, Card.TWO));
        botHand.add(new Card(Card.DIAMONDS, Card.THREE));
        botHand.add(new Card(Card.SPADES, Card.FOUR));
        botHand.add(new Card(Card.DIAMONDS, Card.ACE));

        Card botCardToPlay = BotPlay.playCard(trick, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
        assertEquals("Should play heart", Card.HEARTS, botCardToPlay.getSuit());
        assertEquals("Should play two of hearts", Card.TWO, botCardToPlay.getRank());
    }

    @Test
    public void testPlayCard2() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();

        Trick trick = new Trick();
        trick.makeMove((long) 0, new Card(Card.CLUBS, Card.EIGHT));

        botHand.add(new Card(Card.HEARTS, Card.TWO));
        botHand.add(new Card(Card.DIAMONDS, Card.THREE));
        botHand.add(new Card(Card.SPADES, Card.FOUR));
        botHand.add(new Card(Card.DIAMONDS, Card.ACE));

        Card botCardToPlay = BotPlay.playCard(trick, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
        assertEquals("Should play heart b/c they don't have a spade", Card.HEARTS, botCardToPlay.getSuit());
        assertEquals("Should play two of hearts", Card.TWO, botCardToPlay.getRank());
    }

    @Test
    public void testPlayCard3() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();
        Trick trick = new Trick();

        botHand.add(new Card(Card.HEARTS, Card.TWO));
        botHand.add(new Card(Card.DIAMONDS, Card.THREE));
        botHand.add(new Card(Card.SPADES, Card.FOUR));
        botHand.add(new Card(Card.DIAMONDS, Card.ACE));

        // Heart has been played
        allCardsPlayed.add(new Card(Card.HEARTS, Card.FIVE));

        // No trick cards and we go first. Should not play a club.
        Card botCardToPlay = BotPlay.playCard(trick, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
    }

    @Test
    public void testPlayCard4() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();

        Trick trick = new Trick();
        trick.makeMove((long) 0, new Card(Card.CLUBS, Card.EIGHT));

        trick.makeMove((long) 0, new Card(Card.SPADES, Card.FIVE));
        trick.makeMove((long) 0, new Card(Card.SPADES, Card.QUEEN));
        trick.makeMove((long) 0, new Card(Card.SPADES, Card.THREE));

        botHand.add(new Card(Card.SPADES, Card.ACE));
        botHand.add(new Card(Card.SPADES, Card.FOUR));
        botHand.add(new Card(Card.SPADES, Card.JACK));

        // Should play card below highest in trick if possible
        Card botCardToPlay = BotPlay.playCard(trick, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
        assertEquals("Should play Jack of Spades", new Card(Card.SPADES, Card.JACK), botCardToPlay);
    }

    @Test
    public void testWtfBug1() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();

        Trick trick = new Trick();
        trick.makeMove((long) 0, new Card(Card.SPADES, Card.TWO));
        trick.makeMove((long) 0, new Card(Card.SPADES, Card.SIX));
        trick.makeMove((long) 0, new Card(Card.SPADES, Card.THREE));

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
        Card botCardToPlay = BotPlay.playCard(trick, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
    }

    @Test
    public void testLeadingTrickWithHighHearts() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();
        Trick trick = new Trick();

        allCardsPlayed.add(new Card(Card.CLUBS, Card.TWO));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.TEN));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.NINE));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.QUEEN));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.TWO));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.JACK));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.EIGHT));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.TEN));
        allCardsPlayed.add(new Card(Card.SPADES, Card.ACE));
        allCardsPlayed.add(new Card(Card.SPADES, Card.TEN));
        allCardsPlayed.add(new Card(Card.SPADES, Card.QUEEN));
        allCardsPlayed.add(new Card(Card.SPADES, Card.SEVEN));

        botHand.add(new Card(Card.HEARTS, Card.THREE));
        botHand.add(new Card(Card.HEARTS, Card.FIVE));
        botHand.add(new Card(Card.CLUBS, Card.FIVE));
        botHand.add(new Card(Card.SPADES, Card.FIVE));
        botHand.add(new Card(Card.CLUBS, Card.SIX));
        botHand.add(new Card(Card.CLUBS, Card.SEVEN));
        botHand.add(new Card(Card.SPADES, Card.NINE));
        botHand.add(new Card(Card.CLUBS, Card.TEN));
        botHand.add(new Card(Card.HEARTS, Card.QUEEN));
        botHand.add(new Card(Card.CLUBS, Card.KING));
        botHand.add(new Card(Card.HEARTS, Card.ACE));
        botHand.add(new Card(Card.DIAMONDS, Card.QUEEN));

        Card botCardToPlay = BotPlay.playCard(trick, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
        // This check is done in is move valid. Should be figured into picking a
        // bot card as well.
        assertTrue("Should not be a heart. No hearts played and we lead the trick with other cards to play.",
                botCardToPlay.getSuit() != Card.HEARTS);
    }

    @Test
    public void testLeadingTrickWithOnlyHearts() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();
        Trick trick = new Trick();

        allCardsPlayed.add(new Card(Card.CLUBS, Card.TWO));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.TEN));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.NINE));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.QUEEN));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.TWO));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.JACK));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.EIGHT));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.TEN));
        allCardsPlayed.add(new Card(Card.SPADES, Card.ACE));
        allCardsPlayed.add(new Card(Card.SPADES, Card.TEN));
        allCardsPlayed.add(new Card(Card.SPADES, Card.QUEEN));
        allCardsPlayed.add(new Card(Card.SPADES, Card.SEVEN));

        botHand.add(new Card(Card.HEARTS, Card.FIVE));
        botHand.add(new Card(Card.HEARTS, Card.SEVEN));
        botHand.add(new Card(Card.HEARTS, Card.EIGHT));

        // Should play a heart even though no hearts have been played
        Card botCardToPlay = BotPlay.playCard(trick, botHand, allCardsPlayed);

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
        assertTrue("Should be a heart. No hearts played and we lead the trick with only hearts to play.",
                botCardToPlay.getSuit() == Card.HEARTS);
    }

    @Test
    public void testWtfBug2() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();
        Trick trick = new Trick();

        allCardsPlayed.add(new Card(Card.CLUBS, Card.TWO));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.THREE));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.FOUR));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.NINE));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.ACE));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.TEN));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.QUEEN));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.EIGHT));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.KING));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.NINE));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.JACK));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.FIVE));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.KING));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.JACK));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.EIGHT));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.SEVEN));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.QUEEN));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.TEN));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.SIX));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.FIVE));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.SEVEN));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.FOUR));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.SIX));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.THREE));
        allCardsPlayed.add(new Card(Card.SPADES, Card.SEVEN));
        allCardsPlayed.add(new Card(Card.SPADES, Card.FIVE));
        allCardsPlayed.add(new Card(Card.SPADES, Card.SIX));
        allCardsPlayed.add(new Card(Card.SPADES, Card.THREE));

        botHand.add(new Card(Card.HEARTS, Card.FOUR));
        botHand.add(new Card(Card.HEARTS, Card.SIX));
        botHand.add(new Card(Card.HEARTS, Card.SEVEN));
        botHand.add(new Card(Card.HEARTS, Card.TEN));
        botHand.add(new Card(Card.HEARTS, Card.QUEEN));
        botHand.add(new Card(Card.SPADES, Card.TWO));

        // Should play a heart even though no hearts have been played
        Card botCardToPlay = BotPlay.playCard(trick, botHand, allCardsPlayed);
        // Hearts[10]

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
        assertTrue("Should not be a heart. No hearts played and we lead the trick with only hearts to play.",
                botCardToPlay.getSuit() != Card.HEARTS);
    }

    @Test
    public void testWtfBug3() {
        List<Card> botHand = new LinkedList<Card>();
        List<Card> allCardsPlayed = new LinkedList<Card>();
        Trick trick = new Trick();

        allCardsPlayed.add(new Card(Card.CLUBS, Card.TWO));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.SIX));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.FOUR));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.FIVE));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.ACE));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.JACK));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.QUEEN));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.NINE));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.KING));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.TEN));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.SEVEN));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.EIGHT));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.TEN));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.SIX));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.THREE));
        allCardsPlayed.add(new Card(Card.CLUBS, Card.NINE));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.TEN));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.EIGHT));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.SEVEN));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.FIVE));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.NINE));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.FOUR));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.QUEEN));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.JACK));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.ACE));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.KING));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.SIX));
        allCardsPlayed.add(new Card(Card.DIAMONDS, Card.THREE));

        botHand.add(new Card(Card.HEARTS, Card.FOUR));
        botHand.add(new Card(Card.HEARTS, Card.THREE));
        botHand.add(new Card(Card.HEARTS, Card.JACK));
        botHand.add(new Card(Card.HEARTS, Card.NINE));
        botHand.add(new Card(Card.SPADES, Card.QUEEN));
        botHand.add(new Card(Card.SPADES, Card.TWO));

        // Should play a heart even though no hearts have been played
        Card botCardToPlay = BotPlay.playCard(trick, botHand, allCardsPlayed);
        // Hearts[11]

        assertTrue("Should play a card in our hand", botHand.contains(botCardToPlay));
        assertTrue("Should not be a heart. No hearts played and we lead the trick with only hearts to play.",
                botCardToPlay.getSuit() != Card.HEARTS);
    }
}
