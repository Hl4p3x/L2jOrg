// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class OpCheckCastRangeSkillCondition implements SkillCondition
{
    private final int _distance;
    
    public OpCheckCastRangeSkillCondition(final StatsSet params) {
        this._distance = params.getInt("distance");
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        return target != null && !MathUtil.isInsideRadius3D((ILocational)caster, (ILocational)target, this._distance) && GeoEngine.getInstance().canSeeTarget((WorldObject)caster, target);
    }
}
