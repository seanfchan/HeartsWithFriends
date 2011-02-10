package org.bitcoma.hearts;

import java.util.List;
import java.util.Map;

import org.bitcoma.hearts.model.PassingCardsInfo;

public interface IHeartsGameHandler {

    /**
     * Called when a player plays a card
     * 
     * @param srcId
     *            User id of who played the card
     * @param cardPlayed
     *            Card that was played
     * @param nextPlayerId
     *            User id of the next player to take a turn
     */
    public void handleSingleCardPlayed(Long srcId, Card cardPlayed, Long nextPlayerId);

    /**
     * Called when a player passes a card to another player
     * 
     * @param passingCardInfo
     *            List of source to destination cards being passed
     * @param firstPlayerId
     *            User id of first player from this round
     */
    public void handleCardsPassed(List<PassingCardsInfo> passingCardInfo, Long firstPlayerId);

    /**
     * Called when scores are updated (End of every trick, possible penalties
     * due to timeouts, etc...)
     * 
     * @param userIdToGameScore
     *            Map of user ids to game scores
     * @param userIdToRoundScore
     *            Map of user ids to round scores
     */
    public void handleScoreUpdate(Map<Long, Byte> userIdToGameScore, Map<Long, Byte> userIdToRoundScore);

    /**
     * Called when a trick is finished. DO NOT grab scores from here. Should use
     * handleScoreUpdate.
     * 
     * @param finishedTrick
     *            Trick that has just ended.
     */
    public void handleTrickEnded(Trick finishedTrick);

    /**
     * Called when a round is finished. DO NOT grab scores from here. Should use
     * handleScoreUpdate
     * 
     * @param finishedRound
     *            Round that has just ended.
     */
    public void handleRoundEnded(Round finishedRound);

    /**
     * Called when a round is started. DO NOT grab scores from here. Useful for
     * grabbing hands of each player. And who is first to play
     * 
     * @param finishedRound
     *            Round that has just ended.
     */
    public void handleRoundStarted(Round startedRound);

    /**
     * Called when a game is finished. Scores can be grabbed from here if
     * wanted.
     * 
     * @param finishedGame
     *            Game that just ended.
     */
    public void handleGameEnded(Game finishedGame);
}
