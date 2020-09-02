// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Mp extends AbstractEffect
{
    private final int power;
    private final StatModifierType mode;
    
    private Mp(final StatsSet params) {
        this.power = params.getInt("power", 0);
        this.mode = (StatModifierType)params.getEnum("mode", (Class)StatModifierType.class, (Enum)StatModifierType.DIFF);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead() || GameUtils.isDoor((WorldObject)effected) || effected.isMpBlocked()) {
            return;
        }
        int basicAmount = this.power;
        if (item != null && (item.isPotion() || item.isElixir())) {
            basicAmount += (int)effected.getStats().getValue(Stat.ADDITIONAL_POTION_MP, 0.0);
        }
        double n = 0.0;
        switch (this.mode) {
            case DIFF: {
                n = Math.min(basicAmount, effected.getMaxRecoverableMp() - effected.getCurrentMp());
                break;
            }
            case PER: {
                n = Math.min(effected.getMaxMp() * basicAmount / 100.0, effected.getMaxRecoverableMp() - effected.getCurrentMp());
                break;
            }
            default: {
                throw new IncompatibleClassChangeError();
            }
        }
        final double amount = n;
        if (amount >= 0.0) {
            if (amount != 0.0) {
                effected.setCurrentMp(amount + effected.getCurrentMp(), false);
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
        else {
            final double damage = -amount;
            effected.reduceCurrentMp(damage);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Mp(data);
        }
        
        public String effectName() {
            return "mp";
        }
    }
}
