// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritExtractInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExElementalSpiritExtractInfo extends ClientPacket
{
    private byte type;
    
    @Override
    protected void readImpl() throws Exception {
        this.type = this.readByte();
    }
    
    @Override
    protected void runImpl() {
        ((GameClient)this.client).sendPacket(new ElementalSpiritExtractInfo(this.type));
    }
}
