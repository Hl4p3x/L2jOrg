// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ElementalSpiritSetTalent extends UpdateElementalSpiritPacket
{
    public ElementalSpiritSetTalent(final byte type, final boolean result) {
        super(type, result);
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ELEMENTAL_SPIRIT_SET_TALENT);
        this.writeUpdate(client);
    }
}
