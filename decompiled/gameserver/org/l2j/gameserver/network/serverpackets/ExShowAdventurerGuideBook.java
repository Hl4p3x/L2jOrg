// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowAdventurerGuideBook extends ServerPacket
{
    public static final ExShowAdventurerGuideBook STATIC_PACKET;
    
    private ExShowAdventurerGuideBook() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_ADVENTURER_GUIDE_BOOK);
    }
    
    static {
        STATIC_PACKET = new ExShowAdventurerGuideBook();
    }
}
