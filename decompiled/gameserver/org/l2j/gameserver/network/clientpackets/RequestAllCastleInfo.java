// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExShowCastleInfo;
import org.l2j.gameserver.network.GameClient;

public class RequestAllCastleInfo extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        ((GameClient)this.client).sendPacket(ExShowCastleInfo.STATIC_PACKET);
    }
}
