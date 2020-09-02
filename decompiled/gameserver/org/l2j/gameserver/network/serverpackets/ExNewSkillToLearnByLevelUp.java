// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExNewSkillToLearnByLevelUp extends ServerPacket
{
    public static final ExNewSkillToLearnByLevelUp STATIC_PACKET;
    
    private ExNewSkillToLearnByLevelUp() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_NEW_SKILL_TO_LEARN_BY_LEVEL_UP);
    }
    
    static {
        STATIC_PACKET = new ExNewSkillToLearnByLevelUp();
    }
}
