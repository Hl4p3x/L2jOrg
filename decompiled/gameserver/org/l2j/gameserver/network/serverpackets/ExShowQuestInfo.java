// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowQuestInfo extends ServerPacket
{
    public static final ExShowQuestInfo STATIC_PACKET;
    
    private ExShowQuestInfo() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_QUEST_INFO);
    }
    
    static {
        STATIC_PACKET = new ExShowQuestInfo();
    }
}
