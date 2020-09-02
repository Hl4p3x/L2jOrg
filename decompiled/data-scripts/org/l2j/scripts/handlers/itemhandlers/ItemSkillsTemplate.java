// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import java.util.Objects;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import java.util.Collection;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class ItemSkillsTemplate implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable) && !GameUtils.isPet((WorldObject)playable)) {
            return false;
        }
        if (GameUtils.isPet((WorldObject)playable) && !item.isTradeable()) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        if (!this.checkReuse(playable, null, item)) {
            return false;
        }
        final List<ItemSkillHolder> skills = (List<ItemSkillHolder>)item.getSkills(ItemSkillType.NORMAL);
        if (Util.isNullOrEmpty((Collection)skills)) {
            ItemSkillsTemplate.LOGGER.info("Item {} does not have registered any skill for handler.", (Object)item);
            return false;
        }
        boolean hasConsumeSkill = false;
        boolean successfulUse = false;
        for (final ItemSkillHolder skillInfo : skills) {
            if (Objects.isNull(skillInfo)) {
                continue;
            }
            final Skill itemSkill = skillInfo.getSkill();
            if (!Objects.nonNull(itemSkill)) {
                continue;
            }
            final Player player = playable.getActingPlayer();
            if (itemSkill.hasAnyEffectType(new EffectType[] { EffectType.EXTRACT_ITEM }) && Objects.nonNull(player) && !playable.getActingPlayer().isInventoryUnder80(false)) {
                player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return false;
            }
            if (itemSkill.getItemConsumeId() > 0) {
                hasConsumeSkill = true;
            }
            if (!itemSkill.hasAnyEffectType(new EffectType[] { EffectType.SUMMON_PET }) && !itemSkill.checkCondition((Creature)playable, playable.getTarget())) {
                continue;
            }
            if (playable.isSkillDisabled(itemSkill)) {
                continue;
            }
            if (!this.checkReuse(playable, itemSkill, item)) {
                continue;
            }
            if (!item.isPotion() && !item.isElixir() && !item.isScroll() && playable.isCastingNow()) {
                continue;
            }
            if (GameUtils.isPet((WorldObject)playable)) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_USES_S1);
                sm.addSkillName(itemSkill);
                playable.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            }
            if (GameUtils.isPlayer((WorldObject)playable) && itemSkill.hasAnyEffectType(new EffectType[] { EffectType.SUMMON_PET })) {
                playable.doCast(itemSkill);
                successfulUse = true;
            }
            else if (itemSkill.isWithoutAction() || item.getTemplate().hasImmediateEffect() || item.getTemplate().hasExImmediateEffect()) {
                SkillCaster.triggerCast((Creature)playable, (WorldObject)null, itemSkill, item, false);
                successfulUse = true;
            }
            else {
                playable.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                if (!playable.useMagic(itemSkill, item, forceUse, false)) {
                    continue;
                }
                successfulUse = true;
            }
            if (itemSkill.getReuseDelay() <= 0) {
                continue;
            }
            playable.addTimeStamp(itemSkill, (long)itemSkill.getReuseDelay());
        }
        if (successfulUse && this.checkConsume(item, hasConsumeSkill) && !playable.destroyItem("Consume", item.getObjectId(), 1L, (WorldObject)playable, false)) {
            playable.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            return false;
        }
        return successfulUse;
    }
    
    private boolean checkConsume(final Item item, final boolean hasConsumeSkill) {
        switch (item.getAction()) {
            case CAPSULE:
            case SKILL_REDUCE: {
                if (!hasConsumeSkill && item.getTemplate().hasImmediateEffect()) {
                    return true;
                }
                break;
            }
            case SKILL_REDUCE_ON_SKILL_SUCCESS: {
                return false;
            }
        }
        return hasConsumeSkill;
    }
    
    private boolean checkReuse(final Playable playable, final Skill skill, final Item item) {
        final long remainingTime = (skill != null) ? playable.getSkillRemainingReuseTime(skill.getReuseHashCode()) : playable.getItemRemainingReuseTime(item.getObjectId());
        final boolean isAvailable = remainingTime <= 0L;
        if (GameUtils.isPlayer((WorldObject)playable) && !isAvailable) {
            final int hours = (int)(remainingTime / 3600000L);
            final int minutes = (int)(remainingTime % 3600000L) / 60000;
            final int seconds = (int)(remainingTime / 1000L % 60L);
            SystemMessage sm = null;
            if (hours > 0) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_HOUR_S_S3_MINUTE_S_AND_S4_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                if (skill == null || skill.isStatic()) {
                    sm.addItemName(item);
                }
                else {
                    sm.addSkillName(skill);
                }
                sm.addInt(hours);
                sm.addInt(minutes);
            }
            else if (minutes > 0) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_MINUTE_S_S3_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                if (skill == null || skill.isStatic()) {
                    sm.addItemName(item);
                }
                else {
                    sm.addSkillName(skill);
                }
                sm.addInt(minutes);
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                if (skill == null || skill.isStatic()) {
                    sm.addItemName(item);
                }
                else {
                    sm.addSkillName(skill);
                }
            }
            sm.addInt(seconds);
            playable.sendPacket(new ServerPacket[] { (ServerPacket)sm });
        }
        return isAvailable;
    }
}
