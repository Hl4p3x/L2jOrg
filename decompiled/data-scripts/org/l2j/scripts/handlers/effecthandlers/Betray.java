// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Betray extends AbstractEffect
{
    private Betray() {
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return GameUtils.isPlayer((WorldObject)effector) && GameUtils.isSummon((WorldObject)effected);
    }
    
    public long getEffectFlags() {
        return EffectFlag.BETRAYED.getMask();
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { effected.getActingPlayer() });
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final Betray INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Betray";
        }
        
        static {
            INSTANCE = new Betray();
        }
    }
}
