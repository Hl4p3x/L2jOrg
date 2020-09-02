// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.clan.entry;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;

public class PledgeWaitingInfo
{
    private final int _karma;
    private int _playerId;
    private int _playerClassId;
    private int _playerLvl;
    private String _playerName;
    
    public PledgeWaitingInfo(final int playerId, final int playerLvl, final int karma, final int classId, final String playerName) {
        this._playerId = playerId;
        this._playerClassId = classId;
        this._playerLvl = playerLvl;
        this._karma = karma;
        this._playerName = playerName;
    }
    
    public int getPlayerId() {
        return this._playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this._playerId = playerId;
    }
    
    public int getPlayerClassId() {
        if (this.isOnline() && this.getPlayerInstance().getBaseClass() != this._playerClassId) {
            this._playerClassId = this.getPlayerInstance().getClassId().getId();
        }
        return this._playerClassId;
    }
    
    public int getPlayerLvl() {
        if (this.isOnline() && this.getPlayerInstance().getLevel() != this._playerLvl) {
            this._playerLvl = this.getPlayerInstance().getLevel();
        }
        return this._playerLvl;
    }
    
    public int getKarma() {
        return this._karma;
    }
    
    public String getPlayerName() {
        if (this.isOnline() && !this.getPlayerInstance().getName().equalsIgnoreCase(this._playerName)) {
            this._playerName = this.getPlayerInstance().getName();
        }
        return this._playerName;
    }
    
    public Player getPlayerInstance() {
        return World.getInstance().findPlayer(this._playerId);
    }
    
    public boolean isOnline() {
        return this.getPlayerInstance() != null && this.getPlayerInstance().isOnlineInt() > 0;
    }
}
