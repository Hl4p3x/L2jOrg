// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
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
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class CpHealPercent extends AbstractEffect
{
    private final double power;
    
    private CpHealPercent(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead() || GameUtils.isDoor((WorldObject)effected) || effected.isHpBlocked()) {
            return;
        }
        final boolean full = this.power == 100.0;
        double amount = full ? effected.getMaxCp() : (effected.getMaxCp() * this.power / 100.0);
        if (Objects.nonNull(item) && (item.isPotion() || item.isElixir())) {
            amount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_CP, 0.0);
        }
        amount = Math.max(Math.min(amount, effected.getMaxRecoverableCp() - effected.getCurrentCp()), 0.0);
        if (amount != 0.0) {
            effected.setCurrentCp(amount + effected.getCurrentCp(), false);
            effected.broadcastStatusUpdate(effector);
        }
        SystemMessage sm;
        if (Objects.nonNull(effector) && effector != effected) {
            sm = (SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.S2_CP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName());
        }
        else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CP_HAS_BEEN_RESTORED);
        }
        effected.sendPacket(new ServerPacket[] { (ServerPacket)sm.addInt((int)amount) });
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new CpHealPercent(data);
        }
        
        public String effectName() {
            return "CpHealPercent";
        }
    }
}
