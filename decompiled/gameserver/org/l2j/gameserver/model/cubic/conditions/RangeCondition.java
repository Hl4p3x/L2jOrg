// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.cubic.conditions;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.CubicInstance;

public class RangeCondition implements ICubicCondition
{
    private final int _range;
    
    public RangeCondition(final int range) {
        this._range = range;
    }
    
    @Override
    public boolean test(final CubicInstance cubic, final Creature owner, final WorldObject target) {
        return MathUtil.isInsideRadius2D(owner, target, this._range);
    }
}
