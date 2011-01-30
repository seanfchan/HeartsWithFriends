package org.bitcoma.heartserver;

import javolution.util.FastMap;
import org.jboss.netty.channel.Channel;

import org.bitcoma.heartserver.game.*;

public class ServerState {

    // Create a performant map of user ids to Channels to enable push operations
    // to clients.
    public static FastMap<Long, Channel> userIdToChannelMap = new FastMap<Long, Channel>().shared();
    
    //Fast map for games in waiting state
    public static FastMap<Long, GameInstance> waitingGames = new FastMap<Long, GameInstance>().shared();
    
    //Fast map for games in active state
    public static FastMap<Long, GameInstance> activeGames = new FastMap<Long, GameInstance>().shared();

}
