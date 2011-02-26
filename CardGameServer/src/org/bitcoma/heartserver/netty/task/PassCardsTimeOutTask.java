package org.bitcoma.heartserver.netty.task;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bitcoma.hearts.BotPlay;
import org.bitcoma.hearts.Card;
import org.bitcoma.hearts.Round;
import org.bitcoma.hearts.model.PassingCardsInfo;
import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.model.database.User;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassCardsTimeOutTask implements TimerTask {

    // Values to use with TimeOutTaskCreator
    public static final long DELAY = 30;
    public static final TimeUnit UNIT = TimeUnit.SECONDS;

    public static final byte SCORE_PENALTY = 3;

    private static Logger logger = LoggerFactory.getLogger(PassCardsTimeOutTask.class);

    // Round that these cards are needed for.
    private Round round;

    // Game this task is associated with. Needed to remove timer task.
    private GameInstance gameInstance;

    public PassCardsTimeOutTask(GameInstance gameInstance, Round round) {
        this.round = round;
        this.gameInstance = gameInstance;
    }

    @Override
    public void run(Timeout timeout) throws Exception {

        logger.info("Running timeout task: GameId {}", gameInstance.getId());

        // Check that this timeout is still associated with this game.
        if (gameInstance.getTimeout() == this) {

            // Remove timeout as it is no longer needed
            gameInstance.setTimeout(null, false);

            // Make sure no one plays cards while we finish this work.
            synchronized (gameInstance) {
                Map<Long, User> userIdToUserMap = gameInstance.getUserIdToUserMap();

                // Remove players that have passed cards
                Set<Long> idSet = new HashSet<Long>(userIdToUserMap.keySet());
                for (PassingCardsInfo pci : round.getPassingCardsInfo()) {
                    idSet.remove(pci.srcId);
                }

                // Punish those that have not passed cards
                for (Long id : idSet) {

                    // Bot check shouldn't matter, but to be sure
                    if (!BotPlay.isBot(id)) {

                        byte infractionCount = gameInstance.getInfractionCount(id);

                        if (infractionCount < User.BOT_PENALTY) {
                            logger.info("User: {} takes normal penalty for not passing cards.", id);

                            // Play cards for them based on bot decision
                            Collection<Card> cards = gameInstance.getUserHand(id);
                            List<Card> cardsToPlay = BotPlay.removeThree(cards);
                            gameInstance.playCard(id, cardsToPlay, true);

                            // Penalize the player for the infraction
                            gameInstance.addToScore(id, SCORE_PENALTY);
                        }
                        // Replace with bot - THIS SHOULD not happen at all
                        else {
                            logger.info("User: {} takes harsh penalty for not passing cards. Replaced with bot.", id);
                            gameInstance.removePlayer(userIdToUserMap.get(id));
                        }
                    }
                }
            }
        }
    }
}
