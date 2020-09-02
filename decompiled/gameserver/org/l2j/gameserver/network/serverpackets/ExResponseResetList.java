// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExResponseResetList extends ServerPacket
{
    private final Player _activeChar;
    
    public ExResponseResetList(final Player activeChar) {
        this._activeChar = activeChar;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_RESET_LIST);
        this.writeLong(this._activeChar.getAdena());
        this.writeLong(this._activeChar.getBeautyTickets());
        this.writeInt((int)this._activeChar.getAppearance().getHairStyle());
        this.writeInt((int)this._activeChar.getAppearance().getHairColor());
        this.writeInt((int)this._activeChar.getAppearance().getFace());
    }
}
