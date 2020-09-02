// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class HpByLevel extends AbstractEffect
{
    private final double power;
    
    private HpByLevel(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final double abs = this.power;
        final double absorb = (effector.getCurrentHp() + abs > effector.getMaxHp()) ? effector.getMaxHp() : (effector.getCurrentHp() + abs);
        final int restored = (int)(absorb - effector.getCurrentHp());
        effector.setCurrentHp(absorb);
        effector.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED).addInt(restored) });
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new HpByLevel(data);
        }
        
        public String effectName() {
            return "HpByLevel";
        }
    }
}
