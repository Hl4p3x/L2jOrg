// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import java.util.Iterator;
import java.util.Set;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import java.util.function.Consumer;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class ConvertItem extends AbstractEffect
{
    private ConvertItem() {
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effected.isAlikeDead() || !GameUtils.isPlayer((WorldObject)effected)) {
            return;
        }
        final Player player = effected.getActingPlayer();
        if (player.hasItemRequest()) {
            return;
        }
        final Weapon weaponItem = player.getActiveWeaponItem();
        if (Objects.isNull(weaponItem)) {
            return;
        }
        Item wpn = player.getInventory().getPaperdollItem(InventorySlot.RIGHT_HAND);
        if (Objects.isNull(wpn)) {
            wpn = player.getInventory().getPaperdollItem(InventorySlot.LEFT_HAND);
        }
        if (!GameUtils.isWeapon(wpn) || wpn.isAugmented() || weaponItem.getChangeWeaponId() == 0) {
            return;
        }
        final int newItemId = weaponItem.getChangeWeaponId();
        if (newItemId == -1) {
            return;
        }
        final int enchantLevel = wpn.getEnchantLevel();
        final AttributeHolder elementals = Objects.isNull(wpn.getAttributes()) ? null : wpn.getAttackAttribute();
        final Set<Item> unequiped = (Set<Item>)player.getInventory().unEquipItemInBodySlotAndRecord(wpn.getBodyPart());
        if (unequiped.size() <= 0) {
            return;
        }
        final InventoryUpdate iu = new InventoryUpdate();
        final Set<Item> set = unequiped;
        final InventoryUpdate obj = iu;
        Objects.requireNonNull(obj);
        set.forEach(obj::addModifiedItem);
        player.sendInventoryUpdate(iu);
        byte count = 0;
        for (final Item unequippedItem : unequiped) {
            if (!GameUtils.isWeapon(unequippedItem)) {
                ++count;
            }
            else {
                SystemMessage sm;
                if (unequippedItem.getEnchantLevel() > 0) {
                    sm = (SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(unequippedItem.getEnchantLevel());
                }
                else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
                }
                player.sendPacket(new ServerPacket[] { (ServerPacket)sm.addItemName(unequippedItem) });
            }
        }
        if (count == unequiped.size()) {
            return;
        }
        final Item destroyItem = player.getInventory().destroyItem("ChangeWeapon", wpn, player, (Object)null);
        if (Objects.isNull(destroyItem)) {
            return;
        }
        final Item newItem = player.getInventory().addItem("ChangeWeapon", newItemId, 1L, player, (Object)destroyItem);
        if (Objects.isNull(newItem)) {
            return;
        }
        if (Objects.nonNull(elementals)) {
            newItem.setAttribute(elementals, true);
        }
        newItem.setEnchantLevel(enchantLevel);
        player.getInventory().equipItem(newItem);
        SystemMessage msg;
        if (newItem.getEnchantLevel() > 0) {
            msg = (SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.EQUIPPED_S1_S2).addInt(newItem.getEnchantLevel());
        }
        else {
            msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)msg.addItemName(newItem) });
        final InventoryUpdate u = new InventoryUpdate();
        u.addRemovedItem(destroyItem);
        u.addItem(newItem);
        player.sendInventoryUpdate(u);
        player.broadcastUserInfo();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final ConvertItem INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "ConvertItem";
        }
        
        static {
            INSTANCE = new ConvertItem();
        }
    }
}
