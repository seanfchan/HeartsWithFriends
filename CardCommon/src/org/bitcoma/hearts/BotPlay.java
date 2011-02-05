package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

//SMARTIE PANTS!
public class BotPlay {
    Long playerId;
	HashMap<Card, Double> matrix = new HashMap<Card, Double>();
	int myScore = 0;
	HashMap<Integer, LinkedList<Card>> trickMemory = new HashMap<Integer, LinkedList<Card>>();
	LinkedList<Card> givenAway = new LinkedList<Card>();
	int[] suitRep = new int[4]; // because we only have 4 suits.

	boolean myTurn = false;

	public BotPlay(Long id, LinkedList<Card> cards) {
	    playerId = id;
		for (Card c : cards) {
			matrix.put(c, new Double(0));
		}
		startComputing();
	}
	
	public LinkedList<Card> getBotCards()
	{
	    Iterator<Card> cardIter = matrix.keySet().iterator();
	    LinkedList<Card> result = new LinkedList<Card>();
	    while (cardIter.hasNext())
	    {
	        result.add(cardIter.next());
	    }
	    return result;
	}

	public void startComputing() {
		// we expect receiveThree function to be called here.

		// TODO @madiha: Wait to compute probabilities until the bot has
		// received all three card.

		// we would skip initialization if the bot replaced a person in the
		// middle of the game, as then
		// there is no way to have full memory of what is going on.
		//removeThree();
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
			if (considered.getSuit() == Card.SPADES
					&& considered.getRank() == Card.QUEEN) {
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
		Card holdTwoClubs = null;
		Card holdSpadeQueen = null;

		Iterator<Card> cardIter = matrix.keySet().iterator();
		while (cardIter.hasNext()) {
			Card temp = cardIter.next();
			if (temp.getSuit() == Card.CLUBS && temp.getRank() == Card.TWO) {
			    holdTwoClubs = temp;
				hasTwoClubs = true;
			}

			if (temp.getSuit() == Card.SPADES && temp.getRank() == Card.QUEEN) {
				// very low probability - this is shit!	
			    holdSpadeQueen = temp;
				hasQueenSpades = true;
			}
		}

		double remProb = 1;
		int remCards = length;

		if (hasTwoClubs) {

		    matrix.remove(holdTwoClubs);
			remProb -= new Double(0.5);
			remCards--;
		}
		if (hasQueenSpades) {
		    matrix.remove(holdSpadeQueen);
			remProb -= new Double(0.0001);
			remCards--;
		}

		// depending on the values of the remaining cards, split probabilities.
		Card[] remainingCards = new Card[matrix.keySet().size()];
		Iterator<Card> tempIter = matrix.keySet().iterator();
		int counter = 0;
		while (tempIter.hasNext())
		{
		    remainingCards[counter] = tempIter.next();
		    counter++;
		}
		//Card[] remainingCards = (Card[]) matrix.keySet().toArray();
		Card.sortCards(remainingCards);

		int sumOfValues = 0;
		for (Card c : remainingCards)
			sumOfValues += c.getRank();

		// each card get a probability inversely proportional to its rank
		// regardless of
		// the suit. However lower value will get higher probability as your
		// goal is not to
		// lose the trick (not shooting moon right now).
		// chose 15 to ensure we have all non-zero probabilities.

		for (Card c : matrix.keySet()) {
			double prob = (double) (15 - c.getRank()) / (double) sumOfValues;
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

	public boolean hasQueen() {
		return (getSuitCards(Card.SPADES).contains(new Card(Card.SPADES,
				Card.QUEEN)));
	}

	public Card playCard(byte suitOfTrick, LinkedList<Card> soFarTrick) {
		// based on the existing cards that the bot has, it will decide what it
		// can play.
		// Decision made on the maximization of probability.
		// also using the memory from the prior tricks
		Card selected = null;
		if (soFarTrick.size() == 0) {
			// bot is starting this trick - automatically works for Two of clubs
			// otherwise, it will choose the card with the highest probability.
			// which will be a low-valued card
			// exceptions are taken care of in postplaystate function.
			double highestProb = new Double(0);

			Iterator<Card> cardIter = matrix.keySet().iterator();
			while (cardIter.hasNext()) {
				Card considered = cardIter.next();
				if (matrix.get(considered) > highestProb) {				    
					highestProb = matrix.get(considered);
					selected = considered;
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
			LinkedList<Card> suitCards = getSuitCards(suitOfTrick);
			if (suitCards.size() != 0) {
				if (suitOfTrick == Card.SPADES && hasQueen()) {
					for (Card look : soFarTrick) {
						if (look.getRank() > Card.QUEEN) {
							selected = look;
						}
					}
				} else {
					// loop through and find the card with the highest
					// probability.
					double highestProb = new Double(0);
					for (Card c : suitCards) {
						if (matrix.get(c) > highestProb) {
							highestProb = matrix.get(c);
							selected = c;
						}
					}
				}
			} else {
				/*
				 * If you do not have the suit being played then you have
				 * already lost the trick. So throw down cards in this order.
				 * Queen of Spades, any hearts (high to low), any other suit
				 * (high to low).
				 */
				suitCards = getSuitCards(Card.SPADES);

				Card checkQueen = new Card(Card.SPADES, Card.QUEEN);
				if (suitCards.size() != 0 && suitCards.contains(checkQueen)) {
					selected = checkQueen;
				} else {
					suitCards = getSuitCards(Card.HEARTS);
					if (suitCards.size() == 0) {
						LinkedList<Card> suitCards1 = getSuitCards(Card.DIAMONDS);
						suitCards = suitCards1;
						suitCards1 = getSuitCards(Card.CLUBS);
						for (Card s : suitCards1) {
							suitCards.add(s);
						}
						suitCards1 = getSuitCards(Card.SPADES);
						for (Card s : suitCards1) {
							suitCards.add(s);
						}
                        Card[] arrCards = new Card[suitCards.size()];
                        for (int count = 0; count < suitCards.size(); count++)
                        {
                            arrCards[count] = suitCards.get(count);
                        }
						selected = arrCards[arrCards.length - 1];
					} else {
					    Card[] arrCards = new Card[suitCards.size()];
					    for (int count = 0; count < suitCards.size(); count++)
					    {
					        arrCards[count] = suitCards.get(count);
					    }
						
						Card.sortCards(arrCards);
						selected = arrCards[arrCards.length - 1];
					}
				}
			}

		}

		matrix.remove(selected);
		postPlayState();
		return selected;
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