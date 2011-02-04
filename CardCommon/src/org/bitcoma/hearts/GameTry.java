package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class GameTry {
    
    public GameTry()
    {
        
    }
    
    public static void main(String[] args)
    {
        LinkedList<Long> playerIds = new LinkedList<Long>();
        for (int i = 1; i <= 4; i++)
            playerIds.add((long) i);
        
        Game myGame = new Game(playerIds);
        myGame.currentRound.shuffle(4);
        
        // TODO @madiha to @sean - There is no way in game to allow for Bot players to be added
        // masking players to be bots here.
        BotPlay player1 = new BotPlay(myGame.currentRound.getUserIdToHand().get((long) 1));
        BotPlay player2 = new BotPlay(myGame.currentRound.getUserIdToHand().get((long) 2));
        BotPlay player3 = new BotPlay(myGame.currentRound.getUserIdToHand().get((long) 3));
        BotPlay player4 = new BotPlay(myGame.currentRound.getUserIdToHand().get((long) 4));
        
        
    }
    
}