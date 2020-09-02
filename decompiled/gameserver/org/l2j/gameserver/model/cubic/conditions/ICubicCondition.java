// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.cubic.conditions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.cubic.CubicInstance;

public interface ICubicCondition
{
    boolean test(final CubicInstance cubic, final Creature owner, final WorldObject target);
}
