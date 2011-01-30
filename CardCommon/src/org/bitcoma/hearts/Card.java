package org.bitcoma.hearts;

public class Card {

    public static final byte SPADES = 0;
    public static final byte HEARTS = 1;
    public static final byte CLUBS = 2;
    public static final byte DIAMONDS = 3;

    private static final byte NUM_RANKS = 13;

    public Card(byte suit, byte rank) {
        value = (byte) (suit * NUM_RANKS + rank);
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

    private byte value;
}
