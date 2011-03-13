package org.bitcoma.hearts;

import java.util.Collection;

public class TestUtils {

    public static boolean areAllPlayersPresent(Collection<Long> actualPlayerIds, Collection<Long> checkedPlayerIds) {

        if (actualPlayerIds.size() != checkedPlayerIds.size()) {
            return false;
        }

        return actualPlayerIds.containsAll(checkedPlayerIds);
    }

    public static boolean areAllCardsPresent(Collection<Card> allCardsPlayed, int numPlayers) {
        final int NUM_PLAYERS = 4;
        final int NUM_CARDS = 52;

        // TODO: @someone update Round to return a deck based on number of
        // players so we can use it here to check.
        if (numPlayers != NUM_PLAYERS) {
            return false;
        }

        if (allCardsPlayed.size() != NUM_CARDS) {
            return false;
        }

        for (int i = 0; i < NUM_CARDS; ++i) {
            if (!allCardsPlayed.contains(new Card((byte) i))) {
                return false;
            }
        }

        return true;
    }
}
