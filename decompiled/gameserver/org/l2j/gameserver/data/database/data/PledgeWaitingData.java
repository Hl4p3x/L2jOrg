// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("pledge_waiting_list")
public class PledgeWaitingData
{
    @Column("char_id")
    private int playerId;
    private int karma;
    @NonUpdatable
    @Column("base_class")
    private int classId;
    @NonUpdatable
    private int level;
    @NonUpdatable
    @Column("char_name")
    private String name;
    
    public PledgeWaitingData() {
    }
    
    public PledgeWaitingData(final int playerId, final int playerLvl, final int karma, final int classId, final String playerName) {
        this.playerId = playerId;
        this.classId = classId;
        this.level = playerLvl;
        this.karma = karma;
        this.name = playerName;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public int getPlayerClassId() {
        if (this.isOnline() && this.getPlayerInstance().getBaseClass() != this.classId) {
            this.classId = this.getPlayerInstance().getClassId().getId();
        }
        return this.classId;
    }
    
    public int getPlayerLvl() {
        if (this.isOnline() && this.getPlayerInstance().getLevel() != this.level) {
            this.level = this.getPlayerInstance().getLevel();
        }
        return this.level;
    }
    
    public int getKarma() {
        return this.karma;
    }
    
    public String getPlayerName() {
        if (this.isOnline() && !this.getPlayerInstance().getName().equalsIgnoreCase(this.name)) {
            this.name = this.getPlayerInstance().getName();
        }
        return this.name;
    }
    
    public Player getPlayerInstance() {
        return World.getInstance().findPlayer(this.playerId);
    }
    
    public boolean isOnline() {
        return this.getPlayerInstance() != null && this.getPlayerInstance().isOnline();
    }
}
