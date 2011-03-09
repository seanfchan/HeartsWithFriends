package org.bitcoma.heartserver;

import java.util.concurrent.atomic.AtomicLong;

import javolution.util.FastMap;

import org.bitcoma.hearts.BotPlay;
import org.bitcoma.hearts.model.transfered.OneMessageProtos.OneMessage;
import org.bitcoma.hearts.utils.ReqRespMetrics;
import org.bitcoma.heartserver.game.GameInstance;
import org.bitcoma.heartserver.netty.handler.HeartsServerHandler;
import org.jboss.netty.channel.Channel;

public class ServerState {

    public static AtomicLong numActiveConnections = new AtomicLong(0);
    public static AtomicLong numLoggedInUsers = new AtomicLong(0);

    public static FastMap<Integer, ReqRespMetrics> reqRespMetricsMap = new FastMap<Integer, ReqRespMetrics>(15);

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

    static {
        // Initialize the reqRespMetricsMap with data for each kind of pair
        reqRespMetricsMap.put(OneMessage.Type.LOGIN_REQUEST.getNumber(), new ReqRespMetrics());
        reqRespMetricsMap.put(OneMessage.Type.SIGNUP_REQUEST.getNumber(), new ReqRespMetrics());
        reqRespMetricsMap.put(OneMessage.Type.JOIN_GAME_REQUEST.getNumber(), new ReqRespMetrics());
        reqRespMetricsMap.put(OneMessage.Type.LEAVE_GAME_REQUEST.getNumber(), new ReqRespMetrics());
        reqRespMetricsMap.put(OneMessage.Type.START_GAME_REQUEST.getNumber(), new ReqRespMetrics());
        reqRespMetricsMap.put(OneMessage.Type.PLAY_CARD_REQUEST.getNumber(), new ReqRespMetrics());
        reqRespMetricsMap.put(HeartsServerHandler.UNKNOWN_REQUEST, new ReqRespMetrics());
    }
}
