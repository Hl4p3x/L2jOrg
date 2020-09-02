// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.engine.skill.api.Skill;

public class PledgeSkillList extends ServerPacket
{
    private final Skill[] _skills;
    private final SubPledgeSkill[] _subSkills;
    
    public PledgeSkillList(final Clan clan) {
        this._skills = clan.getAllSkills();
        this._subSkills = clan.getAllSubSkills();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PLEDGE_SKILL_LIST);
        this.writeInt(this._skills.length);
        this.writeInt(this._subSkills.length);
        for (final Skill sk : this._skills) {
            this.writeInt(sk.getDisplayId());
            this.writeShort((short)sk.getDisplayLevel());
            this.writeShort((short)0);
        }
        for (final SubPledgeSkill sk2 : this._subSkills) {
            this.writeInt(sk2._subType);
            this.writeInt(sk2._skillId);
            this.writeShort((short)sk2._skillLvl);
            this.writeShort((short)0);
        }
    }
    
    public static class SubPledgeSkill
    {
        int _subType;
        int _skillId;
        int _skillLvl;
        
        public SubPledgeSkill(final int subType, final int skillId, final int skillLvl) {
            this._subType = subType;
            this._skillId = skillId;
            this._skillLvl = skillLvl;
        }
    }
}
