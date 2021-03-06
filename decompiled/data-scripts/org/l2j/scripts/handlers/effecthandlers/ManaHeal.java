// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class ManaHeal extends AbstractEffect
{
    private final double power;
    
    private ManaHeal(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead() || GameUtils.isDoor((WorldObject)effected) || effected.isMpBlocked()) {
            return;
        }
        if (effected != effector && effected.isAffected(EffectFlag.FACEOFF)) {
            return;
        }
        double amount = this.power;
        if (item != null && (item.isPotion() || item.isElixir())) {
            amount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_MP, 0.0);
        }
        if (!skill.isStatic()) {
            amount = effected.getStats().getValue(Stat.MANA_CHARGE, amount);
        }
        amount = Math.max(Math.min(amount, effected.getMaxRecoverableMp() - effected.getCurrentMp()), 0.0);
        if (amount != 0.0) {
            effected.setCurrentMp(effected.getCurrentMp() + amount);
            effected.broadcastStatusUpdate(effector);
        }
        SystemMessage sm;
        if (effector.getObjectId() != effected.getObjectId()) {
            sm = (SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.S2_MP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName());
        }
        else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_MP_HAS_BEEN_RESTORED);
        }
        effected.sendPacket(new ServerPacket[] { (ServerPacket)sm.addInt((int)amount) });
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ManaHeal(data);
        }
        
        public String effectName() {
            return "ManaHeal";
        }
    }
}
