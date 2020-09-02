// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ElementalSpiritEvolution extends UpdateElementalSpiritPacket
{
    public ElementalSpiritEvolution(final byte type, final boolean evolved) {
        super(type, evolved);
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ELEMENTAL_SPIRIT_EVOLUTION);
        this.writeUpdate(client);
    }
}
