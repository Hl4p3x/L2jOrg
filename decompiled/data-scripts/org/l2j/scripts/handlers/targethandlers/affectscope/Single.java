// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers.affectscope;

import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.handler.AffectObjectHandler;
import java.util.function.Consumer;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.IAffectScopeHandler;

public class Single implements IAffectScopeHandler
{
    public void forEachAffected(final Creature activeChar, final WorldObject target, final Skill skill, final Consumer<? super WorldObject> action) {
        final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler((Enum)skill.getAffectObject());
        if (GameUtils.isCreature(target)) {
            if (skill.getTargetType() == TargetType.GROUND) {
                action.accept((Object)activeChar);
            }
            if (affectObject == null || affectObject.checkAffectedObject(activeChar, (Creature)target)) {
                action.accept(target);
            }
        }
        else if (GameUtils.isItem(target)) {
            action.accept(target);
        }
    }
    
    public Enum<AffectScope> getAffectScopeType() {
        return (Enum<AffectScope>)AffectScope.SINGLE;
    }
}
