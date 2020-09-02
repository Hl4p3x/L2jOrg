// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExUserBoostStat extends ServerPacket
{
    private final BoostStatType type;
    private final short percent;
    
    public ExUserBoostStat(final BoostStatType type, final short percent) {
        this.type = type;
        this.percent = percent;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_USER_BOOST_STAT);
        this.writeByte(this.type.ordinal() + 1);
        this.writeByte(1);
        this.writeShort(this.percent);
    }
    
    public enum BoostStatType
    {
        SERVER, 
        STAT, 
        OTHER;
    }
}
