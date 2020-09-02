// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExShowAgitInfo;
import org.l2j.gameserver.network.GameClient;

public class RequestAllAgitInfo extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        ((GameClient)this.client).sendPacket(ExShowAgitInfo.STATIC_PACKET);
    }
}
