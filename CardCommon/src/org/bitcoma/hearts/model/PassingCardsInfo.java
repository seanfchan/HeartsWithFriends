package org.bitcoma.hearts.model;

import java.util.List;

import org.bitcoma.hearts.Card;

public class PassingCardsInfo {

    public long srcId;
    public long dstId;
    public List<Card> cards;

    public PassingCardsInfo(long srcId, long dstId, List<Card> cards) {
        this.srcId = srcId;
        this.dstId = dstId;
        this.cards = cards;
    }
}
