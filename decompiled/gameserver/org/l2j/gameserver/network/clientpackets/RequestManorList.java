// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExSendManorList;
import org.l2j.gameserver.network.GameClient;

public class RequestManorList extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        ((GameClient)this.client).sendPacket(ExSendManorList.STATIC_PACKET);
    }
}
