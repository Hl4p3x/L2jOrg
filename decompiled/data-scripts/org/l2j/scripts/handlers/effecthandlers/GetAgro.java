// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import java.util.Set;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.world.World;
import java.util.Objects;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class GetAgro extends AbstractEffect
{
    private GetAgro() {
    }
    
    public EffectType getEffectType() {
        return EffectType.AGGRESSION;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isAttackable((WorldObject)effected)) {
            effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { effector });
            final NpcTemplate template = ((Attackable)effected).getTemplate();
            final Set<Integer> clans = (Set<Integer>)template.getClans();
            if (Objects.nonNull(clans)) {
                World.getInstance().forEachVisibleObjectInRange((WorldObject)effected, (Class)Attackable.class, template.getClanHelpRange(), attackable -> this.receiveHate(attackable, effector), nearby -> this.canReceiveHate(nearby, clans));
            }
        }
    }
    
    private boolean canReceiveHate(final Attackable nearby, final Set<Integer> clans) {
        return !nearby.isMovementDisabled() && nearby.getTemplate().isClan((Set)clans);
    }
    
    private void receiveHate(final Attackable attackable, final Creature effector) {
        attackable.addDamageHate(effector, 1, 200);
        attackable.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { effector });
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final GetAgro INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "GetAgro";
        }
        
        static {
            INSTANCE = new GetAgro();
        }
    }
}
