// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;

public interface ICondition
{
    boolean test(final Creature creature, final WorldObject object);
}
