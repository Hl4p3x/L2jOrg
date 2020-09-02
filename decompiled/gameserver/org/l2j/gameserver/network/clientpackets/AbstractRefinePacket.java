// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.EnchantItemAttributeRequest;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.PrivateStoreType;
import java.util.Arrays;
import org.l2j.gameserver.model.item.Armor;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.options.VariationFee;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;

public abstract class AbstractRefinePacket extends ClientPacket
{
    protected static boolean isValid(final Player player, final Item item, final Item mineralItem, final Item feeItem, final VariationFee fee) {
        return fee != null && isValid(player, item, mineralItem) && feeItem.getOwnerId() == player.getObjectId() && feeItem.getItemLocation() == ItemLocation.INVENTORY && fee.getItemId() == feeItem.getId() && fee.getItemCount() <= feeItem.getCount();
    }
    
    protected static boolean isValid(final Player player, final Item item, final Item mineralItem) {
        return isValid(player, item) && mineralItem.getOwnerId() == player.getObjectId() && mineralItem.getItemLocation() == ItemLocation.INVENTORY;
    }
    
    protected static boolean isValid(final Player player, final Item item) {
        if (!isValid(player)) {
            return false;
        }
        if (item.getOwnerId() != player.getObjectId()) {
            return false;
        }
        if (item.isAugmented()) {
            return false;
        }
        if (item.isHeroItem()) {
            return false;
        }
        if (item.isCommonItem()) {
            return false;
        }
        if (item.isEtcItem()) {
            return false;
        }
        if (item.isTimeLimitedItem()) {
            return false;
        }
        if (item.isPvp() && !Config.ALT_ALLOW_AUGMENT_PVP_ITEMS) {
            return false;
        }
        switch (item.getItemLocation()) {
            case INVENTORY:
            case PAPERDOLL: {
                return (item.getTemplate() instanceof Weapon || item.getTemplate() instanceof Armor) && Arrays.binarySearch(Config.AUGMENTATION_BLACKLIST, item.getId()) < 0;
            }
            default: {
                return false;
            }
        }
    }
    
    protected static boolean isValid(final Player player) {
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION);
            return false;
        }
        if (player.getActiveTradeList() != null) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_ENGAGED_IN_TRADE_ACTIVITIES);
            return false;
        }
        if (player.isDead()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD);
            return false;
        }
        if (player.hasBlockActions() && player.hasAbnormalType(AbnormalType.PARALYZE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED);
            return false;
        }
        if (player.isFishing()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING);
            return false;
        }
        if (player.isSitting()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN);
            return false;
        }
        return !player.hasRequest(EnchantItemRequest.class, EnchantItemAttributeRequest.class) && !player.isProcessingTransaction();
    }
}
