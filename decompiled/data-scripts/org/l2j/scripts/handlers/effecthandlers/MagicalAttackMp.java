// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class MagicalAttackMp extends AbstractEffect
{
    private final double power;
    private final boolean critical;
    private final double criticalLimit;
    
    private MagicalAttackMp(final StatsSet params) {
        this.power = params.getDouble("power");
        this.critical = params.getBoolean("critical");
        this.criticalLimit = params.getDouble("critical-limit");
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        if (effected.isMpBlocked()) {
            return false;
        }
        if (GameUtils.isPlayer((WorldObject)effector) && GameUtils.isPlayer((WorldObject)effected) && effected.isAffected(EffectFlag.DUELIST_FURY) && !effector.isAffected(EffectFlag.DUELIST_FURY)) {
            return false;
        }
        if (!Formulas.calcMagicAffected(effector, effected, skill)) {
            if (GameUtils.isPlayer((WorldObject)effector)) {
                effector.sendPacket(SystemMessageId.YOUR_ATTACK_HAS_FAILED);
            }
            if (GameUtils.isPlayer((WorldObject)effected)) {
                effected.sendPacket(new ServerPacket[] { (ServerPacket)((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.C1_RESISTED_C2_S_DRAIN).addString(effected.getName())).addString(effector.getName()) });
            }
            return false;
        }
        return true;
    }
    
    public EffectType getEffectType() {
        return EffectType.MAGICAL_ATTACK;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effector.isAlikeDead()) {
            return;
        }
        final boolean mcrit = this.critical && Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
        final double damage = Formulas.calcManaDam(effector, effected, skill, this.power, mcrit, this.criticalLimit);
        final double mp = Math.min(effected.getCurrentMp(), damage);
        if (damage > 0.0) {
            effected.stopEffectsOnDamage();
            effected.setCurrentMp(effected.getCurrentMp() - mp);
        }
        if (GameUtils.isPlayer((WorldObject)effected)) {
            effected.sendPacket(new ServerPacket[] { (ServerPacket)((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.S2_S_MP_HAS_BEEN_DRAINED_BY_C1).addString(effector.getName())).addInt((int)mp) });
        }
        if (GameUtils.isPlayer((WorldObject)effector)) {
            effector.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.YOUR_OPPONENT_S_MP_WAS_REDUCED_BY_S1).addInt((int)mp) });
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new MagicalAttackMp(data);
        }
        
        public String effectName() {
            return "magical-attack-mp";
        }
    }
}
