// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pledge;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class PledgeSkillDelete extends ServerPacket
{
    private final Skill skill;
    
    public PledgeSkillDelete(final Skill skill) {
        this.skill = skill;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_SKILL_DELETE);
        this.writeInt(this.skill.getId());
        this.writeInt(this.skill.getLevel());
    }
}
