// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpCheckAbnormalSkillCondition implements SkillCondition
{
    public final AbnormalType type;
    public final int level;
    public final boolean hasAbnormal;
    public final SkillConditionAffectType affectType;
    
    private OpCheckAbnormalSkillCondition(final AbnormalType type, final int level, final boolean affected, final SkillConditionAffectType affect) {
        this.type = type;
        this.level = level;
        this.hasAbnormal = affected;
        this.affectType = affect;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        boolean b = false;
        switch (this.affectType) {
            case CASTER: {
                b = (caster.getEffectList().hasAbnormalType(this.type, info -> info.getSkill().getAbnormalLvl() >= this.level) == this.hasAbnormal);
                break;
            }
            case TARGET: {
                b = (GameUtils.isCreature(target) && ((Creature)target).getEffectList().hasAbnormalType(this.type, info -> info.getSkill().getAbnormalLvl() >= this.level) == this.hasAbnormal);
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final NamedNodeMap attr = xmlNode.getAttributes();
            return (SkillCondition)new OpCheckAbnormalSkillCondition((AbnormalType)this.parseEnum(attr, (Class)AbnormalType.class, "type"), this.parseInt(attr, "level"), this.parseBoolean(attr, "affected"), (SkillConditionAffectType)this.parseEnum(attr, (Class)SkillConditionAffectType.class, "affect"));
        }
        
        public String conditionName() {
            return "abnormal";
        }
    }
}
