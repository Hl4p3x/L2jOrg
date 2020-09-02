// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.LinkedList;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.List;
import org.l2j.gameserver.enums.SkillEnchantType;

public class ExEnchantSkillList extends ServerPacket
{
    private final SkillEnchantType _type;
    private final List<Skill> _skills;
    
    public ExEnchantSkillList(final SkillEnchantType type) {
        this._skills = new LinkedList<Skill>();
        this._type = type;
    }
    
    public void addSkill(final Skill skill) {
        this._skills.add(skill);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ENCHANT_SKILL_LIST);
        this.writeInt(this._type.ordinal());
        this.writeInt(this._skills.size());
        for (final Skill skill : this._skills) {
            this.writeInt(skill.getId());
            this.writeShort((short)skill.getLevel());
            this.writeShort((short)skill.getSubLevel());
        }
    }
}
