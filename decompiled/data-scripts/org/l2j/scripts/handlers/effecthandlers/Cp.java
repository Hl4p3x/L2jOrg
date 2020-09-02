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
import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Cp extends AbstractEffect
{
    private final int power;
    private final StatModifierType mode;
    
    private Cp(final StatsSet params) {
        this.power = params.getInt("power", 0);
        this.mode = (StatModifierType)params.getEnum("mode", (Class)StatModifierType.class, (Enum)StatModifierType.DIFF);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isDead() || GameUtils.isDoor((WorldObject)effected) || effected.isHpBlocked()) {
            return;
        }
        int basicAmount = this.power;
        if (Objects.nonNull(item) && (item.isPotion() || item.isElixir())) {
            basicAmount += (int)effected.getStats().getValue(Stat.ADDITIONAL_POTION_CP, 0.0);
        }
        double n = 0.0;
        switch (this.mode) {
            case DIFF: {
                n = Math.min(basicAmount, effected.getMaxRecoverableCp() - effected.getCurrentCp());
                break;
            }
            case PER: {
                n = Math.min(effected.getMaxCp() * basicAmount / 100.0, effected.getMaxRecoverableCp() - effected.getCurrentCp());
                break;
            }
            default: {
                throw new IncompatibleClassChangeError();
            }
        }
        final double amount = n;
        if (amount != 0.0) {
            effected.setCurrentCp(amount + effected.getCurrentCp(), false);
            effected.broadcastStatusUpdate(effector);
        }
        if (amount > 0.0) {
            SystemMessage sm;
            if (Objects.nonNull(effector) && effector != effected) {
                sm = (SystemMessage)((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.S2_CP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName())).addInt((int)amount);
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CP_HAS_BEEN_RESTORED);
            }
            effected.sendPacket(new ServerPacket[] { (ServerPacket)sm.addInt((int)amount) });
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new Cp(data);
        }
        
        public String effectName() {
            return "cp";
        }
    }
}
