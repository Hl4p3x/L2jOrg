// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExDuelUpdateUserInfo extends ServerPacket
{
    private final Player _activeChar;
    
    public ExDuelUpdateUserInfo(final Player cha) {
        this._activeChar = cha;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_DUEL_UPDATE_USER_INFO);
        this.writeString((CharSequence)this._activeChar.getName());
        this.writeInt(this._activeChar.getObjectId());
        this.writeInt(this._activeChar.getClassId().getId());
        this.writeInt(this._activeChar.getLevel());
        this.writeInt((int)this._activeChar.getCurrentHp());
        this.writeInt(this._activeChar.getMaxHp());
        this.writeInt((int)this._activeChar.getCurrentMp());
        this.writeInt(this._activeChar.getMaxMp());
        this.writeInt((int)this._activeChar.getCurrentCp());
        this.writeInt(this._activeChar.getMaxCp());
    }
}
