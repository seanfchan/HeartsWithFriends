package org.bitcoma.hearts;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class TrickTest {

    @Test
    public void testIsMoveValid() {

        Trick currentTrick = new Trick();

        List<Card> playerHand = new LinkedList<Card>();

        playerHand.add(new Card(Card.CLUBS, Card.TWO));
        playerHand.add(new Card(Card.HEARTS, Card.THREE));
        playerHand.add(new Card(Card.SPADES, Card.FIVE));

        boolean result = currentTrick
                .isMoveValid((long) 0, new Card(Card.CLUBS, Card.TWO), playerHand, (long) 0, false);
        assertTrue("Should be able to play Two of Clubs if in hand.", result);

        result = currentTrick.isMoveValid((long) 0, new Card(Card.SPADES, Card.FIVE), playerHand, (long) 0, false);
        assertFalse("Should force play Two of Clubs if in hand.", result);

        playerHand.clear();
        playerHand.add(new Card(Card.HEARTS, Card.THREE));
        playerHand.add(new Card(Card.SPADES, Card.FIVE));

        result = currentTrick.isMoveValid((long) 0, new Card(Card.HEARTS, Card.THREE), playerHand, (long) 0, true);
        assertTrue("Should allow hearts once played.", result);

        result = currentTrick.isMoveValid((long) 0, new Card(Card.HEARTS, Card.KING), playerHand, (long) 0, true);
        assertFalse("Should not allow a card not in your hand.", result);
    }

}
