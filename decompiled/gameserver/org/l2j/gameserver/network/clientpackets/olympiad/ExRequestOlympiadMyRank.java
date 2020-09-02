// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.olympiad;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMyRankInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestOlympiadMyRank extends ClientPacket
{
    @Override
    protected void readImpl() throws Exception {
    }
    
    @Override
    protected void runImpl() {
        ((GameClient)this.client).sendPacket(new ExOlympiadMyRankInfo());
    }
}
