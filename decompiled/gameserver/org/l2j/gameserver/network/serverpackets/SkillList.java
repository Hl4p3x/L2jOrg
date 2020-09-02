// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import java.util.Comparator;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import java.util.List;

public final class SkillList extends ServerPacket
{
    private final List<Skill> _skills;
    private int _lastLearnedSkillId;
    
    public SkillList() {
        this._skills = new ArrayList<Skill>();
        this._lastLearnedSkillId = 0;
    }
    
    public void addSkill(final int id, final int reuseDelayGroup, final int level, final int subLevel, final boolean passive, final boolean disabled, final boolean enchanted) {
        this._skills.add(new Skill(id, reuseDelayGroup, level, subLevel, passive, disabled, enchanted));
    }
    
    public void setLastLearnedSkillId(final int lastLearnedSkillId) {
        this._lastLearnedSkillId = lastLearnedSkillId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SKILL_LIST);
        this._skills.sort(Comparator.comparing(s -> Integer.valueOf(SkillEngine.getInstance().getSkill(s.id, s.level).isToggle() ? 1 : 0)));
        this.writeInt(this._skills.size());
        for (final Skill temp : this._skills) {
            this.writeInt((int)(temp.passive ? 1 : 0));
            this.writeShort((short)temp.level);
            this.writeShort((short)temp.subLevel);
            this.writeInt(temp.id);
            this.writeInt(temp.reuseDelayGroup);
            this.writeByte((byte)(byte)(temp.disabled ? 1 : 0));
            this.writeByte((byte)(byte)(temp.enchanted ? 1 : 0));
        }
        this.writeInt(this._lastLearnedSkillId);
    }
    
    static class Skill
    {
        public int id;
        public int reuseDelayGroup;
        public int level;
        public int subLevel;
        public boolean passive;
        public boolean disabled;
        public boolean enchanted;
        
        Skill(final int pId, final int pReuseDelayGroup, final int pLevel, final int pSubLevel, final boolean pPassive, final boolean pDisabled, final boolean pEnchanted) {
            this.id = pId;
            this.reuseDelayGroup = pReuseDelayGroup;
            this.level = pLevel;
            this.subLevel = pSubLevel;
            this.passive = pPassive;
            this.disabled = pDisabled;
            this.enchanted = pEnchanted;
        }
    }
}
