// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExSubPledgeSkillAdd extends ServerPacket
{
    private final int _type;
    private final int _skillId;
    private final int _skillLevel;
    
    public ExSubPledgeSkillAdd(final int type, final int skillId, final int skillLevel) {
        this._type = type;
        this._skillId = skillId;
        this._skillLevel = skillLevel;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SUBPLEDGE_SKILL_ADD);
        this.writeInt(this._type);
        this.writeInt(this._skillId);
        this.writeInt(this._skillLevel);
    }
}
