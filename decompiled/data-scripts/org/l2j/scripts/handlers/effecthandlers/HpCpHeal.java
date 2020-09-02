// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.serverpackets.ExMagicAttackInfo;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class HpCpHeal extends AbstractEffect
{
    private final double power;
    
    private HpCpHeal(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
    }
    
    public EffectType getEffectType() {
        return EffectType.HEAL;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead() || GameUtils.isDoor((WorldObject)effected) || effected.isHpBlocked()) {
            return;
        }
        if (effected != effector && effected.isAffected(EffectFlag.FACEOFF)) {
            return;
        }
        final double amount = this.calcHealAmount(effector, effected, skill);
        if (amount <= 0.0) {
            return;
        }
        final double healAmount = Math.max(Math.min(amount, effected.getMaxRecoverableHp() - effected.getCurrentHp()), 0.0);
        if (healAmount != 0.0) {
            effected.setCurrentHp(healAmount + effected.getCurrentHp(), false);
            if (GameUtils.isPlayer((WorldObject)effected)) {
                this.sendMessage(effector, effected, (int)healAmount, SystemMessageId.S2_HP_HAS_BEEN_RESTORED_BY_C1, SystemMessageId.S1_HP_HAS_BEEN_RESTORED);
            }
        }
        if (GameUtils.isPlayer((WorldObject)effected) && healAmount < amount) {
            final double cpAmount = Math.max(Math.min(amount - healAmount, effected.getMaxRecoverableCp() - effected.getCurrentCp()), 0.0);
            if (cpAmount > 0.0) {
                effected.setCurrentCp(cpAmount + effected.getCurrentCp(), false);
                this.sendMessage(effector, effected, (int)amount, SystemMessageId.S2_CP_HAS_BEEN_RESTORED_BY_C1, SystemMessageId.S1_CP_HAS_BEEN_RESTORED);
            }
        }
        effected.broadcastStatusUpdate(effector);
    }
    
    private void sendMessage(final Creature effector, final Creature effected, final int healAmount, final SystemMessageId msgRestoredByOther, final SystemMessageId msgRestored) {
        if (GameUtils.isPlayer((WorldObject)effector) && effector != effected) {
            effected.sendPacket(new ServerPacket[] { (ServerPacket)((SystemMessage)SystemMessage.getSystemMessage(msgRestoredByOther).addString(effector.getName())).addInt(healAmount) });
        }
        else {
            effected.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(msgRestored).addInt(healAmount) });
        }
    }
    
    private double calcHealAmount(final Creature effector, final Creature effected, final Skill skill) {
        double amount = this.power;
        double staticShotBonus = 0.0;
        double mAtkMul = skill.isMagic() ? effector.chargedShotBonus(ShotType.SPIRITSHOTS) : 1.0;
        if (mAtkMul > 1.0 && ((GameUtils.isPlayer((WorldObject)effector) && effector.getActingPlayer().isMageClass()) || GameUtils.isSummon((WorldObject)effector))) {
            staticShotBonus = skill.getMpConsume();
            staticShotBonus *= ((mAtkMul >= 4.0) ? 2.4 : 1.0);
        }
        else if (mAtkMul > 1.0 && GameUtils.isNpc((WorldObject)effector)) {
            staticShotBonus = 2.4 * skill.getMpConsume();
        }
        else {
            mAtkMul = ((mAtkMul >= 4.0) ? (mAtkMul * 4.0) : (mAtkMul + 1.0));
        }
        if (!skill.isStatic()) {
            amount += staticShotBonus + Math.sqrt(mAtkMul * effector.getMAtk());
            amount *= effected.getStats().getValue(Stat.HEAL_RECEIVE, 1.0);
            amount = effector.getStats().getValue(Stat.HEAL_POWER, amount);
            if (skill.isMagic() && (Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill) || effector.isAffected(EffectFlag.HPCPHEAL_CRITICAL))) {
                amount *= 3.0;
                effector.sendPacket(SystemMessageId.M_CRITICAL);
                effector.sendPacket(new ServerPacket[] { (ServerPacket)new ExMagicAttackInfo(effector.getObjectId(), effected.getObjectId(), 2) });
                if (GameUtils.isPlayer((WorldObject)effected) && effected != effector) {
                    effected.sendPacket(new ServerPacket[] { (ServerPacket)new ExMagicAttackInfo(effector.getObjectId(), effected.getObjectId(), 2) });
                }
            }
        }
        return amount;
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new HpCpHeal(data);
        }
        
        public String effectName() {
            return "HpCpHeal";
        }
    }
}
