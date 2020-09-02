// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class RemainStatusSkillCondition implements SkillCondition
{
    StatusType stat;
    boolean lower;
    int amount;
    SkillConditionAffectType affect;
    
    public RemainStatusSkillCondition(final StatusType stat, final int amount, final boolean lower, final SkillConditionAffectType affect) {
        this.stat = stat;
        this.amount = amount;
        this.lower = lower;
        this.affect = affect;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (this.affect == SkillConditionAffectType.TARGET && !GameUtils.isCreature(target)) {
            return false;
        }
        final Creature creature = (Creature)((this.affect == SkillConditionAffectType.CASTER) ? caster : target);
        boolean b = false;
        switch (this.stat) {
            case CP: {
                b = (this.lower == creature.getCurrentCpPercent() <= this.amount);
                break;
            }
            case HP: {
                b = (this.lower == creature.getCurrentHpPercent() <= this.amount);
                break;
            }
            case MP: {
                b = (this.lower == creature.getCurrentMpPercent() <= this.amount);
                break;
            }
            default: {
                throw new IncompatibleClassChangeError();
            }
        }
        return b;
    }
    
    private enum StatusType
    {
        CP, 
        HP, 
        MP;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final NamedNodeMap attr = xmlNode.getAttributes();
            return (SkillCondition)new RemainStatusSkillCondition((StatusType)this.parseEnum(attr, (Class)StatusType.class, "stat"), this.parseInt(attr, "amount"), this.parseBoolean(attr, "lower"), (SkillConditionAffectType)this.parseEnum(attr, (Class)SkillConditionAffectType.class, "affect"));
        }
        
        public String conditionName() {
            return "remain-status";
        }
    }
}
