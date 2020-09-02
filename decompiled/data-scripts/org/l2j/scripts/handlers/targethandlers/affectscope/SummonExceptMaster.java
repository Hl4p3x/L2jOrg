// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers.affectscope;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.handler.AffectObjectHandler;
import java.util.function.Consumer;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.IAffectScopeHandler;

public class SummonExceptMaster implements IAffectScopeHandler
{
    public void forEachAffected(final Creature activeChar, final WorldObject target, final Skill skill, final Consumer<? super WorldObject> action) {
        final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler((Enum)skill.getAffectObject());
        final int affectRange = skill.getAffectRange();
        final int affectLimit = skill.getAffectLimit();
        if (GameUtils.isPlayable(target)) {
            final Player player = target.getActingPlayer();
            final int n;
            final IAffectObjectHandler affectObjectHandler;
            player.getServitorsAndPets().stream().filter(c -> !c.isDead()).filter(c -> n <= 0 || GameUtils.checkIfInRange(n, c, target, true)).filter(c -> affectObjectHandler == null || affectObjectHandler.checkAffectedObject(activeChar, c)).limit((affectLimit > 0) ? affectLimit : Long.MAX_VALUE).forEach(action);
        }
    }
    
    public Enum<AffectScope> getAffectScopeType() {
        return (Enum<AffectScope>)AffectScope.SUMMON_EXCEPT_MASTER;
    }
}
