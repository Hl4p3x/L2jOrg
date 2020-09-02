// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExUserInfoCubic extends ServerPacket
{
    private final Player _activeChar;
    
    public ExUserInfoCubic(final Player cha) {
        this._activeChar = cha;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_USER_INFO_CUBIC);
        this.writeInt(this._activeChar.getObjectId());
        this.writeShort((short)this._activeChar.getCubics().size());
        this._activeChar.getCubics().keySet().forEach(key -> this.writeShort(key.shortValue()));
        this.writeInt(this._activeChar.getAgathionId());
    }
}
