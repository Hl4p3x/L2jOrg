// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.vip;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipLuckyGameInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestVipLuckGameInfo extends ClientPacket
{
    @Override
    protected void readImpl() throws Exception {
    }
    
    @Override
    protected void runImpl() throws Exception {
        ((GameClient)this.client).sendPacket(new ReceiveVipLuckyGameInfo());
    }
}
