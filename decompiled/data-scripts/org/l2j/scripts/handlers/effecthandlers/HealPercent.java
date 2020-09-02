// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class HealPercent extends AbstractEffect
{
    private final int power;
    
    private HealPercent(final StatsSet params) {
        this.power = params.getInt("power", 0);
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
        final double power = this.power;
        final boolean full = power == 100.0;
        double amount = full ? effected.getMaxHp() : (effected.getMaxHp() * power / 100.0);
        if (Objects.nonNull(item) && (item.isPotion() || item.isElixir())) {
            amount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_HP, 0.0);
        }
        amount = Math.min(amount, effected.getMaxRecoverableHp() - effected.getCurrentHp());
        if (amount >= 0.0) {
            if (amount != 0.0) {
                effected.setCurrentHp(amount + effected.getCurrentHp(), false);
                effected.broadcastStatusUpdate(effector);
            }
            SystemMessage sm;
            if (effector.getObjectId() != effected.getObjectId()) {
                sm = (SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.S2_HP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName());
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED);
            }
            sm.addInt((int)amount);
            effected.sendPacket(new ServerPacket[] { (ServerPacket)sm });
        }
        else {
            final double damage = -amount;
            effected.reduceCurrentHp(damage, effector, skill, false, false, false, false, DamageInfo.DamageType.OTHER);
            effector.sendDamageMessage(effected, skill, (int)damage, 0.0, false, false);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new HealPercent(data);
        }
        
        public String effectName() {
            return "HealPercent";
        }
    }
}
