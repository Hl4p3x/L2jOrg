// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class CheckRangeSkillCondition implements SkillCondition
{
    private final int min;
    private final int max;
    private final boolean check3D;
    
    public CheckRangeSkillCondition(final int min, final int max, final boolean check3D) {
        this.min = min * min;
        this.max = ((max > 0) ? (max * max) : max);
        this.check3D = check3D;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (Objects.isNull(target)) {
            return false;
        }
        final Creature creature;
        final double radius = caster.getCollisionRadius() + ((target instanceof Creature && (creature = (Creature)target) == (Creature)target) ? creature.getCollisionRadius() : 0.0);
        final double distance = (this.check3D ? MathUtil.calculateDistanceSq3D((ILocational)caster, (ILocational)target) : MathUtil.calculateDistanceSq2D((ILocational)caster, (ILocational)target)) - radius * radius;
        return distance >= this.min && (this.max <= 0 || distance <= this.max);
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final NamedNodeMap attr = xmlNode.getAttributes();
            return (SkillCondition)new CheckRangeSkillCondition(this.parseInt(attr, "min"), this.parseInt(attr, "max"), this.parseBoolean(attr, "check-3d"));
        }
        
        public String conditionName() {
            return "check-range";
        }
    }
}
