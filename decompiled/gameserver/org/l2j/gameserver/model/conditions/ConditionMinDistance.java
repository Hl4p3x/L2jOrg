// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionMinDistance extends Condition
{
    private final int _distance;
    
    public ConditionMinDistance(final int distance) {
        this._distance = distance;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        return effected != null && !MathUtil.isInsideRadius3D(effector, effected, this._distance) && GeoEngine.getInstance().canSeeTarget(effector, effected);
    }
}
