// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExChangeMpCost extends ServerPacket
{
    private final double modifier;
    private final int skillType;
    
    public ExChangeMpCost(final double modifier, final int skillType) {
        this.modifier = modifier;
        this.skillType = skillType;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_CHANGE_MP_COST);
        this.writeInt(this.skillType);
        this.writeDouble(this.modifier);
    }
}
