// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import java.util.List;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class PetFood implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (GameUtils.isPet((WorldObject)playable) && !((Pet)playable).canEatFoodId(item.getId())) {
            playable.sendPacket(SystemMessageId.THIS_PET_CANNOT_USE_THIS_ITEM);
            return false;
        }
        final List<ItemSkillHolder> skills = (List<ItemSkillHolder>)item.getSkills(ItemSkillType.NORMAL);
        if (skills != null) {
            skills.forEach(holder -> this.useFood(playable, holder.getSkillId(), holder.getLevel(), item));
        }
        return true;
    }
    
    private boolean useFood(final Playable activeChar, final int skillId, final int skillLevel, final Item item) {
        final Skill skill = SkillEngine.getInstance().getSkill(skillId, skillLevel);
        if (skill != null) {
            if (GameUtils.isPet((WorldObject)activeChar)) {
                final Pet pet = (Pet)activeChar;
                if (pet.destroyItem("Consume", item.getObjectId(), 1L, (WorldObject)null, false)) {
                    pet.broadcastPacket((ServerPacket)new MagicSkillUse((Creature)pet, (WorldObject)pet, skillId, skillLevel, 0, 0));
                    skill.applyEffects((Creature)pet, (Creature)pet);
                    pet.broadcastStatusUpdate();
                    if (pet.isHungry()) {
                        pet.sendPacket(SystemMessageId.YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY);
                    }
                    return true;
                }
            }
            else if (GameUtils.isPlayer((WorldObject)activeChar)) {
                final Player player = activeChar.getActingPlayer();
                if (player.isMounted()) {
                    final List<Integer> foodIds = (List<Integer>)PetDataTable.getInstance().getPetData(player.getMountNpcId()).getFood();
                    if (foodIds.contains(item.getId()) && player.destroyItem("Consume", item.getObjectId(), 1L, (WorldObject)null, false)) {
                        player.broadcastPacket((ServerPacket)new MagicSkillUse((Creature)player, (WorldObject)player, skillId, skillLevel, 0, 0));
                        skill.applyEffects((Creature)player, (Creature)player);
                        return true;
                    }
                }
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
                sm.addItemName(item);
                player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            }
        }
        return false;
    }
}
