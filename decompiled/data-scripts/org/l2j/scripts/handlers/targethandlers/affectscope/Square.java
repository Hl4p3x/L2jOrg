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

public class Square extends SquarePB
{
    @Override
    public void forEachAffected(final Creature activeChar, final WorldObject target, final Skill skill, final Consumer<? super WorldObject> action) {
        final int squareLength = skill.getFanRadius();
        final int squareWidth = skill.getFanAngle();
        final int radius = (int)Math.sqrt(squareLength * squareLength + squareWidth * squareWidth);
        final Predicate<Creature> filter = this.squareFilterOf(activeChar, skill, squareLength, squareWidth);
        final World instance = World.getInstance();
        final Class<Creature> clazz = Creature.class;
        final int n = radius;
        Objects.requireNonNull(action);
        instance.forEachVisibleObjectInRange((WorldObject)activeChar, (Class)clazz, n, (Consumer)action::accept, (Predicate)filter);
        if (filter.test(activeChar)) {
            action.accept((Object)activeChar);
        }
    }
    
    @Override
    public Enum<AffectScope> getAffectScopeType() {
        return (Enum<AffectScope>)AffectScope.SQUARE;
    }
}
