package org.bitcoma.hearts;

import java.util.Arrays;

public class Card implements Comparable<Card> {

	// Suit values
	public static final byte SPADES = 0;
	public static final byte HEARTS = 1;
	public static final byte CLUBS = 2;
	public static final byte DIAMONDS = 3;

	// Rank values
	public static final byte TWO = 2;
	public static final byte THREE = 3;
	public static final byte FOUR = 4;
	public static final byte FIVE = 5;
	public static final byte SIX = 6;
	public static final byte SEVEN = 7;
	public static final byte EIGHT = 8;
	public static final byte NINE = 9;
	public static final byte TEN = 10;
	public static final byte JACK = 11;
	public static final byte QUEEN = 12;
	public static final byte KING = 13;
	public static final byte ACE = 14;

	private static final byte NUM_RANKS = 13;

	public Card(byte suit, byte rank) {
		value = (byte) (suit * NUM_RANKS + rank - 2);
	}

	public Card(byte value) {
		this.value = value;
	}

	public byte getSuit() {
		return (byte) (value / NUM_RANKS);
	}

	public byte getRank() {
		return (byte) ((value % NUM_RANKS) + 2);
	}

	public int getPoints() {
		if (getSuit() == HEARTS)
			return 1;
		if (getSuit() == SPADES && getRank() == QUEEN)
			return 13;
		return 0;
	}

	public static void sortCards(Card[] input) {
		Arrays.sort(input);
	}

	private byte value;

	public String toString() {
	    String suit = new String();
	    /*switch (this.getSuit())
	    {
	        case 0:
	            suit = "Spades";
	            break;
	        case 1:
	            suit = "Hearts";
	            break;
	        case 2:
	            suit = "Clubs";
	            break;
	        case 3:
	            suit = "Diamonds";
	            break;
	        default:
	            suit = "UNknown";
	            break;
	    }*/
		return "Suit[" + this.getSuit() + "] " + "Value[" + this.getRank() + "]";
	}

	@Override
	public int compareTo(Card o) {
		if (this.getRank() < o.getRank())
			return -1;
		else if (this.getRank() == o.getRank())
			return 0;
		else
			return 1;
	}
}
