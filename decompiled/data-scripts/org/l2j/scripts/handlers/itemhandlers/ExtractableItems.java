// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.network.serverpackets.SystemMessage;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.Config;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.ExtractableProduct;
import java.util.ArrayList;
import java.util.HashMap;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class ExtractableItems implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        final Player activeChar = playable.getActingPlayer();
        final EtcItem etcitem = (EtcItem)item.getTemplate();
        final List<ExtractableProduct> exitems = (List<ExtractableProduct>)etcitem.getExtractableItems();
        if (exitems == null) {
            ExtractableItems.LOGGER.info(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/item/EtcItem;)Ljava/lang/String;, etcitem));
            return false;
        }
        if (!activeChar.isInventoryUnder80(false)) {
            activeChar.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
            return false;
        }
        if (!activeChar.destroyItem("Extract", item.getObjectId(), 1L, (WorldObject)activeChar, true)) {
            return false;
        }
        final Map<Item, Long> extractedItems = new HashMap<Item, Long>();
        final List<Item> enchantedItems = new ArrayList<Item>();
        if (etcitem.getExtractableCountMin() > 0) {
            while (extractedItems.size() < etcitem.getExtractableCountMin()) {
                for (final ExtractableProduct expi : exitems) {
                    if (etcitem.getExtractableCountMax() > 0 && extractedItems.size() == etcitem.getExtractableCountMax()) {
                        break;
                    }
                    if (Rnd.get(100000) > expi.getChance()) {
                        continue;
                    }
                    final int min = (int)(expi.getMin() * Config.RATE_EXTRACTABLE);
                    final int max = (int)(expi.getMax() * Config.RATE_EXTRACTABLE);
                    int createItemAmount = (max == min) ? min : (Rnd.get(max - min + 1) + min);
                    if (createItemAmount == 0) {
                        continue;
                    }
                    boolean alreadyExtracted = false;
                    for (final Item i : extractedItems.keySet()) {
                        if (i.getTemplate().getId() == expi.getId()) {
                            alreadyExtracted = true;
                            break;
                        }
                    }
                    if (alreadyExtracted && exitems.size() >= etcitem.getExtractableCountMax()) {
                        continue;
                    }
                    if (ItemEngine.getInstance().getTemplate(expi.getId()).isStackable() || createItemAmount == 1) {
                        final Item newItem = activeChar.addItem("Extract", expi.getId(), (long)createItemAmount, (WorldObject)activeChar, false);
                        if (expi.getMaxEnchant() > 0) {
                            newItem.setEnchantLevel(Rnd.get(expi.getMinEnchant(), expi.getMaxEnchant()));
                            enchantedItems.add(newItem);
                        }
                        this.addItem(extractedItems, newItem);
                    }
                    else {
                        while (createItemAmount > 0) {
                            final Item newItem = activeChar.addItem("Extract", expi.getId(), 1L, (WorldObject)activeChar, false);
                            if (expi.getMaxEnchant() > 0) {
                                newItem.setEnchantLevel(Rnd.get(expi.getMinEnchant(), expi.getMaxEnchant()));
                                enchantedItems.add(newItem);
                            }
                            this.addItem(extractedItems, newItem);
                            --createItemAmount;
                        }
                    }
                }
            }
        }
        else {
            for (final ExtractableProduct expi : exitems) {
                if (etcitem.getExtractableCountMax() > 0 && extractedItems.size() == etcitem.getExtractableCountMax()) {
                    break;
                }
                if (Rnd.get(100000) > expi.getChance()) {
                    continue;
                }
                final int min = (int)(expi.getMin() * Config.RATE_EXTRACTABLE);
                final int max = (int)(expi.getMax() * Config.RATE_EXTRACTABLE);
                int createItemAmount = (max == min) ? min : (Rnd.get(max - min + 1) + min);
                if (createItemAmount == 0) {
                    continue;
                }
                if (ItemEngine.getInstance().getTemplate(expi.getId()).isStackable() || createItemAmount == 1) {
                    final Item newItem2 = activeChar.addItem("Extract", expi.getId(), (long)createItemAmount, (WorldObject)activeChar, false);
                    if (expi.getMaxEnchant() > 0) {
                        newItem2.setEnchantLevel(Rnd.get(expi.getMinEnchant(), expi.getMaxEnchant()));
                        enchantedItems.add(newItem2);
                    }
                    this.addItem(extractedItems, newItem2);
                }
                else {
                    while (createItemAmount > 0) {
                        final Item newItem2 = activeChar.addItem("Extract", expi.getId(), 1L, (WorldObject)activeChar, false);
                        if (expi.getMaxEnchant() > 0) {
                            newItem2.setEnchantLevel(Rnd.get(expi.getMinEnchant(), expi.getMaxEnchant()));
                            enchantedItems.add(newItem2);
                        }
                        this.addItem(extractedItems, newItem2);
                        --createItemAmount;
                    }
                }
            }
        }
        if (extractedItems.isEmpty()) {
            activeChar.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
        }
        if (!enchantedItems.isEmpty()) {
            final InventoryUpdate playerIU = new InventoryUpdate();
            for (final Item j : enchantedItems) {
                playerIU.addModifiedItem(j);
            }
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)playerIU });
        }
        for (final Item k : extractedItems.keySet()) {
            this.sendMessage(activeChar, k, extractedItems.get(k));
        }
        return true;
    }
    
    private void addItem(final Map<Item, Long> extractedItems, final Item newItem) {
        if (extractedItems.get(newItem) != null) {
            extractedItems.put(newItem, extractedItems.get(newItem) + 1L);
        }
        else {
            extractedItems.put(newItem, 1L);
        }
    }
    
    private void sendMessage(final Player player, final Item item, final Long count) {
        SystemMessage sm;
        if (count > 1L) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S2_S1);
            sm.addItemName(item);
            sm.addLong((long)count);
        }
        else if (item.getEnchantLevel() > 0) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_A_S1_S2);
            sm.addInt(item.getEnchantLevel());
            sm.addItemName(item);
        }
        else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1);
            sm.addItemName(item);
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
    }
}
