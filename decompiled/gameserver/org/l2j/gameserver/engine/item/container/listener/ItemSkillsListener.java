// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.item.container.listener;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import java.util.Iterator;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillConditionScope;
import org.l2j.gameserver.enums.ItemSkillType;
import java.util.concurrent.atomic.AtomicBoolean;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.InventorySlot;
import org.slf4j.Logger;
import org.l2j.gameserver.api.item.PlayerInventoryListener;

public final class ItemSkillsListener implements PlayerInventoryListener
{
    private static final Logger LOGGER;
    
    private ItemSkillsListener() {
    }
    
    @Override
    public void notifyUnequiped(final InventorySlot slot, final Item item, final Inventory inventory) {
        if (!GameUtils.isPlayer(inventory.getOwner())) {
            return;
        }
        final Player player = (Player)inventory.getOwner();
        final ItemTemplate it = item.getTemplate();
        final AtomicBoolean update = new AtomicBoolean();
        final AtomicBoolean updateTimestamp = new AtomicBoolean();
        if (item.isAugmented()) {
            item.getAugmentation().removeBonus(player);
        }
        player.getStats().recalculateStats(true);
        final Player player2;
        final AtomicBoolean atomicBoolean;
        it.forEachSkill(ItemSkillType.ON_ENCHANT, holder -> {
            if (this.verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }
            else {
                player2.removeSkill(holder.getSkill(), false, holder.getSkill().isPassive());
                atomicBoolean.compareAndSet(false, true);
                return;
            }
        });
        item.clearEnchantStats();
        item.clearSpecialAbilities();
        Skill Skill;
        final Player player3;
        final AtomicBoolean atomicBoolean2;
        it.forEachSkill(ItemSkillType.NORMAL, holder -> {
            if (this.verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }
            else {
                Skill = holder.getSkill();
                if (Skill != null) {
                    player3.removeSkill(Skill, false, Skill.isPassive());
                    atomicBoolean2.compareAndSet(false, true);
                }
                else {
                    ItemSkillsListener.LOGGER.warn("Incorrect skill: {}", (Object)holder);
                }
                return;
            }
        });
        if (item.isArmor()) {
            for (final Item itm : inventory.getItems()) {
                if (itm.isEquipped() && itm.getSkills(ItemSkillType.NORMAL) != null) {
                    if (itm.equals(item)) {
                        continue;
                    }
                    final Player player4;
                    Skill skill;
                    final AtomicBoolean update2;
                    final AtomicBoolean updateTimestamp2;
                    itm.getTemplate().forEachSkill(ItemSkillType.NORMAL, holder -> {
                        if (this.verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                            return;
                        }
                        else if (player4.getSkillLevel(holder.getSkillId()) != 0) {
                            return;
                        }
                        else {
                            skill = holder.getSkill();
                            if (skill != null) {
                                this.applySkillOnPlayer(item, skill, player4, update2, updateTimestamp2);
                            }
                            return;
                        }
                    });
                }
            }
        }
        Skill skill2;
        final Player player5;
        final AtomicBoolean atomicBoolean3;
        inventory.forEachEquippedItem(equipped -> equipped.forEachSkill(ItemSkillType.ON_ENCHANT, holder -> {
            if (equipped.getEnchantLevel() >= holder.getValue()) {
                skill2 = holder.getSkill();
                if (skill2.isPassive() && !skill2.checkConditions(SkillConditionScope.PASSIVE, player5, player5)) {
                    player5.removeSkill(holder.getSkill(), false, holder.getSkill().isPassive());
                    atomicBoolean3.compareAndSet(false, true);
                }
            }
        }));
        for (final Skill skill3 : player.getAllSkills()) {
            if (skill3.isToggle() && player.isAffectedBySkill(skill3.getId()) && !skill3.checkConditions(SkillConditionScope.GENERAL, player, player)) {
                player.stopSkillEffects(true, skill3.getId());
                update.compareAndSet(false, true);
            }
        }
        final Creature caster;
        it.forEachSkill(ItemSkillType.ON_UNEQUIP, holder -> {
            if (this.verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }
            else {
                holder.getSkill().activateSkill(caster, caster);
                return;
            }
        });
        if (update.get()) {
            player.sendSkillList();
        }
        if (updateTimestamp.get()) {
            player.sendPacket(new SkillCoolTime(player));
        }
        if (item.isWeapon()) {
            player.unchargeAllShots();
        }
    }
    
