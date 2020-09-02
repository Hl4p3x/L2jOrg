// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExAdenaInvenCount extends ServerPacket
{
    private final Player player;
    
    public ExAdenaInvenCount(final Player cha) {
        this.player = cha;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ADENA_INVEN_COUNT);
        this.writeLong(this.player.getAdena());
        this.writeShort(this.player.getInventory().getSize());
    }
}
