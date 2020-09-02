// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("pledge_applicant")
public class PledgeApplicantData
{
    @Column("charId")
    private int playerId;
    private int clanId;
    private int karma;
    private String message;
    @NonUpdatable
    @Column("base_class")
    private int classId;
    @NonUpdatable
    @Column("level")
    private int playerLevel;
    @NonUpdatable
    @Column("char_name")
    private String playerName;
    
    public PledgeApplicantData() {
    }
    
    public PledgeApplicantData(final int playerId, final String playerName, final int playerLevel, final int karma, final int requestClanId, final String message) {
        this.playerId = playerId;
        this.clanId = requestClanId;
        this.playerName = playerName;
        this.playerLevel = playerLevel;
        this.karma = karma;
        this.message = message;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public int getRequestClanId() {
        return this.clanId;
    }
    
    public String getPlayerName() {
        if (this.isOnline() && !this.getPlayerInstance().getName().equalsIgnoreCase(this.playerName)) {
            this.playerName = this.getPlayerInstance().getName();
        }
        return this.playerName;
    }
    
    public int getPlayerLvl() {
        if (this.isOnline() && this.getPlayerInstance().getLevel() != this.playerLevel) {
            this.playerLevel = this.getPlayerInstance().getLevel();
        }
        return this.playerLevel;
    }
    
    public int getClassId() {
        if (this.isOnline() && this.getPlayerInstance().getBaseClass() != this.classId) {
            this.classId = this.getPlayerInstance().getClassId().getId();
        }
        return this.classId;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public int getKarma() {
        return this.karma;
    }
    
    public Player getPlayerInstance() {
        return World.getInstance().findPlayer(this.playerId);
    }
    
    public boolean isOnline() {
        return this.getPlayerInstance() != null && this.getPlayerInstance().isOnline();
    }
}
