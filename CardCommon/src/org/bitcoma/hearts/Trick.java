package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.List;

public class Trick {

	public Trick() {
		playerIdToCardMap = new HashMap<Long, Card>();
		suitOfTrick = (byte)0xFF;
	}

	public boolean isMoveValid(Long playerId, Card cardToPlay, List<Card> playerCards) {
		int numCardsPlayed;
		synchronized (playerIdToCardMap) {
			numCardsPlayed = playerIdToCardMap.size();
		}
		
		// Set the suit with the first played card
		if(numCardsPlayed == 0) {
			suitOfTrick = cardToPlay.getSuit();
			return true;
		}
		else {
			// Card matches trick suit.
			if(cardToPlay.getSuit() == suitOfTrick)
				return true;
			else {
				// Do they have a card that can be played?
				for(Card c : playerCards) {
					if(c.getSuit() == suitOfTrick)
						return false;
				}
			}
		}
		
		return true;
	}
	
	public void makeMove(Long playerId, Card card) {
		synchronized(playerIdToCardMap) {
			playerIdToCardMap.put(playerId, card);
		}
	}
	
	private byte suitOfTrick;
	private HashMap<Long, Card> playerIdToCardMap;
}
