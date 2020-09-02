// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers.affectscope;

import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import java.util.function.Predicate;
import java.util.Comparator;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.util.GameUtils;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.handler.AffectObjectHandler;
import java.util.function.Consumer;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.IAffectScopeHandler;

public class RangeSortByHp implements IAffectScopeHandler
{
    public void forEachAffected(final Creature activeChar, final WorldObject target, final Skill skill, final Consumer<? super WorldObject> action) {
        final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler((Enum)skill.getAffectObject());
        final int affectRange = skill.getAffectRange();
        final int affectLimit = skill.getAffectLimit();
        final AtomicInteger affected = new AtomicInteger(0);
        final int n;
        final AtomicInteger atomicInteger;
        final IAffectObjectHandler affectObjectHandler;
        final Predicate<Creature> filter = c -> {
            if ((n > 0 && atomicInteger.get() >= n) || c.isDead()) {
                return false;
            }
            else if (c == activeChar && target != activeChar) {
                return false;
            }
            else if (affectObjectHandler != null && !affectObjectHandler.checkAffectedObject(activeChar, c)) {
                return false;
            }
            else {
                atomicInteger.incrementAndGet();
                return true;
            }
        };
        if (GameUtils.isCreature(target) && filter.test((Creature)target)) {
            action.accept(target);
        }
        World.getInstance().forVisibleOrderedObjectsInRange(target, (Class)Creature.class, affectRange, (affectLimit > 0) ? affectLimit : Integer.MAX_VALUE, (Predicate)filter, (Comparator)Comparator.comparingInt(Creature::getCurrentHpPercent), (Consumer)action);
    }
    
    public Enum<AffectScope> getAffectScopeType() {
        return (Enum<AffectScope>)AffectScope.RANGE_SORT_BY_HP;
    }
}
