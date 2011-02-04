package org.bitcoma.hearts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class SmartAttack {

    public SmartAttack() {
        // nothing
    }

    public static void main(String[] args) {
        LinkedList<Long> playerIds = new LinkedList<Long>();
        for (int i = 1; i <= 4; i++)
            playerIds.add((long) i);

        HashMap<Long, String> name = new HashMap<Long, String>();

        System.out.println("Are you smarter than Smartie Pants, Michael Scott and uhm.. Eric Cartman? Let's see!");
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter your name:");
        String playerName = scan.nextLine();
        
        name.put((long) 1, "Smartie Pants");
        name.put((long) 2, "Michael Scott");
        name.put((long) 3, "Eric Cartman");
        name.put((long) 4, playerName);
        
        Game myGame = new Game(playerIds);
        int roundNum = 1;
          
        while (!myGame.isGameOver()) {

            System.out.println("Round " + roundNum);
            myGame.currentRound.shuffle(4);
            LinkedList<BotPlay> players = new LinkedList<BotPlay>();
            players.add(new BotPlay((long) 1, myGame.currentRound.getUserIdToHand().get((long) 1)));
            players.add(new BotPlay((long) 2, myGame.currentRound.getUserIdToHand().get((long) 2)));
            players.add(new BotPlay((long) 3, myGame.currentRound.getUserIdToHand().get((long) 3)));
            players.add(new BotPlay((long) 4, myGame.currentRound.getUserIdToHand().get((long) 4)));
            
            // setting player 4 to be you - the NON BOT
            
            int i = 0;
            for (BotPlay p : players) {
                System.out.println("Bot " + (i + 1) + " cards : " + p.matrix.keySet());
                i++;
            }
            System.out.println("\n");
            
            BotPlay first = null;
            // finding which player has two of clubs
            int num = 0;
            int playerNum = 0;
            boolean foundFirst = false;
            for (BotPlay p : players) {
                for (Card c : p.matrix.keySet()) {
                    if (c.getSuit() == Card.CLUBS && c.getRank() == Card.TWO) {
                        foundFirst = true;
                        break;
                    }
                }
                if (foundFirst)
                    break;
                num++;
            }
            playerNum = num;
            int tricks = 1;
            while (!myGame.currentRound.hasRoundEnded()) {
                
                Trick startTrick = new Trick();
                LinkedList<Card> soFarTrick = new LinkedList<Card>();
                System.out.println("It is " + name.get((long) (playerNum+1)) + "'s turn");
                boolean result = false;
                Card played = null;
                int index = 0;
                
                if (tricks == 1)
                {
                    // have to start with Two of Clubs 
                    first = players.get(playerNum);
                    played = first.playCard((byte) 0, soFarTrick);
                    System.out.println(name.get(first.playerId) + " played " + played);
                    result = startTrick.isMoveValid(first.playerId, played, first.getBotCards());
                    if (result) {
                        startTrick.makeMove(first.playerId, played);
                        myGame.currentRound.removeCard(first.playerId, played);
                    }
                    soFarTrick.add(played);
                }
                // starting trick
                // System.out.println(playerNum);
                else
                {
                    first = players.get(playerNum);
                    
                    if (first.playerId == 4)
                    {
                        System.out.println("Here are your cards:");
                        System.out.println(first.matrix.keySet());
                        System.out.println("Enter the index number of the card you want to select - indexing starts at zero");
                        index = scan.nextInt();
                        Iterator<Card> cardIter = first.matrix.keySet().iterator();
                        int counter = 0;
                        while (cardIter.hasNext())
                        {
                            Card s = cardIter.next();
                            if (counter == index)
                            {
                                played = s;
                                break;
                            }                          
                        }              
                    }
                    else
                    {
                        played = first.playCard((byte) 0, soFarTrick);    
                    }
                    result = startTrick.isMoveValid(first.playerId, played, first.getBotCards());
                    if (result) {
                        startTrick.makeMove(first.playerId, played);
                        myGame.currentRound.removeCard(first.playerId, played);
                    }
                    System.out.println(name.get(first.playerId) + " played " + played);
                    soFarTrick.add(played);
                }

                int nextPlayer = (playerNum + 1) % 4;
                Card played2 = null;
                if (nextPlayer == 3)
                {
                    // you
                    System.out.println("Here are your cards:");
                    System.out.println(players.get(nextPlayer).matrix.keySet());
                    System.out.println("Enter the index number of the card you want to select - indexing starts at zero");
                    index = scan.nextInt();
                    Iterator<Card> cardIter = players.get(nextPlayer).matrix.keySet().iterator();
                    int counter = 0;
                    while (cardIter.hasNext())
                    {
                        Card s = cardIter.next();
                        if (counter == index)
                        {
                            played2 = s;
                            break;
                        }                          
                    }              
                }
                else
                {
                    // bot
                    played2 = players.get(nextPlayer).playCard(played.getSuit(), soFarTrick);
                }
                result = startTrick.isMoveValid(players.get(nextPlayer).playerId, played2, players.get(nextPlayer)
                        .getBotCards());
                if (result) {
                    startTrick.makeMove(players.get(nextPlayer).playerId, played2);
                    myGame.currentRound.removeCard(players.get(nextPlayer).playerId, played2);
                }
                System.out.println(name.get(players.get(nextPlayer).playerId) + " played " + played2);
                soFarTrick.add(played2);

                nextPlayer = (nextPlayer + 1) % 4;
                Card played3 = null;
                if (nextPlayer == 3)
                {
                    // you
                    System.out.println("Here are your cards:");
                    System.out.println(players.get(nextPlayer).matrix.keySet());
                    System.out.println("Enter the index number of the card you want to select - indexing starts at zero");
                    index = scan.nextInt();
                    Iterator<Card> cardIter = players.get(nextPlayer).matrix.keySet().iterator();
                    int counter = 0;
                    while (cardIter.hasNext())
                    {
                        Card s = cardIter.next();
                        if (counter == index)
                        {
                            played3 = s;
                            break;
                        }                          
                    }              
                }
                else
                {
                    // bot
                    played3 = players.get(nextPlayer).playCard(played.getSuit(), soFarTrick);
                }
                
                result = startTrick.isMoveValid(players.get(nextPlayer).playerId, played3, players.get(nextPlayer)
                        .getBotCards());
                if (result) {
                    startTrick.makeMove(players.get(nextPlayer).playerId, played3);
                    myGame.currentRound.removeCard(players.get(nextPlayer).playerId, played3);
                }
                System.out.println(name.get(players.get(nextPlayer).playerId) + " played " + played3);
                soFarTrick.add(played3);

                nextPlayer = (nextPlayer + 1) % 4;
                Card played4 = null;
                if (nextPlayer == 3)
                {
                    // you
                    System.out.println("Here are your cards:");
                    System.out.println(players.get(nextPlayer).matrix.keySet());
                    System.out.println("Enter the index number of the card you want to select - indexing starts at zero");
                    index = scan.nextInt();
                    Iterator<Card> cardIter = players.get(nextPlayer).matrix.keySet().iterator();
                    int counter = 0;
                    while (cardIter.hasNext())
                    {
                        Card s = cardIter.next();
                        if (counter == index)
                        {
                            played4 = s;
                            break;
                        }                          
                    }              
                }
                else
                {
                    // bot
                    played4 = players.get(nextPlayer).playCard(played.getSuit(), soFarTrick);
                }
               
                result = startTrick.isMoveValid(players.get(nextPlayer).playerId, played4, players.get(nextPlayer)
                        .getBotCards());
                if (result) {
                    startTrick.makeMove(players.get(nextPlayer).playerId, played4);
                    myGame.currentRound.removeCard(players.get(nextPlayer).playerId, played4);
                }
                System.out.println(name.get(players.get(nextPlayer).playerId) + " played " + played4);
                soFarTrick.add(played4);

                Long loser = startTrick.getLoser();
                int penalty = startTrick.computeScore();
                System.out.println("Loser of the trick is " + name.get(loser) + " gets " + penalty);

                myGame.userIdToGameScore.put(loser, (byte) (myGame.userIdToGameScore.get(loser) + penalty));
                // System.out.println(myGame.playerIdToGameScore.get(loser));

                playerNum = loser.intValue() - 1;

                System.out.println("End of trick " + tricks + "\n");
                
                System.out.println("Score card");

                for (Long id : myGame.userIdToGameScore.keySet()) {
                    System.out.println(name.get(id) + ": " + myGame.userIdToGameScore.get(id));
                }
                System.out.println("\n");
                if (myGame.isGameOver())
                    break;
                tricks++;
            }
            // initializing a new round.
            myGame.currentRound = new Round(myGame.userIdToGameScore);
            // reset states
            roundNum++;

        }

        System.out.println("~~Game Summary~~");

        for (Long id : myGame.userIdToGameScore.keySet()) {
            System.out.println(name.get(id) + ": " + myGame.userIdToGameScore.get(id));
        }

        System.out.println("Winners are " + myGame.findWinner());
        System.out.println("Game over!");
    }

}