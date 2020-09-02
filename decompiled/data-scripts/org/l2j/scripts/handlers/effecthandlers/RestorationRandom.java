// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Map;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.Config;
import java.util.HashMap;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Collection;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.ArrayList;
import org.l2j.gameserver.model.holders.RestorationItemHolder;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.ExtractableProductItem;
import java.util.List;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class RestorationRandom extends AbstractEffect
{
    private final List<ExtractableProductItem> products;
    
    private RestorationRandom(final StatsSet params) {
        if (params.contains("id")) {
            final RestorationItemHolder item = new RestorationItemHolder(params.getInt("id"), (long)params.getInt("count"), 0, 0);
            this.products = List.of(new ExtractableProductItem((List)List.of(item), (double)params.getInt("chance")));
        }
        else {
            this.products = new ArrayList<ExtractableProductItem>();
            StatsSet set;
            RestorationItemHolder item2;
            params.getSet().forEach((key, value) -> {
                if (key.startsWith("item")) {
                    set = value;
                    item2 = new RestorationItemHolder(set.getInt("id"), (long)set.getInt("count"), 0, 0);
                    this.products.add(new ExtractableProductItem((List)List.of(item2), (double)set.getInt("chance")));
                }
            });
        }
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final double rndNum = 100.0 * Rnd.nextDouble();
        double chanceFrom = 0.0;
        final List<RestorationItemHolder> creationList = new ArrayList<RestorationItemHolder>();
        for (final ExtractableProductItem expi : this.products) {
            final double chance = expi.getChance();
            if (rndNum >= chanceFrom && rndNum <= chance + chanceFrom) {
                creationList.addAll(expi.getItems());
                break;
            }
            chanceFrom += chance;
        }
        final Player player = effected.getActingPlayer();
        if (creationList.isEmpty()) {
            player.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
            return;
        }
        final Map<Item, Long> extractedItems = new HashMap<Item, Long>();
        for (final RestorationItemHolder createdItem : creationList) {
            if (createdItem.getId() > 0) {
                if (createdItem.getCount() <= 0L) {
                    continue;
                }
                final long itemCount = (long)(createdItem.getCount() * Config.RATE_EXTRACTABLE);
                final Item newItem = player.addItem("Extract", createdItem.getId(), itemCount, (WorldObject)effector, false);
                if (Objects.nonNull(newItem) && createdItem.getMaxEnchant() > 0) {
                    newItem.setEnchantLevel(Rnd.get(createdItem.getMinEnchant(), createdItem.getMaxEnchant()));
                }
                if (Objects.nonNull(extractedItems.get(newItem))) {
                    extractedItems.put(newItem, extractedItems.get(newItem) + itemCount);
                }
                else {
                    extractedItems.put(newItem, itemCount);
                }
            }
        }
        if (!extractedItems.isEmpty()) {
            final InventoryUpdate playerIU = new InventoryUpdate();
            for (final Map.Entry<Item, Long> entry : extractedItems.entrySet()) {
                if (entry.getKey().isStackable()) {
                    playerIU.addModifiedItem((Item)entry.getKey());
                }
                else {
                    for (final Item itemInstance : player.getInventory().getItemsByItemId(entry.getKey().getId())) {
                        playerIU.addModifiedItem(itemInstance);
                    }
                }
                this.sendMessage(player, entry.getKey(), entry.getValue());
            }
            player.sendPacket(new ServerPacket[] { (ServerPacket)playerIU });
        }
    }
    
    public EffectType getEffectType() {
        return EffectType.EXTRACT_ITEM;
    }
    
    private void sendMessage(final Player player, final Item item, final Long count) {
        SystemMessage sm;
        if (count > 1L) {
            sm = (SystemMessage)((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S2_S1).addItemName(item)).addLong((long)count);
        }
        else if (item.getEnchantLevel() > 0) {
            sm = (SystemMessage)((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_A_S1_S2).addInt(item.getEnchantLevel())).addItemName(item);
        }
        else {
            sm = (SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1).addItemName(item);
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new RestorationRandom(data);
        }
        
        public String effectName() {
            return "random-restoration";
        }
    }
}
