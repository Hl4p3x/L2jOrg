// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.item.container.listener;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.List;
import org.l2j.gameserver.data.xml.impl.ArmorSetsData;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillConditionScope;
import org.l2j.gameserver.model.holders.ArmorsetSkillHolder;
import java.util.function.ToIntFunction;
import org.l2j.gameserver.model.ArmorSet;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.api.item.PlayerInventoryListener;

public final class ArmorSetListener implements PlayerInventoryListener
{
    private static final Logger LOGGER;
    
    private ArmorSetListener() {
    }
    
    private static boolean applySkills(final Player player, final Item item, final ArmorSet armorSet, final ToIntFunction<Item> idProvider) {
        final long piecesCount = armorSet.getPiecesCount(player, idProvider);
        if (piecesCount >= armorSet.getMinimumPieces()) {
            boolean updateTimeStamp = false;
            boolean update = false;
            for (final ArmorsetSkillHolder holder : armorSet.getSkills()) {
                if (holder.validateConditions(player, armorSet, idProvider)) {
                    if (player.getSkillLevel(holder.getSkillId()) >= holder.getLevel()) {
                        continue;
                    }
                    final Skill itemSkill = holder.getSkill();
                    if (itemSkill == null) {
                        ArmorSetListener.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/holders/ArmorsetSkillHolder;)Ljava/lang/String;, holder));
                    }
                    else {
                        if (itemSkill.isPassive() && !itemSkill.checkConditions(SkillConditionScope.PASSIVE, player, player)) {
                            continue;
                        }
                        player.addSkill(itemSkill, false);
                        if (itemSkill.isActive() && item != null) {
                            if (!player.hasSkillReuse(itemSkill.getReuseHashCode())) {
                                final int equipDelay = item.getEquipReuseDelay();
                                if (equipDelay > 0) {
                                    player.addTimeStamp(itemSkill, equipDelay);
                                    player.disableSkill(itemSkill, equipDelay);
                                }
                            }
                            updateTimeStamp = true;
                        }
                        update = true;
                    }
                }
            }
            if (updateTimeStamp) {
                player.sendPacket(new SkillCoolTime(player));
            }
            return update;
        }
        return false;
    }
    
    private static boolean verifyAndApply(final Player player, final Item item, final ToIntFunction<Item> idProvider) {
        boolean update = false;
        final List<ArmorSet> armorSets = ArmorSetsData.getInstance().getSets(idProvider.applyAsInt(item));
        for (final ArmorSet armorSet : armorSets) {
            if (applySkills(player, item, armorSet, idProvider)) {
                update = true;
            }
        }
        return update;
    }
    
    private static boolean verifyAndRemove(final Player player, final Item item, final ToIntFunction<Item> idProvider) {
        boolean update = false;
        final List<ArmorSet> armorSets = ArmorSetsData.getInstance().getSets(idProvider.applyAsInt(item));
        for (final ArmorSet armorSet : armorSets) {
            for (final ArmorsetSkillHolder holder : armorSet.getSkills()) {
                if (!holder.validateConditions(player, armorSet, idProvider)) {
                    final Skill itemSkill = holder.getSkill();
                    if (itemSkill == null) {
                        ArmorSetListener.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/holders/ArmorsetSkillHolder;)Ljava/lang/String;, holder));
                    }
                    else {
                        if (player.removeSkill(itemSkill, false, itemSkill.isPassive()) == null) {
                            continue;
                        }
                        update = true;
                    }
                }
            }
            if (applySkills(player, item, armorSet, idProvider)) {
                update = true;
            }
        }
        return update;
    }
    
    @Override
    public void notifyEquiped(final InventorySlot slot, final Item item, final Inventory inventory) {
        if (!GameUtils.isPlayer(inventory.getOwner())) {
            return;
        }
        final Player player = (Player)inventory.getOwner();
        boolean update = false;
        if (verifyAndApply(player, item, Item::getId)) {
            update = true;
        }
        if (update) {
            player.sendSkillList();
        }
    }
    
    @Override
    public void notifyUnequiped(final InventorySlot slot, final Item item, final Inventory inventory) {
        if (!GameUtils.isPlayer(inventory.getOwner())) {
            return;
        }
        final Player player = (Player)inventory.getOwner();
        boolean remove = false;
        if (verifyAndRemove(player, item, Item::getId)) {
            remove = true;
        }
        if (remove) {
            player.checkItemRestriction();
            player.sendSkillList();
        }
    }
    
    public static ArmorSetListener provider() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ArmorSetListener.class);
    }
    
    private static class Singleton
    {
        private static final ArmorSetListener INSTANCE;
        
        static {
            INSTANCE = new ArmorSetListener();
        }
    }
}
