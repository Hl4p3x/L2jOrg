// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritAbsorbInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExElementalSpiritAbsorbInfo extends ClientPacket
{
    private byte type;
    
    @Override
    protected void readImpl() throws Exception {
        this.readByte();
        this.type = this.readByte();
    }
    
    @Override
    protected void runImpl() {
        ((GameClient)this.client).sendPacket(new ElementalSpiritAbsorbInfo(this.type));
    }
}
