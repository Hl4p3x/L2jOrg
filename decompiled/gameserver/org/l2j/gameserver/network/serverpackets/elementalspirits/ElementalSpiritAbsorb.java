// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ElementalSpiritAbsorb extends UpdateElementalSpiritPacket
{
    public ElementalSpiritAbsorb(final byte type, final boolean absorbed) {
        super(type, absorbed);
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ELEMENTAL_SPIRIT_ABSORB);
        this.writeUpdate(client);
    }
}
