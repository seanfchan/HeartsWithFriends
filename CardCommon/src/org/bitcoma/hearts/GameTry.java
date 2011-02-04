package org.bitcoma.hearts;

import java.util.LinkedList;

public class GameTry {

    public GameTry() {
        // nothing
    }

    public static void main(String[] args) {
        LinkedList<Long> playerIds = new LinkedList<Long>();
        for (int i = 1; i <= 4; i++)
            playerIds.add((long) i);

        Game myGame = new Game(playerIds);
        myGame.currentRound.shuffle(4);

        // TODO @madiha to @sean - There is no way in game to allow for Bot
        // players to be added
        // masking players to be bots here.
        LinkedList<BotPlay> players = new LinkedList<BotPlay>();
        players.add(new BotPlay((long) 1,myGame.currentRound.getUserIdToHand().get((long) 1)));
        players.add(new BotPlay((long) 2,myGame.currentRound.getUserIdToHand().get((long) 2)));
        players.add(new BotPlay((long) 3,myGame.currentRound.getUserIdToHand().get((long) 3)));
        players.add(new BotPlay((long) 4,myGame.currentRound.getUserIdToHand().get((long) 4)));

        int i = 0;
        for (BotPlay p : players)
        {
            System.out.println("Bot " + (i+1) + " cards : " + p.matrix.keySet());
            i++;
        }
        
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
        System.out.println("Bot " + (playerNum+1) + " has two of clubs");
        while (!myGame.isGameOver()) {
            int tricks = 1;
            while (!myGame.currentRound.hasRoundEnded()) 
            {
                Trick startTrick = new Trick();
                LinkedList<Card> soFarTrick = new LinkedList<Card>();

                // starting trick
                //System.out.println(playerNum);
                first = players.get(playerNum);
                Card played = first.playCard((byte) 0, soFarTrick);
                System.out.println("Bot " + first.playerId + " played " + played);
                boolean result = startTrick.isMoveValid(first.playerId, played, first.getBotCards());
                if (result)
                {
                    startTrick.makeMove(first.playerId, played);
                    myGame.currentRound.removeCard(first.playerId, played);
                }
                soFarTrick.add(played);

                int nextPlayer = (playerNum + 1) % 4;
                Card played2 = players.get(nextPlayer).playCard(played.getSuit(), soFarTrick);
                System.out.println("Bot " +players.get(nextPlayer).playerId + " played " + played2);
                result = startTrick.isMoveValid(players.get(nextPlayer).playerId, played2, players.get(nextPlayer).getBotCards());
                if (result)
                {
                    startTrick.makeMove(players.get(nextPlayer).playerId, played2);
                    myGame.currentRound.removeCard(players.get(nextPlayer).playerId, played2);
                }
                soFarTrick.add(played2);

                nextPlayer = (nextPlayer + 1) % 4;
                Card played3 = players.get(nextPlayer).playCard(played.getSuit(), soFarTrick);
                System.out.println("Bot " + players.get(nextPlayer).playerId + " played " + played3);
                result = startTrick.isMoveValid(players.get(nextPlayer).playerId, played3, players.get(nextPlayer).getBotCards());
                if (result)
                {
                    startTrick.makeMove(players.get(nextPlayer).playerId, played3);
                    myGame.currentRound.removeCard(players.get(nextPlayer).playerId, played3);
                }
                soFarTrick.add(played3);

                nextPlayer = (nextPlayer + 1) % 4;
                Card played4 = players.get(nextPlayer).playCard(played.getSuit(), soFarTrick);
                System.out.println("Bot  " +players.get(nextPlayer).playerId + " played " + played4);
                result = startTrick.isMoveValid(players.get(nextPlayer).playerId, played4, players.get(nextPlayer).getBotCards());
                if (result)
                {
                    startTrick.makeMove(players.get(nextPlayer).playerId, played4);
                    myGame.currentRound.removeCard(players.get(nextPlayer).playerId, played4);
                }
                soFarTrick.add(played4);
                System.out.println("Loser of the trick is " + startTrick.getLoser());
                playerNum = startTrick.getLoser().intValue() - 1;
                
                System.out.println("End of trick " + tricks);
                // HACK added because round end is not giving the right output..
                if (tricks == 13)
                    break;
                tricks++;
            }
            // initializing a new round.
            myGame.currentRound = new Round(myGame.playerIdToGameScore);
            players.add(new BotPlay((long) 1,myGame.currentRound.getUserIdToHand().get((long) 1)));
            players.add(new BotPlay((long) 2,myGame.currentRound.getUserIdToHand().get((long) 2)));
            players.add(new BotPlay((long) 3,myGame.currentRound.getUserIdToHand().get((long) 3)));
            players.add(new BotPlay((long) 4,myGame.currentRound.getUserIdToHand().get((long) 4)));
            //break; // another testing thing
        }

        System.out.println(myGame.findWinner());
    }

}