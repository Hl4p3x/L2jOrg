// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.Config;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.ExtractableProduct;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.WorldObject;
import java.util.Objects;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class ExtractableItems implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        final ItemTemplate template = item.getTemplate();
        final EtcItem etcItem;
        if (!(template instanceof EtcItem) || (etcItem = (EtcItem)template) != (EtcItem)template) {
            return false;
        }
        final Player player;
        if (!(playable instanceof Player) || (player = (Player)playable) != (Player)playable) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        final List<ExtractableProduct> extractables = (List<ExtractableProduct>)etcItem.getExtractableItems();
        if (Objects.isNull(extractables)) {
            ExtractableItems.LOGGER.info("No extractable data defined for {}", (Object)etcItem);
            return false;
        }
        if (!player.isInventoryUnder80(false)) {
            player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
            return false;
        }
        if (!player.destroyItem("Extract", item.getObjectId(), 1L, (WorldObject)player, true)) {
            return false;
        }
        final InventoryUpdate inventoryUpdate = new InventoryUpdate();
        int extracted = 0;
        for (final ExtractableProduct product : extractables) {
            if (etcItem.getMaxExtractable() > 0 && extracted >= etcItem.getMaxExtractable()) {
                break;
            }
            if (!Rnd.chance(product.chance())) {
                continue;
            }
            final int min = (int)(product.min() * Config.RATE_EXTRACTABLE);
            final int max = (int)(product.max() * Config.RATE_EXTRACTABLE);
            final int amount = Rnd.get(min, max);
            if (amount == 0) {
                continue;
            }
            final int enchant = Rnd.get(product.minEnchant(), product.maxEnchant());
            final Item extractedItem = player.addItem("Extract", product.id(), (long)amount, enchant, (WorldObject)item, true, false);
            if (!Objects.nonNull(extractedItem)) {
                continue;
            }
            ++extracted;
            inventoryUpdate.addItem(extractedItem);
        }
        if (extracted == 0) {
            player.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
        }
        else {
            player.sendInventoryUpdate(inventoryUpdate);
        }
        return true;
    }
}
