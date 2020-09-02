// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.costume;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Set;
import org.l2j.gameserver.data.database.data.CostumeData;
import java.util.Collection;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExSendCostumeList extends ServerPacket
{
    private final Collection<CostumeData> costumes;
    
    public ExSendCostumeList(final CostumeData playerCostume) {
        this.costumes = Set.of(playerCostume);
    }
    
    public ExSendCostumeList(final Set<CostumeData> costumes) {
        this.costumes = costumes;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SEND_COSTUME_LIST);
        this.writeInt(this.costumes.size());
        for (final CostumeData costume : this.costumes) {
            this.writeInt(costume.getId());
            this.writeLong(costume.getAmount());
            this.writeByte(costume.isLocked());
            this.writeByte(costume.checkIsNewAndChange());
        }
    }
}
