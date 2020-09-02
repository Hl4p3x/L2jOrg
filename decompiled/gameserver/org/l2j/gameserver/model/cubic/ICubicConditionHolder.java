// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.cubic;

import org.l2j.gameserver.model.cubic.conditions.ICubicCondition;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;

public interface ICubicConditionHolder
{
    boolean validateConditions(final CubicInstance cubic, final Creature owner, final WorldObject target);
    
    void addCondition(final ICubicCondition condition);
}
