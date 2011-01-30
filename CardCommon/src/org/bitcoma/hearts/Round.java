package org.bitcoma.hearts;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class Round {

    //True when playing 4 player hearts
    private byte numOfCardsInDeck;
    private byte numOfCardsInUsersHand;
    private Map<Long, Byte> userIdToScoreInRound;
    private Map<Long, Byte> userIdToScoreInGame;
    private Map<Long, LinkedList<Byte>> cardsInHand;
    private Long loserId;

    public Round(int inputNumCardsInDeck, Map<Long, Byte> inputUserIdToScoreInGame, int numOfCardsInUsersHand) {
        cardsInHand = new HashMap<Long, LinkedList<Byte>>();
        userIdToScoreInRound = new HashMap<Long, Byte>();
        numOfCardsInDeck = (byte)inputNumCardsInDeck;
        userIdToScoreInGame = inputUserIdToScoreInGame;
        this.numOfCardsInUsersHand = (byte)numOfCardsInUsersHand;
        
        //Initializing score
        for(Long userId : userIdToScoreInGame.keySet())
        {
            userIdToScoreInRound.put(userId, (byte)0);
            cardsInHand.put(userId, new LinkedList<Byte>());
        }
    }

    public void shuffle() {
        Random rand = new Random();
        byte deck[] = new byte[numOfCardsInDeck];
        for (byte i = 0; i < numOfCardsInDeck; ++i)
            deck[i] = i;

        byte swapIndex = 0, tempValue = 0;
        for (byte i = 0; i < numOfCardsInDeck; ++i) {
            swapIndex = (byte) (rand.nextInt() % numOfCardsInDeck);
            tempValue = deck[swapIndex];
            deck[swapIndex] = deck[i];
            deck[i] = tempValue;
        }

        deal(deck);
    }

    public void deal(byte deck[]) {
        for(Long userIds : cardsInHand.keySet())
        {
            for(int i = 0; i < numOfCardsInUsersHand; ++i)
            {
                cardsInHand.get(userIds).add(deck[i]);
            }
        }
    }

    public boolean hasLoser(Map<Long, Byte> userIdToScoreInRound, Map<Long, Byte> userIdToScoreInGame) {
        for(Long userId : userIdToScoreInRound.keySet())
        {
            if((int)(userIdToScoreInGame.get(userId) +  userIdToScoreInRound.get(userId)) >= 100)
            {
                loserId = userId;
                return true;
            }
        }
        
        return false;
    }
    
    public Long getLoserId()
    {
        return loserId;
    }
}