    private void applySkillOnPlayer(final Item item, final Skill skill, final Player player, final AtomicBoolean update, final AtomicBoolean updateTimestamp) {
        player.addSkill(skill, false);
        if (skill.isActive() && !player.hasSkillReuse(skill.getReuseHashCode())) {
            final int equipDelay = item.getEquipReuseDelay();
            if (equipDelay > 0) {
                player.addTimeStamp(skill, equipDelay);
                player.disableSkill(skill, equipDelay);
            }
            updateTimestamp.compareAndSet(false, true);
        }
        update.compareAndSet(false, true);
    }
    
    @Override
    public void notifyEquiped(final InventorySlot slot, final Item item, final Inventory inventory) {
        if (!GameUtils.isPlayer(inventory.getOwner())) {
            return;
        }
        final Player player = (Player)inventory.getOwner();
        final AtomicBoolean update = new AtomicBoolean();
        final AtomicBoolean updateTimestamp = new AtomicBoolean();
        if (item.isAugmented()) {
            item.getAugmentation().applyBonus(player);
        }
        player.getStats().recalculateStats(true);
        final Player player2;
        final AtomicBoolean update2;
        item.getTemplate().forEachSkill(ItemSkillType.ON_ENCHANT, holder -> {
            if (this.verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }
            else if (player2.getSkillLevel(holder.getSkillId()) >= holder.getLevel()) {
                return;
            }
            else {
                this.applyEnchantSkill(item, player2, holder, update2);
                return;
            }
        });
        item.applyEnchantStats();
        item.applySpecialAbilities();
        final Player player3;
        Skill skill;
        final AtomicBoolean update3;
        final AtomicBoolean updateTimestamp2;
        item.getTemplate().forEachSkill(ItemSkillType.NORMAL, holder -> {
            if (this.verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }
            else if (player3.getSkillLevel(holder.getSkillId()) >= holder.getLevel()) {
                return;
            }
            else {
                skill = holder.getSkill();
                if (skill != null) {
                    if (!skill.isPassive() || skill.checkConditions(SkillConditionScope.PASSIVE, player3, player3)) {
                        this.applySkillOnPlayer(item, skill, player3, update3, updateTimestamp2);
                    }
                }
                else {
                    ItemSkillsListener.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/holders/ItemSkillHolder;)Ljava/lang/String;, holder));
                }
                return;
            }
        });
        inventory.forEachEquippedItem(equipped -> equipped.forEachSkill(ItemSkillType.ON_ENCHANT, holder -> this.applyEnchantSkill(equipped, player, holder, update)));
        final Creature caster;
        item.getTemplate().forEachSkill(ItemSkillType.ON_EQUIP, holder -> {
            if (this.verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }
            else {
                holder.getSkill().activateSkill(caster, caster);
                return;
            }
        });
        if (update.get()) {
            player.sendSkillList();
        }
        if (updateTimestamp.get()) {
            player.sendPacket(new SkillCoolTime(player));
        }
    }
    
    private void applyEnchantSkill(final Item item, final Player player, final ItemSkillHolder holder, final AtomicBoolean update) {
        if (player.getSkillLevel(holder.getSkillId()) >= holder.getLevel()) {
            return;
        }
        if (item.getEnchantLevel() >= holder.getValue()) {
            final Skill skill = holder.getSkill();
            if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player)) {
                return;
            }
            player.addSkill(skill, false);
            update.compareAndSet(false, true);
        }
    }
    
    private boolean verifySkillActiveIfAddtionalAgathion(final InventorySlot slot, final ItemSkillHolder holder) {
        return slot != InventorySlot.AGATHION1 && InventorySlot.agathions().contains(slot) && holder.getSkill().isActive();
    }
    
    public static ItemSkillsListener provider() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ItemSkillsListener.class);
    }
    
    private static final class Singleton
    {
        private static final ItemSkillsListener INSTANCE;
        
        static {
            INSTANCE = new ItemSkillsListener();
        }
    }
}
