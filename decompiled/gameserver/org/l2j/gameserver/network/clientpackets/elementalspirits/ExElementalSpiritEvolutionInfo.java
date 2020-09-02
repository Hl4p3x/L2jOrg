// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritEvolutionInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExElementalSpiritEvolutionInfo extends ClientPacket
{
    private byte id;
    
    @Override
    protected void readImpl() throws Exception {
        this.id = this.readByte();
    }
    
    @Override
    protected void runImpl() {
        ((GameClient)this.client).sendPacket(new ElementalSpiritEvolutionInfo(this.id));
    }
}
