// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.data.database.data.CostumeCollectionData;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExCostumeCollectionSkillActive extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_COSTUME_COLLECTION_SKILL_ACTIVE);
        final CostumeCollectionData collection = client.getPlayer().getActiveCostumeCollection();
        this.writeInt(collection.getId());
        this.writeInt(collection.getReuseTime());
    }
}
