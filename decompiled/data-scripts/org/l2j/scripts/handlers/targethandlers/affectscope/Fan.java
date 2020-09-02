// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers.affectscope;

import org.l2j.gameserver.model.skills.targets.AffectScope;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import java.util.function.Consumer;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;

public class Fan extends FanPB
{
    @Override
    public void forEachAffected(final Creature activeChar, final WorldObject target, final Skill skill, final Consumer<? super WorldObject> action) {
        final Predicate<Creature> filter = this.fanFilterOf(activeChar, skill);
        final World instance = World.getInstance();
        final Class<Creature> clazz = Creature.class;
        final int fanRadius = skill.getFanRadius();
        Objects.requireNonNull(action);
        instance.forEachVisibleObjectInRange((WorldObject)activeChar, (Class)clazz, fanRadius, (Consumer)action::accept, (Predicate)filter);
        if (filter.test(activeChar)) {
            action.accept((Object)activeChar);
        }
    }
    
    @Override
    public Enum<AffectScope> getAffectScopeType() {
        return (Enum<AffectScope>)AffectScope.FAN;
    }
}
