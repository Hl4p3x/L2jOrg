// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Spoil extends AbstractEffect
{
    private Spoil() {
    }
    
    public boolean calcSuccess(final Creature effector, final Creature effected, final Skill skill) {
        final int lvlDifference = effected.getLevel() - ((skill.getMagicLevel() > 0) ? skill.getMagicLevel() : effector.getLevel());
        final double lvlModifier = Math.pow(1.3, lvlDifference);
        float targetModifier = 1.0f;
        if (GameUtils.isAttackable((WorldObject)effected) && !effected.isRaid() && !effected.isRaidMinion() && effected.getLevel() >= Config.MIN_NPC_LVL_MAGIC_PENALTY && effector.getActingPlayer() != null && effected.getLevel() - effector.getActingPlayer().getLevel() >= 3) {
            final int lvlDiff = effected.getLevel() - effector.getActingPlayer().getLevel() - 2;
            if (lvlDiff >= Config.NPC_SKILL_CHANCE_PENALTY.size()) {
                targetModifier = Config.NPC_SKILL_CHANCE_PENALTY.get(Config.NPC_SKILL_CHANCE_PENALTY.size() - 1);
            }
            else {
                targetModifier = Config.NPC_SKILL_CHANCE_PENALTY.get(lvlDiff);
            }
        }
        return Rnd.get(100) < 100 - Math.round((float)(lvlModifier * targetModifier));
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isMonster((WorldObject)effected) || effected.isDead()) {
            effector.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        final Monster target = (Monster)effected;
        if (target.isSpoiled()) {
            effector.sendPacket(SystemMessageId.IT_HAS_ALREADY_BEEN_SPOILED);
            return;
        }
        target.setSpoilerObjectId(effector.getObjectId());
        effector.sendPacket(SystemMessageId.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
        target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, (Object)effector);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public static final Spoil INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Spoil";
        }
        
        static {
            INSTANCE = new Spoil();
        }
    }
}
