// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.olympiad.Participant;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExOlympiadUserInfo extends ServerPacket
{
    private final Player _player;
    private Participant _par;
    private int _curHp;
    private int _maxHp;
    private int _curCp;
    private int _maxCp;
    
    public ExOlympiadUserInfo(final Player player) {
        this._par = null;
        this._player = player;
        if (this._player != null) {
            this._curHp = (int)this._player.getCurrentHp();
            this._maxHp = this._player.getMaxHp();
            this._curCp = (int)this._player.getCurrentCp();
            this._maxCp = this._player.getMaxCp();
        }
        else {
            this._curHp = 0;
            this._maxHp = 100;
            this._curCp = 0;
            this._maxCp = 100;
        }
    }
    
    public ExOlympiadUserInfo(final Participant par) {
        this._par = null;
        this._par = par;
        this._player = par.getPlayer();
        if (this._player != null) {
            this._curHp = (int)this._player.getCurrentHp();
            this._maxHp = this._player.getMaxHp();
            this._curCp = (int)this._player.getCurrentCp();
            this._maxCp = this._player.getMaxCp();
        }
        else {
            this._curHp = 0;
            this._maxHp = 100;
            this._curCp = 0;
            this._maxCp = 100;
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_OLYMPIAD_USER_INFO);
        if (this._player != null) {
            this.writeByte((byte)this._player.getOlympiadSide());
            this.writeInt(this._player.getObjectId());
            this.writeString((CharSequence)this._player.getName());
            this.writeInt(this._player.getClassId().getId());
        }
        else {
            this.writeByte((byte)this._par.getSide());
            this.writeInt(this._par.getObjectId());
            this.writeString((CharSequence)this._par.getName());
            this.writeInt(this._par.getBaseClass());
        }
        this.writeInt(this._curHp);
        this.writeInt(this._maxHp);
        this.writeInt(this._curCp);
        this.writeInt(this._maxCp);
    }
}
