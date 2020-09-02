// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.Position;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpBlinkSkillCondition implements SkillCondition
{
    private final int angle;
    private final int range;
    
    private OpBlinkSkillCondition(final Position direction, final int range) {
        int angle = 0;
        switch (direction) {
            case BACK: {
                angle = 0;
                break;
            }
            case FRONT: {
                angle = 180;
                break;
            }
            default: {
                angle = -1;
                break;
            }
        }
        this.angle = angle;
        this.range = range;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        final double angle = MathUtil.convertHeadingToDegree(caster.getHeading());
        final double radian = Math.toRadians(angle);
        final double course = Math.toRadians(this.angle);
        final int x1 = (int)(Math.cos(3.141592653589793 + radian + course) * this.range);
        final int y1 = (int)(Math.sin(3.141592653589793 + radian + course) * this.range);
        final int x2 = caster.getX() + x1;
        final int y2 = caster.getY() + y1;
        final int z = caster.getZ();
        return GeoEngine.getInstance().canMoveToTarget(caster.getX(), caster.getY(), caster.getZ(), x2, y2, z, caster.getInstanceWorld());
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final NamedNodeMap attr = xmlNode.getAttributes();
            return (SkillCondition)new OpBlinkSkillCondition((Position)this.parseEnum(attr, (Class)Position.class, "direction"), this.parseInt(attr, "range"));
        }
        
        public String conditionName() {
            return "blink";
        }
    }
}
