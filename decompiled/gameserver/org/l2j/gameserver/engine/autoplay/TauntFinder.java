// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.autoplay;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;

class TauntFinder extends AbstractAutoPlayTargetFinder
{
    @Override
    public boolean canBeTarget(final Player player, final WorldObject target) {
        final Creature creature;
        return target instanceof Creature && (creature = (Creature)target) == target && !creature.isDead() && super.canBeTarget(player, creature);
    }
    
    @Override
    public Creature findNextTarget(final Player player, final int range) {
        return this.findNextTarget(player, Creature.class, range);
    }
}
