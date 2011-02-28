package org.bitcoma.heartserver.netty.task;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bitcoma.hearts.BotPlay;
import org.bitcoma.hearts.Card;
import org.bitcoma.hearts.Round;
import org.bitcoma.hearts.Trick;
import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.model.database.User;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaySingleCardTimeOutTask implements TimerTask {

    // Values to use with TimeOutTaskCreator
    public static final long DELAY = 12;
    public static final TimeUnit UNIT = TimeUnit.SECONDS;

    public static final byte SCORE_PENALTY = 1;

    private static Logger logger = LoggerFactory.getLogger(PlaySingleCardTimeOutTask.class);

    // Player id for whose turn it is.
    private Long playerId;

    // Game this task is associated with. Needed to remove timer task.
    private GameInstance gameInstance;

    public PlaySingleCardTimeOutTask(GameInstance gameInstance, Long playerId) {
        this.playerId = playerId;
        this.gameInstance = gameInstance;
    }

    @Override
    public void run(Timeout timeout) throws Exception {

        logger.info("Running timeout task: GameId {}", gameInstance.getId());

        // Check that this timeout is still associated with this game.
        if (gameInstance.getTimeout() == timeout) {

            // Make sure no one plays cards while we finish this work.
            synchronized (gameInstance) {

                Map<Long, User> userIdToUserMap = gameInstance.getUserIdToUserMap();

                // Remove timeout as it is no longer needed
                gameInstance.setTimeout(null, false);

                // Bot check shouldn't matter, but to be sure
                if (!BotPlay.isBot(playerId)) {

                    Round round = gameInstance.getCurrentRound();

                    // No longer our turn
                    if (playerId != round.getCurrentTurnPlayerId()) {
                        return;
                    }

                    byte infractionCount = gameInstance.getInfractionCount(playerId);

                    if (infractionCount < User.BOT_PENALTY) {
                        logger.info("User: {} takes normal penalty for not playing a card.", playerId);

                        Trick currentTrick = round.getCurrentTrick();
                        // Punish the player for not playing a card
                        List<Card> playerHand = gameInstance.getUserHand(playerId);

                        // Play cards for them based on bot decision
                        Card cardToPlay = BotPlay.playCard(currentTrick, playerHand, round.getAllCardsPlayed());
                        List<Card> cardsToPlay = new LinkedList<Card>();
                        cardsToPlay.add(cardToPlay);
                        gameInstance.playCard(playerId, cardsToPlay, true);

                        // Penalize the player for the infraction
                        gameInstance.addToScore(playerId, SCORE_PENALTY);
                    }
                    // Replace with bot
                    else {
                        logger.info("User: {} takes harsh penalty for not playing single card. Replaced with bot.",
                                playerId);
                        gameInstance.removePlayer(userIdToUserMap.get(playerId));
                    }

                }
            }
        }
    }
}
