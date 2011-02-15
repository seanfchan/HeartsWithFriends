package org.bitcoma.heartserver;

import javolution.util.FastMap;

import org.bitcoma.hearts.BotPlay;
import org.bitcoma.heartserver.game.GameInstance;
import org.jboss.netty.channel.Channel;

public class ServerState {

    // Create a performant map of user ids to Channels to enable push operations
    // to clients.
    public static FastMap<Long, Channel> userIdToChannelMap = new FastMap<Long, Channel>().shared();

    // Fast map for games in waiting state - Game Id To Game Instance
    public static FastMap<Long, GameInstance> waitingGames = new FastMap<Long, GameInstance>().shared();

    // Fast map for games in active state - Game Id to Game Instance
    public static FastMap<Long, GameInstance> activeGames = new FastMap<Long, GameInstance>().shared();

    public static void sendToClient(Long userId, Object msg) {
        if (userId == null || BotPlay.isBot(userId))
            return;

        // TODO: @Jon figure out how to handle this. Should not happen
        if (!userIdToChannelMap.containsKey(userId))
            return;

        synchronized (userId) {
            userIdToChannelMap.get(userId).write(msg);
        }
    }
}
