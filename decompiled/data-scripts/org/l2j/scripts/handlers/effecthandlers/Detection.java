// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Detection extends AbstractEffect
{
    private Detection() {
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effector) || !GameUtils.isPlayer((WorldObject)effected)) {
            return;
        }
        final Player player = effector.getActingPlayer();
        final Player target = effected.getActingPlayer();
        final boolean hasParty = player.isInParty();
        final boolean hasClan = player.getClanId() > 0;
        final boolean hasAlly = player.getAllyId() > 0;
        if (target.isInvisible()) {
            if (hasParty && target.isInParty() && player.getParty().getLeaderObjectId() == target.getParty().getLeaderObjectId()) {
                return;
            }
            if (hasClan && player.getClanId() == target.getClanId()) {
                return;
            }
            if (hasAlly && player.getAllyId() == target.getAllyId()) {
                return;
            }
            target.getEffectList().stopEffects(AbnormalType.HIDE);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final Detection INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Detection";
        }
        
        static {
            INSTANCE = new Detection();
        }
    }
}
