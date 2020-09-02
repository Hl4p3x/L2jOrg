// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.actor.instance.Chest;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class OpenChest extends AbstractEffect
{
    private OpenChest() {
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!(effected instanceof Chest)) {
            return;
        }
        final Player player = effector.getActingPlayer();
        final Chest chest = (Chest)effected;
        if (chest.isDead() || player.getInstanceWorld() != chest.getInstanceWorld()) {
            return;
        }
        if ((player.getLevel() <= 77 && Math.abs(chest.getLevel() - player.getLevel()) <= 6) || (player.getLevel() >= 78 && Math.abs(chest.getLevel() - player.getLevel()) <= 5)) {
            player.broadcastSocialAction(3);
            chest.setSpecialDrop();
            chest.setMustRewardExpSp(false);
            chest.reduceCurrentHp((double)chest.getMaxHp(), (Creature)player, skill, DamageInfo.DamageType.OTHER);
        }
        else {
            player.broadcastSocialAction(13);
            chest.addDamageHate((Creature)player, 0, 1);
            chest.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { player });
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final OpenChest INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "OpenChest";
        }
        
        static {
            INSTANCE = new OpenChest();
        }
    }
}
