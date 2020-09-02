// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers.affectscope;

import org.l2j.gameserver.model.skills.targets.AffectScope;
import java.util.function.Predicate;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.world.World;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.handler.AffectObjectHandler;
import java.util.function.Consumer;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.IAffectScopeHandler;

public class Pledge implements IAffectScopeHandler
{
    public void forEachAffected(final Creature activeChar, final WorldObject target, final Skill skill, final Consumer<? super WorldObject> action) {
        final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler((Enum)skill.getAffectObject());
        final int affectRange = skill.getAffectRange();
        final int affectLimit = skill.getAffectLimit();
        if (GameUtils.isPlayable(target)) {
            final Playable playable = (Playable)target;
            final Player player = playable.getActingPlayer();
            final AtomicInteger affected = new AtomicInteger(0);
            final int n;
            final AtomicInteger atomicInteger;
            Player p;
            final Player player2;
            final IAffectObjectHandler affectObjectHandler;
            final Predicate<Playable> filter = plbl -> {
                if (n > 0 && atomicInteger.get() >= n) {
                    return false;
                }
                else {
                    p = plbl.getActingPlayer();
                    if (p == null || p.isDead()) {
                        return false;
                    }
                    else if (p != player2 && (p.getClanId() == 0 || p.getClanId() != player2.getClanId())) {
                        return false;
                    }
                    else if (affectObjectHandler != null && !affectObjectHandler.checkAffectedObject(activeChar, (Creature)p)) {
                        return false;
                    }
                    else {
                        atomicInteger.incrementAndGet();
                        return true;
                    }
                }
            };
            if (filter.test(playable)) {
                action.accept((Object)playable);
            }
            World.getInstance().forEachVisibleObjectInRange((WorldObject)playable, (Class)Playable.class, affectRange, c -> {
                if (filter.test((Playable)c)) {
                    action.accept((Object)c);
                }
            });
        }
    }
    
    public Enum<AffectScope> getAffectScopeType() {
        return (Enum<AffectScope>)AffectScope.PLEDGE;
    }
}
