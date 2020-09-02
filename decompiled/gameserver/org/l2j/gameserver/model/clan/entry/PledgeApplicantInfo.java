// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.clan.entry;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;

public class PledgeApplicantInfo
{
    private final int _playerId;
    private final int _requestClanId;
    private final int _karma;
    private final String _message;
    private String _playerName;
    private int _playerLvl;
    private int _classId;
    
    public PledgeApplicantInfo(final int playerId, final String playerName, final int playerLevel, final int karma, final int requestClanId, final String message) {
        this._playerId = playerId;
        this._requestClanId = requestClanId;
        this._playerName = playerName;
        this._playerLvl = playerLevel;
        this._karma = karma;
        this._message = message;
    }
    
    public int getPlayerId() {
        return this._playerId;
    }
    
    public int getRequestClanId() {
        return this._requestClanId;
    }
    
    public String getPlayerName() {
        if (this.isOnline() && !this.getPlayerInstance().getName().equalsIgnoreCase(this._playerName)) {
            this._playerName = this.getPlayerInstance().getName();
        }
        return this._playerName;
    }
    
    public int getPlayerLvl() {
        if (this.isOnline() && this.getPlayerInstance().getLevel() != this._playerLvl) {
            this._playerLvl = this.getPlayerInstance().getLevel();
        }
        return this._playerLvl;
    }
    
    public int getClassId() {
        if (this.isOnline() && this.getPlayerInstance().getBaseClass() != this._classId) {
            this._classId = this.getPlayerInstance().getClassId().getId();
        }
        return this._classId;
    }
    
    public String getMessage() {
        return this._message;
    }
    
    public int getKarma() {
        return this._karma;
    }
    
    public Player getPlayerInstance() {
        return World.getInstance().findPlayer(this._playerId);
    }
    
    public boolean isOnline() {
        return this.getPlayerInstance() != null && this.getPlayerInstance().isOnlineInt() > 0;
    }
}
