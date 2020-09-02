// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.autoplay;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;

class PlayerFinder extends AbstractAutoPlayTargetFinder
{
    @Override
    public boolean canBeTarget(final Player player, final WorldObject target) {
        final Player playerTarget;
        return target instanceof Player && (playerTarget = (Player)target) == target && !playerTarget.isDead() && super.canBeTarget(player, playerTarget);
    }
    
    @Override
    public Creature findNextTarget(final Player player, final int range) {
        return this.findNextTarget(player, Player.class, range);
    }
}
