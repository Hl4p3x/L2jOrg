// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers.affectscope;

import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.model.Location;
import java.util.function.Predicate;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.skills.targets.TargetType;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.handler.AffectObjectHandler;
import java.util.function.Consumer;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.IAffectScopeHandler;

public class Range implements IAffectScopeHandler
{
    public void forEachAffected(final Creature activeChar, final WorldObject target, final Skill skill, final Consumer<? super WorldObject> action) {
        final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler((Enum)skill.getAffectObject());
        final int affectRange = skill.getAffectRange();
        final int affectLimit = skill.getAffectLimit();
        final TargetType targetType = skill.getTargetType();
        final AtomicInteger affected = new AtomicInteger(0);
        final int n;
        final AtomicInteger atomicInteger;
        final TargetType targetType2;
        final IAffectObjectHandler affectObjectHandler;
        final Predicate<Creature> filter = c -> {
            if (n > 0 && atomicInteger.get() >= n) {
                return false;
            }
            else if (c.isDead() && targetType2 != TargetType.NPC_BODY && targetType2 != TargetType.PC_BODY) {
                return false;
            }
            else if (c == activeChar && target != activeChar) {
                return false;
            }
            else if (c != target && affectObjectHandler != null && !affectObjectHandler.checkAffectedObject(activeChar, c)) {
                return false;
            }
            else if (!GeoEngine.getInstance().canSeeTarget(target, (WorldObject)c)) {
                return false;
            }
            else {
                atomicInteger.incrementAndGet();
                return true;
            }
        };
        if (targetType == TargetType.GROUND) {
            if (GameUtils.isPlayable((WorldObject)activeChar)) {
                final Location worldPosition = activeChar.getActingPlayer().getCurrentSkillWorldPosition();
                if (worldPosition != null) {
                    final ILocational locational;
                    final int n2;
                    final Predicate<ILocational> predicate;
                    World.getInstance().forEachVisibleObjectInRange((WorldObject)activeChar, (Class)Creature.class, (int)(affectRange + MathUtil.calculateDistance2D((ILocational)activeChar, (ILocational)worldPosition)), c -> {
                        if (!(!MathUtil.isInsideRadius3D(c, locational, n2))) {
                            if (predicate.test(c)) {
                                action.accept((Object)c);
                            }
                        }
                    });
                }
            }
        }
        else {
            if (GameUtils.isCreature(target) && filter.test((Creature)target)) {
                action.accept(target);
            }
            World.getInstance().forEachVisibleObjectInRange(target, (Class)Creature.class, affectRange, c -> {
                if (filter.test((Creature)c)) {
                    action.accept((Object)c);
                }
            });
        }
    }
    
    public Enum<AffectScope> getAffectScopeType() {
        return (Enum<AffectScope>)AffectScope.RANGE;
    }
}
