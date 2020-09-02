// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.SkillLearn;
import java.util.List;

public class ExAcquirableSkillListByClass extends ServerPacket
{
    final List<SkillLearn> _learnable;
    final AcquireSkillType _type;
    
    public ExAcquirableSkillListByClass(final List<SkillLearn> learnable, final AcquireSkillType type) {
        this._learnable = learnable;
        this._type = type;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ACQUIRABLE_SKILL_LIST_BY_CLASS);
        this.writeShort((short)this._type.getId());
        this.writeShort((short)this._learnable.size());
        for (final SkillLearn skill : this._learnable) {
            this.writeInt(skill.getSkillId());
            this.writeShort((short)skill.getSkillLevel());
            this.writeShort((short)skill.getSkillLevel());
            this.writeByte((byte)skill.getGetLevel());
            this.writeLong(skill.getLevelUpSp());
            this.writeByte((byte)skill.getRequiredItems().size());
            if (this._type == AcquireSkillType.SUBPLEDGE) {
                this.writeShort((short)0);
            }
        }
    }
}
