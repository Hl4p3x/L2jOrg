// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import java.util.Iterator;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;

public final class AntiFeedManager
{
    public static final int GAME_ID = 0;
    public static final int OLYMPIAD_ID = 1;
    public static final int TVT_ID = 2;
    public static final int L2EVENT_ID = 3;
    private final Map<Integer, Long> _lastDeathTimes;
    private final Map<Integer, Map<Integer, AtomicInteger>> _eventIPs;
    
    private AntiFeedManager() {
        this._lastDeathTimes = new ConcurrentHashMap<Integer, Long>();
        this._eventIPs = new ConcurrentHashMap<Integer, Map<Integer, AtomicInteger>>();
    }
    
    public void setLastDeathTime(final int objectId) {
        this._lastDeathTimes.put(objectId, System.currentTimeMillis());
    }
    
    public boolean check(final Creature attacker, final Creature target) {
        if (!Config.ANTIFEED_ENABLE) {
            return true;
        }
        if (target == null) {
            return false;
        }
        final Player targetPlayer = target.getActingPlayer();
        if (targetPlayer == null) {
            return false;
        }
        if (targetPlayer.getClient().isDetached()) {
            return false;
        }
        if (Config.ANTIFEED_INTERVAL > 0 && this._lastDeathTimes.containsKey(targetPlayer.getObjectId()) && System.currentTimeMillis() - this._lastDeathTimes.get(targetPlayer.getObjectId()) < Config.ANTIFEED_INTERVAL) {
            return false;
        }
        if (!Config.ANTIFEED_DUALBOX || attacker == null) {
            return true;
        }
        final Player attackerPlayer = attacker.getActingPlayer();
        if (attackerPlayer == null) {
            return false;
        }
        final GameClient targetClient = targetPlayer.getClient();
        final GameClient attackerClient = attackerPlayer.getClient();
        if (targetClient == null || attackerClient == null || targetClient.isDetached() || attackerClient.isDetached()) {
            return !Config.ANTIFEED_DISCONNECTED_AS_DUALBOX;
        }
        return !targetClient.getHostAddress().equals(attackerClient.getHostAddress());
    }
    
    public void clear() {
        this._lastDeathTimes.clear();
    }
    
    public void registerEvent(final int eventId) {
        this._eventIPs.putIfAbsent(eventId, new ConcurrentHashMap<Integer, AtomicInteger>());
    }
    
    public boolean tryAddPlayer(final int eventId, final Player player, final int max) {
        return this.tryAddClient(eventId, player.getClient(), max);
    }
    
    public boolean tryAddClient(final int eventId, final GameClient client, final int max) {
        if (client == null) {
            return false;
        }
        final Map<Integer, AtomicInteger> event = this._eventIPs.get(eventId);
        if (event == null) {
            return false;
        }
        final Integer addrHash = client.getHostAddress().hashCode();
        final AtomicInteger connectionCount = event.computeIfAbsent(addrHash, k -> new AtomicInteger());
        if (!Config.DUALBOX_COUNT_OFFLINE_TRADERS) {
            final String address = client.getHostAddress();
            for (final Player player : World.getInstance().getPlayers()) {
                if ((player.getClient() == null || player.getClient().isDetached()) && player.getIPAddress().equals(address)) {
                    connectionCount.decrementAndGet();
                }
            }
        }
        if (connectionCount.get() + 1 <= max + Config.DUALBOX_CHECK_WHITELIST.getOrDefault(addrHash, 0)) {
            connectionCount.incrementAndGet();
            return true;
        }
        return false;
    }
    
    public boolean removePlayer(final int eventId, final Player player) {
        return this.removeClient(eventId, player.getClient());
    }
    
    public boolean removeClient(final int eventId, final GameClient client) {
        if (client == null) {
            return false;
        }
        final Map<Integer, AtomicInteger> event = this._eventIPs.get(eventId);
        if (event == null) {
            return false;
        }
        final Integer addrHash = client.getHostAddress().hashCode();
        return event.computeIfPresent(addrHash, (k, v) -> {
            if (v == null || v.decrementAndGet() == 0) {
                return null;
            }
            else {
                return v;
            }
        }) != null;
    }
    
    public void onDisconnect(final GameClient client) {
        if (client == null || client.getHostAddress() == null || client.getPlayer() == null) {
            return;
        }
        this._eventIPs.forEach((k, v) -> this.removeClient(k, client));
    }
    
    public void clear(final int eventId) {
        final Map<Integer, AtomicInteger> event = this._eventIPs.get(eventId);
        if (event != null) {
            event.clear();
        }
    }
    
    public int getLimit(final Player player, final int max) {
        return this.getLimit(player.getClient(), max);
    }
    
    public int getLimit(final GameClient client, final int max) {
        if (client == null) {
            return max;
        }
        final Integer addrHash = client.getHostAddress().hashCode();
        int limit = max;
        if (Config.DUALBOX_CHECK_WHITELIST.containsKey(addrHash)) {
            limit += Config.DUALBOX_CHECK_WHITELIST.get(addrHash);
        }
        return limit;
    }
    
    public static AntiFeedManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static class Singleton
    {
        private static final AntiFeedManager INSTANCE;
        
        static {
            INSTANCE = new AntiFeedManager();
        }
    }
}
