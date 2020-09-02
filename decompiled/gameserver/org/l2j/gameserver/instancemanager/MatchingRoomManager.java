// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.enums.PartyMatchingRoomLevelType;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.gameserver.model.base.ClassId;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.enums.MatchingRoomType;
import java.util.Map;

public class MatchingRoomManager
{
    private static final Map<MatchingRoomType, Map<Integer, MatchingRoom>> _rooms;
    private final AtomicInteger _id;
    private volatile Set<Player> _waitingList;
    
    private MatchingRoomManager() {
        this._id = new AtomicInteger(0);
    }
    
    public void addToWaitingList(final Player player) {
        if (this._waitingList == null) {
            synchronized (this) {
                if (this._waitingList == null) {
                    this._waitingList = (Set<Player>)ConcurrentHashMap.newKeySet(1);
                }
            }
        }
        this._waitingList.add(player);
    }
    
    public void removeFromWaitingList(final Player player) {
        this.getPlayerInWaitingList().remove(player);
    }
    
    public Set<Player> getPlayerInWaitingList() {
        return (this._waitingList == null) ? Collections.emptySet() : this._waitingList;
    }
    
    public List<Player> getPlayerInWaitingList(final int minLevel, final int maxLevel, final List<ClassId> classIds, final String query) {
        if (this._waitingList == null) {
            return Collections.emptyList();
        }
        return this._waitingList.stream().filter(p -> p != null && p.getLevel() >= minLevel && p.getLevel() <= maxLevel).filter(p -> classIds == null || classIds.contains(p.getClassId())).filter(p -> query == null || query.isEmpty() || p.getName().toLowerCase().contains(query)).collect((Collector<? super Object, ?, List<Player>>)Collectors.toList());
    }
    
    public int addMatchingRoom(final MatchingRoom room) {
        final int roomId = this._id.incrementAndGet();
        MatchingRoomManager._rooms.computeIfAbsent(room.getRoomType(), k -> new ConcurrentHashMap()).put(roomId, room);
        return roomId;
    }
    
    public void removeMatchingRoom(final MatchingRoom room) {
        MatchingRoomManager._rooms.getOrDefault(room.getRoomType(), Collections.emptyMap()).remove(room.getId());
    }
    
    public Map<Integer, MatchingRoom> getPartyMathchingRooms() {
        return MatchingRoomManager._rooms.get(MatchingRoomType.PARTY);
    }
    
    public List<MatchingRoom> getPartyMathchingRooms(final int location, final PartyMatchingRoomLevelType type, final int requestorLevel) {
        return MatchingRoomManager._rooms.getOrDefault(MatchingRoomType.PARTY, Collections.emptyMap()).values().stream().filter(room -> location < 0 || room.getLocation() == location).filter(room -> type == PartyMatchingRoomLevelType.ALL || (room.getMinLvl() >= requestorLevel && room.getMaxLvl() <= requestorLevel)).collect((Collector<? super MatchingRoom, ?, List<MatchingRoom>>)Collectors.toList());
    }
    
    public Map<Integer, MatchingRoom> getCCMathchingRooms() {
        return MatchingRoomManager._rooms.get(MatchingRoomType.COMMAND_CHANNEL);
    }
    
    public List<MatchingRoom> getCCMathchingRooms(final int location, final int level) {
        return MatchingRoomManager._rooms.getOrDefault(MatchingRoomType.COMMAND_CHANNEL, Collections.emptyMap()).values().stream().filter(r -> r.getLocation() == location).filter(r -> r.getMinLvl() <= level && r.getMaxLvl() >= level).collect((Collector<? super MatchingRoom, ?, List<MatchingRoom>>)Collectors.toList());
    }
    
    public MatchingRoom getCCMatchingRoom(final int roomId) {
        return MatchingRoomManager._rooms.getOrDefault(MatchingRoomType.COMMAND_CHANNEL, Collections.emptyMap()).get(roomId);
    }
    
    public MatchingRoom getPartyMathchingRoom(final int location, final int level) {
        return MatchingRoomManager._rooms.getOrDefault(MatchingRoomType.PARTY, Collections.emptyMap()).values().stream().filter(r -> r.getLocation() == location).filter(r -> r.getMinLvl() <= level && r.getMaxLvl() >= level).findFirst().orElse(null);
    }
    
    public MatchingRoom getPartyMathchingRoom(final int roomId) {
        return MatchingRoomManager._rooms.getOrDefault(MatchingRoomType.PARTY, Collections.emptyMap()).get(roomId);
    }
    
    public static MatchingRoomManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        _rooms = new ConcurrentHashMap<MatchingRoomType, Map<Integer, MatchingRoom>>(2);
    }
    
    private static class Singleton
    {
        private static final MatchingRoomManager INSTANCE;
        
        static {
            INSTANCE = new MatchingRoomManager();
        }
    }
}
