// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers.affectscope;

import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.interfaces.ILocational;
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

public class FanPB implements IAffectScopeHandler
{
    public void forEachAffected(final Creature activeChar, final WorldObject target, final Skill skill, final Consumer<? super WorldObject> action) {
        final World instance = World.getInstance();
        final Class<Creature> clazz = Creature.class;
        final int fanRadius = skill.getFanRadius();
        Objects.requireNonNull(action);
        instance.forEachVisibleObjectInRange((WorldObject)activeChar, (Class)clazz, fanRadius, (Consumer)action::accept, (Predicate)this.fanFilterOf(activeChar, skill));
    }
    
    protected Predicate<Creature> fanFilterOf(final Creature activeChar, final Skill skill) {
        final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler((Enum)skill.getAffectObject());
        final double headingAngle = MathUtil.convertHeadingToDegree(activeChar.getHeading());
        final int fanStartAngle = skill.getFanStartAngle();
        final int fanAngle = skill.getFanAngle();
        final double fanHalfAngle = fanAngle / 2.0;
        final int affectLimit = skill.getAffectLimit();
        final AtomicInteger affected = new AtomicInteger(0);
        final int n;
        final AtomicInteger atomicInteger;
        final double n2;
        final int n3;
        final double n4;
        final IAffectObjectHandler affectObjectHandler;
        return creature -> {
            if (((Creature)creature).isDead() || (n > 0 && atomicInteger.get() >= n)) {
                return false;
            }
            else if (Math.abs(MathUtil.calculateAngleFrom((ILocational)activeChar, (ILocational)creature) - (n2 + n3)) > n4) {
                return false;
            }
            else if (affectObjectHandler != null && !affectObjectHandler.checkAffectedObject(activeChar, (Creature)creature)) {
                return false;
            }
            else if (!GeoEngine.getInstance().canSeeTarget((WorldObject)activeChar, creature)) {
                return false;
            }
            else {
                atomicInteger.incrementAndGet();
                return true;
            }
        };
    }
    
    public Enum<AffectScope> getAffectScopeType() {
        return (Enum<AffectScope>)AffectScope.FAN_PB;
    }
}
