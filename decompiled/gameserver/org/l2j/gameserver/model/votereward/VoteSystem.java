// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.votereward;

import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Collection;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPool;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.slf4j.Logger;

public abstract class VoteSystem implements Runnable
{
    protected static final Logger LOGGER;
    private static List<VoteSystem> voteSystems;
    private final int votesDiff;
    private final boolean allowReport;
    private final int boxes;
    private final Map<Integer, Integer> rewards;
    private final Map<String, Integer> playerIps;
    private int lastVotes;
    
    public VoteSystem(final int votesDiff, final boolean allowReport, final int boxes, final Map<Integer, Integer> rewards, final int checkMins) {
        this.playerIps = new HashMap<String, Integer>();
        this.lastVotes = 0;
        this.votesDiff = votesDiff;
        this.allowReport = allowReport;
        this.boxes = boxes;
        this.rewards = rewards;
        ThreadPool.scheduleAtFixedRate((Runnable)this, (long)(checkMins * 1000 * 60), (long)(checkMins * 1000 * 60));
    }
    
    public static void initialize() {
        if (Config.ALLOW_NETWORK_VOTE_REWARD || Config.ALLOW_TOPZONE_VOTE_REWARD || Config.ALLOW_HOPZONE_VOTE_REWARD) {
            VoteSystem.LOGGER.info("VoteSystem: Initialized.");
            if (Config.ALLOW_NETWORK_VOTE_REWARD) {
                VoteSystem.voteSystems.add(new Network(Config.NETWORK_VOTES_DIFFERENCE, Config.ALLOW_NETWORK_GAME_SERVER_REPORT, Config.NETWORK_DUALBOXES_ALLOWED, Config.NETWORK_REWARD, Config.NETWORK_REWARD_CHECK_TIME));
                VoteSystem.LOGGER.info("VoteSystem: Network votes enabled.");
            }
            else {
                VoteSystem.LOGGER.info("VoteSystem: Network votes disabled.");
            }
            if (Config.ALLOW_TOPZONE_VOTE_REWARD) {
                VoteSystem.voteSystems.add(new Topzone(Config.TOPZONE_VOTES_DIFFERENCE, Config.ALLOW_TOPZONE_GAME_SERVER_REPORT, Config.TOPZONE_DUALBOXES_ALLOWED, Config.TOPZONE_REWARD, Config.TOPZONE_REWARD_CHECK_TIME));
                VoteSystem.LOGGER.info("VoteSystem: Topzone votes enabled.");
            }
            else {
                VoteSystem.LOGGER.info("VoteSystem: Topzone votes disabled.");
            }
            if (Config.ALLOW_HOPZONE_VOTE_REWARD) {
                VoteSystem.voteSystems.add(new Hopzone(Config.HOPZONE_VOTES_DIFFERENCE, Config.ALLOW_HOPZONE_GAME_SERVER_REPORT, Config.HOPZONE_DUALBOXES_ALLOWED, Config.HOPZONE_REWARD, Config.HOPZONE_REWARD_CHECK_TIME));
                VoteSystem.LOGGER.info("VoteSystem: Hopzone votes enabled.");
            }
            else {
                VoteSystem.LOGGER.info("VoteSystem: Hopzone votes disabled.");
            }
        }
        else {
            VoteSystem.LOGGER.info("VoteSystem: Disabled.");
        }
    }
    
    private static void announce(final String msg) {
        final CreatureSay cs = new CreatureSay(0, ChatType.CRITICAL_ANNOUNCE, "", msg);
        Broadcast.toAllOnlinePlayers(cs);
    }
    
    protected void reward() {
        final int currentVotes = this.getVotes();
        if (currentVotes == -1) {
            VoteSystem.LOGGER.info("VoteSystem: There was a problem on getting server votes.");
            return;
        }
        if (this.lastVotes == 0) {
            this.lastVotes = currentVotes;
            announce(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getSiteName(), currentVotes));
            announce(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getSiteName(), this.lastVotes + this.votesDiff - currentVotes));
            if (this.allowReport) {
                VoteSystem.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getSiteName(), currentVotes));
                VoteSystem.LOGGER.info(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.lastVotes + this.votesDiff - currentVotes));
            }
            return;
        }
        if (currentVotes >= this.lastVotes + this.votesDiff) {
            final Collection<Player> pls = World.getInstance().getPlayers();
            if (this.allowReport) {
                VoteSystem.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getSiteName(), currentVotes));
                VoteSystem.LOGGER.info(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, currentVotes + this.votesDiff - currentVotes));
            }
            announce(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getSiteName()));
            announce(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getSiteName(), currentVotes));
            announce(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getSiteName(), this.votesDiff));
            for (final Player p : pls) {
                if (p.getClient() == null) {
                    continue;
                }
                boolean canReward = false;
                final String pIp = p.getClient().getHostAddress();
                if (this.playerIps.containsKey(pIp)) {
                    final int count = this.playerIps.get(pIp);
                    if (count < this.boxes) {
                        this.playerIps.remove(pIp);
                        this.playerIps.put(pIp, count + 1);
                        canReward = true;
                    }
                }
                else {
                    canReward = true;
                    this.playerIps.put(pIp, 1);
                }
                if (canReward) {
                    for (final int i : this.rewards.keySet()) {
                        p.addItem("Vote reward.", i, this.rewards.get(i), p, true);
                    }
                }
                else {
                    p.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.boxes));
                }
            }
            this.playerIps.clear();
            this.lastVotes = currentVotes;
        }
        else {
            if (this.allowReport) {
                VoteSystem.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getSiteName(), currentVotes));
                VoteSystem.LOGGER.info(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.lastVotes + this.votesDiff - currentVotes));
            }
            announce(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getSiteName(), currentVotes));
            announce(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getSiteName(), this.lastVotes + this.votesDiff - currentVotes));
        }
    }
    
    public abstract int getVotes();
    
    public abstract String getSiteName();
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)VoteSystem.class);
        VoteSystem.voteSystems = new ArrayList<VoteSystem>();
    }
}
