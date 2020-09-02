// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExElementalSpiritGetExp extends ServerPacket
{
    private final long experience;
    private final byte type;
    
    public ExElementalSpiritGetExp(final byte type, final long experience) {
        this.type = type;
        this.experience = experience;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ELEMENTAL_SPIRIT_GET_EXP);
        this.writeByte(this.type);
        this.writeLong(this.experience);
    }
}
