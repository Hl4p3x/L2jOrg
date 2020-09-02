// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class CheckLevelSkillCondition implements SkillCondition
{
    public final int minLevel;
    public final int maxLevel;
    public final SkillConditionAffectType affectType;
    
    private CheckLevelSkillCondition(final SkillConditionAffectType affect, final int minLevel, final int maxLevel) {
        this.affectType = affect;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        boolean between = false;
        switch (this.affectType) {
            case CASTER: {
                between = Util.isBetween(caster.getLevel(), this.minLevel, this.maxLevel);
                break;
            }
            case TARGET: {
                between = (GameUtils.isPlayer(target) && Util.isBetween(target.getActingPlayer().getLevel(), this.minLevel, this.maxLevel));
                break;
            }
            default: {
                between = false;
                break;
            }
        }
        return between;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final NamedNodeMap attr = xmlNode.getAttributes();
            return (SkillCondition)new CheckLevelSkillCondition((SkillConditionAffectType)this.parseEnum(attr, (Class)SkillConditionAffectType.class, "affect"), this.parseInt(attr, "min-level"), this.parseInt(attr, "max-level"));
        }
        
        public String conditionName() {
            return "level";
        }
    }
}
