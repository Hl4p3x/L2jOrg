// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.database.data.CostumeData;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExCostumeExtract extends ServerPacket
{
    private int costumeId;
    private boolean success;
    private long amount;
    private int extractedItem;
    private long totalAmount;
    
    private ExCostumeExtract() {
    }
    
    public static ExCostumeExtract failed(final int costumeId) {
        final ExCostumeExtract packet = new ExCostumeExtract();
        packet.costumeId = costumeId;
        return packet;
    }
    
    public static ExCostumeExtract success(final CostumeData costume, final int extractItem, final long amount) {
        final ExCostumeExtract packet = new ExCostumeExtract();
        packet.costumeId = costume.getId();
        packet.success = true;
        packet.extractedItem = extractItem;
        packet.amount = amount;
        packet.totalAmount = costume.getAmount();
        return packet;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_COSTUME_EXTRACT);
        this.writeByte(this.success);
        this.writeInt(this.costumeId);
        this.writeLong(this.amount);
        this.writeInt(this.extractedItem);
        this.writeLong(this.amount);
        this.writeLong(this.totalAmount);
    }
}
