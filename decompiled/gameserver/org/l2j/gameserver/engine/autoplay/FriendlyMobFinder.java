// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.autoplay;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.FriendlyMob;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;

class FriendlyMobFinder extends AbstractAutoPlayTargetFinder
{
    @Override
    public boolean canBeTarget(final Player player, final WorldObject target) {
        final FriendlyMob friendly;
        return target instanceof FriendlyMob && (friendly = (FriendlyMob)target) == target && !friendly.isDead() && super.canBeTarget(player, friendly);
    }
    
    @Override
    public Creature findNextTarget(final Player player, final int range) {
        return this.findNextTarget(player, FriendlyMob.class, range);
    }
}
