// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Disarm extends AbstractEffect
{
    private Disarm() {
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return GameUtils.isPlayer((WorldObject)effected);
    }
    
    public long getEffectFlags() {
        return EffectFlag.DISARMED.getMask();
    }
    
    public void continuousInstant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final Player player = effected.getActingPlayer();
        if (Objects.nonNull(player)) {
            player.disarmWeapons();
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final Disarm INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Disarm";
        }
        
        static {
            INSTANCE = new Disarm();
        }
    }
}
