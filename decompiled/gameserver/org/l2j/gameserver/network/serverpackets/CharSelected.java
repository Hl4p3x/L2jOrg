// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class CharSelected extends ServerPacket
{
    private final Player _activeChar;
    private final int _sessionId;
    
    public CharSelected(final Player cha, final int sessionId) {
        this._activeChar = cha;
        this._sessionId = sessionId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CHARACTER_SELECTED);
        this.writeString((CharSequence)this._activeChar.getName());
        this.writeInt(this._activeChar.getObjectId());
        this.writeString((CharSequence)this._activeChar.getTitle());
        this.writeInt(this._sessionId);
        this.writeInt(this._activeChar.getClanId());
        this.writeInt(0);
        this.writeInt((int)(this._activeChar.getAppearance().isFemale() ? 1 : 0));
        this.writeInt(this._activeChar.getRace().ordinal());
        this.writeInt(this._activeChar.getClassId().getId());
        this.writeInt(1);
        this.writeInt(this._activeChar.getX());
        this.writeInt(this._activeChar.getY());
        this.writeInt(this._activeChar.getZ());
        this.writeDouble(this._activeChar.getCurrentHp());
        this.writeDouble(this._activeChar.getCurrentMp());
        this.writeLong(this._activeChar.getSp());
        this.writeLong(this._activeChar.getExp());
        this.writeInt(this._activeChar.getLevel());
        this.writeInt(this._activeChar.getReputation());
        this.writeInt(this._activeChar.getPkKills());
        this.writeInt(WorldTimeController.getInstance().getGameTime() % 1440);
        this.writeInt(0);
        this.writeInt(this._activeChar.getClassId().getId());
        this.writeBytes(new byte[16]);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
        this.writeBytes(new byte[28]);
        this.writeInt(0);
    }
}
