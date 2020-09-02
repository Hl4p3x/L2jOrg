// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExUserInfoInvenWeight extends ServerPacket
{
    private final Player player;
    
    public ExUserInfoInvenWeight(final Player cha) {
        this.player = cha;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_USER_INFO_INVEN_WEIGHT);
        this.writeInt(this.player.getObjectId());
        this.writeInt(this.player.getCurrentLoad());
        this.writeInt(this.player.getMaxLoad());
    }
}
