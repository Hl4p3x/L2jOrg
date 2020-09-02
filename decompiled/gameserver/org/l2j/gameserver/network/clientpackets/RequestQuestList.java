// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.QuestList;
import org.l2j.gameserver.network.GameClient;

public final class RequestQuestList extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        ((GameClient)this.client).sendPacket(new QuestList(((GameClient)this.client).getPlayer()));
    }
}
