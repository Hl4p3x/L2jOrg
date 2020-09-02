// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchList;
import org.l2j.gameserver.network.GameClient;

public class RequestExOlympiadMatchListRefresh extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        ((GameClient)this.client).sendPacket(new ExOlympiadMatchList());
    }
}
