// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.autoplay;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;

interface AutoPlayTargetFinder
{
    boolean canBeTarget(final Player player, final WorldObject target);
    
    Creature findNextTarget(final Player player, final int range);
}
