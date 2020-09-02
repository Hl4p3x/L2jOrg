// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.commons.util.StreamUtil;
import java.util.function.ToIntFunction;
import java.util.Arrays;
import org.l2j.gameserver.model.StatsSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class BlockAction extends AbstractEffect
{
    private IntSet blockedActions;
    
    private BlockAction(final StatsSet params) {
        this.blockedActions = StreamUtil.collectToSet(Arrays.stream(params.getString("actions").split(" ")).mapToInt(Integer::parseInt));
    }
    
    public boolean canStart(final Creature effector, final Creature effected, final Skill skill) {
        return GameUtils.isPlayer((WorldObject)effected);
    }
    
    public boolean checkCondition(final Object id) {
        return !(id instanceof Integer) || !this.blockedActions.contains((int)id);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (this.blockedActions.contains(-3)) {
            PunishmentManager.getInstance().startPunishment(new PunishmentTask(0, (Object)effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.PARTY_BAN, 0L, "block action debuff", "system", true));
        }
        if (this.blockedActions.contains(-5)) {
            PunishmentManager.getInstance().startPunishment(new PunishmentTask(0, (Object)effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, 0L, "block action debuff", "system", true));
        }
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        if (this.blockedActions.contains(-3)) {
            PunishmentManager.getInstance().stopPunishment((Object)effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.PARTY_BAN);
        }
        if (this.blockedActions.contains(-5)) {
            PunishmentManager.getInstance().stopPunishment((Object)effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new BlockAction(data);
        }
        
        public String effectName() {
            return "block-action";
        }
    }
}
