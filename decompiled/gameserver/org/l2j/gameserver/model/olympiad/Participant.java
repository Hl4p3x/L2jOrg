// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.StatsSet;

public final class Participant
{
    private final int objectId;
    private final String name;
    private final int side;
    private final int baseClass;
    private final StatsSet stats;
    public String clanName;
    public int clanId;
    private Player player;
    private boolean disconnected;
    private boolean defaulted;
    
    public Participant(final Player plr, final int olympiadSide) {
        this.disconnected = false;
        this.defaulted = false;
        this.objectId = plr.getObjectId();
        this.player = plr;
        this.name = plr.getName();
        this.side = olympiadSide;
        this.baseClass = plr.getBaseClass();
        this.stats = null;
        this.clanName = ((plr.getClan() != null) ? plr.getClan().getName() : "");
        this.clanId = plr.getClanId();
    }
    
    public Participant(final int objId, final int olympiadSide) {
        this.disconnected = false;
        this.defaulted = false;
        this.objectId = objId;
        this.player = null;
        this.name = "-";
        this.side = olympiadSide;
        this.baseClass = 0;
        this.stats = null;
        this.clanName = "";
        this.clanId = 0;
    }
    
    public final boolean updatePlayer() {
        if (this.player == null || !this.player.isOnline()) {
            this.player = World.getInstance().findPlayer(this.getObjectId());
        }
        return this.player != null;
    }
    
    public final void updateStat(final String statName, final int increment) {
        this.stats.set(statName, Math.max(this.stats.getInt(statName) + increment, 0));
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getClanName() {
        return this.clanName;
    }
    
    public int getClanId() {
        return this.clanId;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void setPlayer(final Player noble) {
        this.player = noble;
    }
    
    public int getObjectId() {
        return this.objectId;
    }
    
    public StatsSet getStats() {
        return this.stats;
    }
    
    public int getSide() {
        return this.side;
    }
    
    public int getBaseClass() {
        return this.baseClass;
    }
    
    public boolean isDisconnected() {
        return this.disconnected;
    }
    
    public void setDisconnected(final boolean val) {
        this.disconnected = val;
    }
    
    public boolean isDefaulted() {
        return this.defaulted;
    }
    
    public void setDefaulted(final boolean val) {
        this.defaulted = val;
    }
}
