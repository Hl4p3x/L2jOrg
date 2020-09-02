// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ElementalSpiritExtract extends UpdateElementalSpiritPacket
{
    public ElementalSpiritExtract(final byte type, final boolean extracted) {
        super(type, extracted);
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ELEMENTAL_SPIRIT_EXTRACT);
        this.writeUpdate(client);
    }
}
