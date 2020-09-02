// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers.affectscope;

import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.engine.geo.GeoEngine;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.handler.AffectObjectHandler;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import java.util.function.Consumer;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.IAffectScopeHandler;

public class SquarePB implements IAffectScopeHandler
{
    public void forEachAffected(final Creature activeChar, final WorldObject target, final Skill skill, final Consumer<? super WorldObject> action) {
        final int squareLength = skill.getFanRadius();
        final int squareWidth = skill.getFanAngle();
        final int radius = (int)Math.sqrt(squareLength * squareLength + squareWidth * squareWidth);
        final World instance = World.getInstance();
        final Class<Creature> clazz = Creature.class;
        final int n = radius;
        Objects.requireNonNull(action);
        instance.forEachVisibleObjectInRange((WorldObject)activeChar, (Class)clazz, n, (Consumer)action::accept, (Predicate)this.squareFilterOf(activeChar, skill, squareLength, squareWidth));
    }
    
    protected Predicate<Creature> squareFilterOf(final Creature activeChar, final Skill skill, final int squareLength, final int squareWidth) {
        final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler((Enum)skill.getAffectObject());
        final int squareStartAngle = skill.getFanStartAngle();
        final int affectLimit = skill.getAffectLimit();
        final int rectX = activeChar.getX();
        final int rectY = activeChar.getY() - squareWidth / 2;
        final double heading = Math.toRadians(squareStartAngle + MathUtil.convertHeadingToDegree(activeChar.getHeading()));
        final double cos = Math.cos(-heading);
        final double sin = Math.sin(-heading);
        final AtomicInteger affected = new AtomicInteger(0);
        final int n;
        final AtomicInteger atomicInteger;
        int xp;
        int yp;
        final double n2;
        final double n3;
        int xr;
        int yr;
        final int n4;
        final int n5;
        final IAffectObjectHandler affectObjectHandler;
        return creature -> {
            if (creature.isDead() || (n > 0 && atomicInteger.get() >= n)) {
                return false;
            }
            else {
                xp = creature.getX() - activeChar.getX();
                yp = creature.getY() - activeChar.getY();
                xr = (int)(activeChar.getX() + xp * n2 - yp * n3);
                yr = (int)(activeChar.getY() + xp * n3 + yp * n2);
                if (xr > n4 && xr < n4 + squareLength && yr > n5 && yr < n5 + squareWidth) {
                    if (affectObjectHandler != null && !affectObjectHandler.checkAffectedObject(activeChar, creature)) {
                        return false;
                    }
                    else if (!GeoEngine.getInstance().canSeeTarget((WorldObject)activeChar, (WorldObject)creature)) {
                        return false;
                    }
                    else {
                        atomicInteger.incrementAndGet();
                        return true;
                    }
                }
                else {
                    return false;
                }
            }
        };
    }
    
    public Enum<AffectScope> getAffectScopeType() {
        return (Enum<AffectScope>)AffectScope.SQUARE_PB;
    }
}
