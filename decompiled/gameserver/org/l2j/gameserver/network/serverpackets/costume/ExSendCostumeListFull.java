// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.data.database.data.CostumeData;
import org.l2j.gameserver.data.database.data.CostumeCollectionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExSendCostumeListFull extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SEND_COSTUME_LIST_FULL);
        final Player player = client.getPlayer();
        this.writeInt(player.getCostumeAmount());
        player.forEachCostume(this::writeCostume);
        this.writeInt(0);
        final CostumeCollectionData activeCollection = player.getActiveCostumeCollection();
        this.writeInt(activeCollection.getId());
        this.writeInt(activeCollection.getReuseTime());
    }
    
    private void writeCostume(final CostumeData costume) {
        this.writeInt(costume.getId());
        this.writeLong(costume.getAmount());
        this.writeByte(costume.isLocked());
        this.writeByte(costume.checkIsNewAndChange());
    }
}
