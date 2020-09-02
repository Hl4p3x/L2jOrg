// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExChooseCostumeItem extends ServerPacket
{
    private final int itemId;
    
    public ExChooseCostumeItem(final int itemId) {
        this.itemId = itemId;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHOOSE_COSTUME_ITEM);
        this.writeInt(this.itemId);
    }
}
