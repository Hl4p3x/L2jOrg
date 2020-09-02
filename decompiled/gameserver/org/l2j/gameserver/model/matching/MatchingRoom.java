// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.matching;

import org.l2j.gameserver.enums.MatchingMemberType;
import org.l2j.gameserver.enums.MatchingRoomType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.MapRegionManager;
import java.util.Iterator;
import org.l2j.gameserver.enums.UserInfoType;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Set;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public abstract class MatchingRoom implements IIdentifiable
{
    private final int _id;
    private String _title;
    private int _loot;
    private int _minLvl;
    private int _maxLvl;
    private int _maxCount;
    private volatile Set<Player> _members;
    private Player _leader;
    
    public MatchingRoom(final String title, final int loot, final int minlvl, final int maxlvl, final int maxmem, final Player leader) {
        this._id = MatchingRoomManager.getInstance().addMatchingRoom(this);
        this._title = title;
        this._loot = loot;
        this._minLvl = minlvl;
        this._maxLvl = maxlvl;
        this._maxCount = maxmem;
        this.addMember(this._leader = leader);
        this.onRoomCreation(leader);
    }
    
    public Set<Player> getMembers() {
        if (this._members == null) {
            synchronized (this) {
                if (this._members == null) {
                    this._members = (Set<Player>)ConcurrentHashMap.newKeySet(1);
                }
            }
        }
        return this._members;
    }
    
    public void addMember(final Player player) {
        if (player.getLevel() < this._minLvl || player.getLevel() > this._maxLvl || (this._members != null && this._members.size() >= this._maxCount)) {
            this.notifyInvalidCondition(player);
            return;
        }
        this.getMembers().add(player);
        MatchingRoomManager.getInstance().removeFromWaitingList(player);
        this.notifyNewMember(player);
        player.setMatchingRoom(this);
        player.broadcastUserInfo(UserInfoType.CLAN);
    }
    
    public void deleteMember(final Player player, final boolean kicked) {
        boolean leaderChanged = false;
        if (player == this._leader) {
            if (this.getMembers().isEmpty()) {
                MatchingRoomManager.getInstance().removeMatchingRoom(this);
            }
            else {
                final Iterator<Player> iter = this.getMembers().iterator();
                if (iter.hasNext()) {
                    this._leader = iter.next();
                    iter.remove();
                    leaderChanged = true;
                }
            }
        }
        else {
            this.getMembers().remove(player);
        }
        player.setMatchingRoom(null);
        player.broadcastUserInfo(UserInfoType.CLAN);
        MatchingRoomManager.getInstance().addToWaitingList(player);
        this.notifyRemovedMember(player, kicked, leaderChanged);
    }
    
    @Override
    public int getId() {
        return this._id;
    }
    
    public int getLootType() {
        return this._loot;
    }
    
    public void setLootType(final int loot) {
        this._loot = loot;
    }
    
    public int getMinLvl() {
        return this._minLvl;
    }
    
    public void setMinLvl(final int minLvl) {
        this._minLvl = minLvl;
    }
    
    public int getMaxLvl() {
        return this._maxLvl;
    }
    
    public void setMaxLvl(final int maxLvl) {
        this._maxLvl = maxLvl;
    }
    
    public int getLocation() {
        return MapRegionManager.getInstance().getBBs(this._leader.getLocation());
    }
    
    public int getMembersCount() {
        return (this._members == null) ? 0 : this._members.size();
    }
    
    public int getMaxMembers() {
        return this._maxCount;
    }
    
    public void setMaxMembers(final int maxCount) {
        this._maxCount = maxCount;
    }
    
    public String getTitle() {
        return this._title;
    }
    
    public void setTitle(final String title) {
        this._title = title;
    }
    
    public Player getLeader() {
        return this._leader;
    }
    
    public boolean isLeader(final Player player) {
        return player == this._leader;
    }
    
    protected abstract void onRoomCreation(final Player player);
    
    protected abstract void notifyInvalidCondition(final Player player);
    
    protected abstract void notifyNewMember(final Player player);
    
    protected abstract void notifyRemovedMember(final Player player, final boolean kicked, final boolean leaderChanged);
    
    public abstract void disbandRoom();
    
    public abstract MatchingRoomType getRoomType();
    
    public abstract MatchingMemberType getMemberType(final Player player);
}
