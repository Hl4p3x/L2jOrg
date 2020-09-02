// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExAlterSkillRequest extends ServerPacket
{
    private final int _currentSkillId;
    private final int _nextSkillId;
    private final int _alterTime;
    
    public ExAlterSkillRequest(final int currentSkill, final int nextSkill, final int alterTime) {
        this._currentSkillId = currentSkill;
        this._nextSkillId = nextSkill;
        this._alterTime = alterTime;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ALTER_SKILL_REQUEST);
        this.writeInt(this._nextSkillId);
        this.writeInt(this._currentSkillId);
        this.writeInt(this._alterTime);
    }
}
