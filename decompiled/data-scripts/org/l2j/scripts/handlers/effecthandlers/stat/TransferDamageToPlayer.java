// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers.stat;

import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.StatsSet;

public final class TransferDamageToPlayer extends AbstractStatAddEffect
{
    private TransferDamageToPlayer(final StatsSet params) {
        super(params, Stat.TRANSFER_DAMAGE_TO_PLAYER);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (GameUtils.isPlayable((WorldObject)effected) && GameUtils.isPlayer((WorldObject)effector)) {
            ((Playable)effected).setTransferDamageTo((Player)null);
        }
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (GameUtils.isPlayable((WorldObject)effected) && GameUtils.isPlayer((WorldObject)effector)) {
            ((Playable)effected).setTransferDamageTo(effector.getActingPlayer());
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new TransferDamageToPlayer(data);
        }
        
        public String effectName() {
            return "TransferDamageToPlayer";
        }
    }
}
