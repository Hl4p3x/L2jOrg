// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class PledgeSkillAdd extends ServerPacket
{
    private final int id;
    private final int level;
    
    public PledgeSkillAdd(final int id, final int level) {
        this.id = id;
        this.level = level;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_SKILL_ADD);
        this.writeInt(this.id);
        this.writeInt(this.level);
    }
}
