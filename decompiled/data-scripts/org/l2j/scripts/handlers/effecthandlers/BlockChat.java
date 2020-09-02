// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class BlockChat extends AbstractEffect
{
    private BlockChat() {
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return GameUtils.isPlayer((WorldObject)effected);
    }
    
    public long getEffectFlags() {
        return EffectFlag.CHAT_BLOCK.getMask();
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        PunishmentManager.getInstance().startPunishment(new PunishmentTask(0, (Object)effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, 0L, "Chat banned bot report", "system", true));
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        PunishmentManager.getInstance().stopPunishment((Object)effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final BlockChat INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "BlockChat";
        }
        
        static {
            INSTANCE = new BlockChat();
        }
    }
}
