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
        int roundNum = 1;
        

        
        while (!myGame.isGameOver()) {

            System.out.println("Round " + roundNum);
            myGame.currentRound.shuffle(4);
            LinkedList<BotPlay> players = new LinkedList<BotPlay>();
            players.add(new BotPlay((long) 1, myGame.currentRound.getUserIdToHand().get((long) 1)));
            players.add(new BotPlay((long) 2, myGame.currentRound.getUserIdToHand().get((long) 2)));
            players.add(new BotPlay((long) 3, myGame.currentRound.getUserIdToHand().get((long) 3)));
            players.add(new BotPlay((long) 4, myGame.currentRound.getUserIdToHand().get((long) 4)));

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

                // starting trick
                // System.out.println(playerNum);
                first = players.get(playerNum);
                Card played = first.playCard((byte) 0, soFarTrick);
                System.out.println("Bot " + first.playerId + " played " + played);
                boolean result = startTrick.isMoveValid(first.playerId, played, first.getBotCards());
                if (result) {
                    startTrick.makeMove(first.playerId, played);
                    myGame.currentRound.removeCard(first.playerId, played);
                }
                soFarTrick.add(played);

                int nextPlayer = (playerNum + 1) % 4;
                Card played2 = players.get(nextPlayer).playCard(played.getSuit(), soFarTrick);
                System.out.println("Bot " + players.get(nextPlayer).playerId + " played " + played2);
                result = startTrick.isMoveValid(players.get(nextPlayer).playerId, played2, players.get(nextPlayer)
                        .getBotCards());
                if (result) {
                    startTrick.makeMove(players.get(nextPlayer).playerId, played2);
                    myGame.currentRound.removeCard(players.get(nextPlayer).playerId, played2);
                }
                soFarTrick.add(played2);

                nextPlayer = (nextPlayer + 1) % 4;
                Card played3 = players.get(nextPlayer).playCard(played.getSuit(), soFarTrick);
                System.out.println("Bot " + players.get(nextPlayer).playerId + " played " + played3);
                result = startTrick.isMoveValid(players.get(nextPlayer).playerId, played3, players.get(nextPlayer)
                        .getBotCards());
                if (result) {
                    startTrick.makeMove(players.get(nextPlayer).playerId, played3);
                    myGame.currentRound.removeCard(players.get(nextPlayer).playerId, played3);
                }
                soFarTrick.add(played3);

                nextPlayer = (nextPlayer + 1) % 4;
                Card played4 = players.get(nextPlayer).playCard(played.getSuit(), soFarTrick);
                System.out.println("Bot " + players.get(nextPlayer).playerId + " played " + played4);
                result = startTrick.isMoveValid(players.get(nextPlayer).playerId, played4, players.get(nextPlayer)
                        .getBotCards());
                if (result) {
                    startTrick.makeMove(players.get(nextPlayer).playerId, played4);
                    myGame.currentRound.removeCard(players.get(nextPlayer).playerId, played4);
                }
                soFarTrick.add(played4);

                Long loser = startTrick.getLoser();
                int penalty = startTrick.computeScore();
                System.out.println("Loser of the trick is " + loser + " gets " + penalty);

                myGame.userIdToGameScore.put(loser, (byte) (myGame.userIdToGameScore.get(loser) + penalty));
                // System.out.println(myGame.playerIdToGameScore.get(loser));

                playerNum = loser.intValue() - 1;

                System.out.println("End of trick " + tricks + "\n");
                
                System.out.println("Score card");

                for (Long id : myGame.userIdToGameScore.keySet()) {
                    System.out.println("Player " + id + ": " + myGame.userIdToGameScore.get(id));
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
            System.out.println("Player " + id + ": " + myGame.userIdToGameScore.get(id));
        }

        System.out.println("Winners are " + myGame.findWinner());
        System.out.println("Game over!");
    }

}