// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Hide extends AbstractEffect
{
    private Hide() {
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayer((WorldObject)effected)) {
            effected.setInvisible(true);
            if (effected.getAI().getNextIntention() != null && effected.getAI().getNextIntention().getCtrlIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
                effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
            }
            World.getInstance().forEachVisibleObject((WorldObject)effected, (Class)Creature.class, target -> {
                target.setTarget((WorldObject)null);
                target.abortAttack();
                target.abortCast();
                target.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
            }, target -> target.getTarget() == effected);
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (GameUtils.isPlayer((WorldObject)effected)) {
            final Player activeChar = effected.getActingPlayer();
            if (!activeChar.inObserverMode()) {
                activeChar.setInvisible(false);
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final Hide INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Hide";
        }
        
        static {
            INSTANCE = new Hide();
        }
    }
}
