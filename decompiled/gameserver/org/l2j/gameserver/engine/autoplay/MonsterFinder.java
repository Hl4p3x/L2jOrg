// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.autoplay;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;

class MonsterFinder extends AbstractAutoPlayTargetFinder
{
    @Override
    public boolean canBeTarget(final Player player, final WorldObject target) {
        final Monster monster;
        return target instanceof Monster && (monster = (Monster)target) == target && !monster.isDead() && super.canBeTarget(player, monster);
    }
    
    @Override
    public Creature findNextTarget(final Player player, final int range) {
        return this.findNextTarget(player, Monster.class, range);
    }
}
