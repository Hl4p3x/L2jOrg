// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.costume;

import java.util.Iterator;
import java.util.Objects;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Set;
import org.l2j.gameserver.data.database.data.CostumeData;
import java.util.Collection;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExCostumeEvolution extends ServerPacket
{
    private boolean success;
    private Collection<CostumeData> targetCostumes;
    private CostumeData resultCostume;
    
    private ExCostumeEvolution() {
    }
    
    public static ExCostumeEvolution failed() {
        return new ExCostumeEvolution();
    }
    
    public static ExCostumeEvolution success(final Set<CostumeData> costume, final CostumeData resultCostume) {
        final ExCostumeEvolution packet = new ExCostumeEvolution();
        packet.success = true;
        packet.targetCostumes = costume;
        packet.resultCostume = resultCostume;
        return packet;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_COSTUME_EVOLUTION);
        this.writeByte(this.success);
        this.writeInt(this.targetCostumes.size());
        for (final CostumeData targetCostume : this.targetCostumes) {
            this.writeInt(targetCostume.getId());
            this.writeLong(targetCostume.getAmount());
        }
        if (Objects.nonNull(this.resultCostume)) {
            this.writeInt(1);
            this.writeInt(this.resultCostume.getId());
            this.writeLong(this.resultCostume.getAmount());
        }
        else {
            this.writeInt(0);
        }
    }
}
