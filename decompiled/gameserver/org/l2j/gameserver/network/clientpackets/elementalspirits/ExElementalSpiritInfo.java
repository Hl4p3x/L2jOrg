// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExElementalSpiritInfo extends ClientPacket
{
    private byte type;
    
    public void readImpl() {
        this.type = this.readByte();
    }
    
    public void runImpl() {
        ((GameClient)this.client).sendPacket(new ElementalSpiritInfo(((GameClient)this.client).getPlayer().getActiveElementalSpiritType(), this.type));
    }
}
