// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class TutorialCloseHtml extends ServerPacket
{
    public static final TutorialCloseHtml STATIC_PACKET;
    
    private TutorialCloseHtml() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TUTORIAL_CLOSE_HTML);
    }
    
    static {
        STATIC_PACKET = new TutorialCloseHtml();
    }
}
