// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class MagicalSoulAttack extends AbstractEffect
{
    private final double power;
    
    private MagicalSoulAttack(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
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
        if (GameUtils.isPlayer((WorldObject)effected) && effected.getActingPlayer().isFakeDeath()) {
            effected.stopFakeDeath(true);
        }
        final int chargedSouls = Math.min(skill.getMaxSoulConsumeCount(), effector.getActingPlayer().getCharges());
        if (!effector.getActingPlayer().decreaseCharges(chargedSouls)) {
            effector.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill) });
            return;
        }
        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
        final double mAtk = effector.getMAtk() * ((chargedSouls > 0) ? (1.3 + chargedSouls * 0.05) : 1.0);
        final double damage = Formulas.calcMagicDam(effector, effected, skill, mAtk, this.power, (double)effected.getMDef(), mcrit);
        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new MagicalSoulAttack(data);
        }
        
        public String effectName() {
            return "MagicalSoulAttack";
        }
    }
}
