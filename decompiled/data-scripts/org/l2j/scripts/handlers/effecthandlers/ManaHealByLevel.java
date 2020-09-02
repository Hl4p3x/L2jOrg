// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class ManaHealByLevel extends AbstractEffect
{
    private final double power;
    
    private ManaHealByLevel(final StatsSet params) {
        this.power = params.getDouble("power", 0.0);
    }
    
    public EffectType getEffectType() {
        return EffectType.MANAHEAL_BY_LEVEL;
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
        amount = effected.getStats().getValue(Stat.MANA_CHARGE, amount);
        if (effected.getLevel() > skill.getMagicLevel()) {
            final int lvlDiff = effected.getLevel() - skill.getMagicLevel();
            if (lvlDiff == 6) {
                amount *= 0.9;
            }
            else if (lvlDiff == 7) {
                amount *= 0.8;
            }
            else if (lvlDiff == 8) {
                amount *= 0.7;
            }
            else if (lvlDiff == 9) {
                amount *= 0.6;
            }
            else if (lvlDiff == 10) {
                amount *= 0.5;
            }
            else if (lvlDiff == 11) {
                amount *= 0.4;
            }
            else if (lvlDiff == 12) {
                amount *= 0.3;
            }
            else if (lvlDiff == 13) {
                amount *= 0.2;
            }
            else if (lvlDiff == 14) {
                amount *= 0.1;
            }
            else if (lvlDiff >= 15) {
                amount = 0.0;
            }
        }
        amount = Math.max(Math.min(amount, effected.getMaxRecoverableMp() - effected.getCurrentMp()), 0.0);
        if (amount != 0.0) {
            effected.setCurrentMp(amount + effected.getCurrentMp(), false);
            effected.broadcastStatusUpdate(effector);
        }
        final SystemMessage sm = SystemMessage.getSystemMessage((effector.getObjectId() != effected.getObjectId()) ? SystemMessageId.S2_MP_HAS_BEEN_RESTORED_BY_C1 : SystemMessageId.S1_MP_HAS_BEEN_RESTORED);
        if (effector.getObjectId() != effected.getObjectId()) {
            sm.addString(effector.getName());
        }
        effected.sendPacket(new ServerPacket[] { (ServerPacket)sm.addInt((int)amount) });
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ManaHealByLevel(data);
        }
        
        public String effectName() {
            return "ManaHealByLevel";
        }
    }
}
