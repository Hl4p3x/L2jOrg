// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.luckygame;

import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import org.l2j.gameserver.model.holders.LuckyGameDataHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import java.util.Map;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.enums.LuckyGameResultType;
import org.l2j.gameserver.model.holders.ItemPointHolder;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.item.ItemEngine;
import java.util.ArrayList;
import org.l2j.gameserver.model.holders.ItemChanceHolder;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import java.util.EnumMap;
import org.l2j.gameserver.enums.LuckyGameItemType;
import org.l2j.gameserver.network.serverpackets.luckygame.ExBettingLuckyGameResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.impl.LuckyGameData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.enums.LuckyGameType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestLuckyGamePlay extends ClientPacket
{
    private static final int FORTUNE_READING_TICKET = 23767;
    private static final int LUXURY_FORTUNE_READING_TICKET = 23768;
    private LuckyGameType _type;
    private int _reading;
    
    public void readImpl() {
        final int type = CommonUtil.constrain(this.readInt(), 0, LuckyGameType.values().length);
        this._type = LuckyGameType.values()[type];
        this._reading = CommonUtil.constrain(this.readInt(), 0, 50);
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final int index = (this._type == LuckyGameType.LUXURY) ? 102 : 2;
        final LuckyGameDataHolder holder = LuckyGameData.getInstance().getLuckyGameDataByIndex(index);
        if (holder == null) {
            return;
        }
        final long tickets = (this._type == LuckyGameType.LUXURY) ? player.getInventory().getInventoryItemCount(23768, -1) : player.getInventory().getInventoryItemCount(23767, -1);
        if (tickets < this._reading) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_TICKETS_YOU_CANNOT_CONTINUE_THE_GAME);
            player.sendPacket((this._type == LuckyGameType.LUXURY) ? ExBettingLuckyGameResult.LUXURY_INVALID_ITEM_COUNT : ExBettingLuckyGameResult.NORMAL_INVALID_ITEM_COUNT);
            return;
        }
        int playCount = player.getFortuneTelling();
        boolean blackCat = player.isFortuneTellingBlackCat();
        final EnumMap<LuckyGameItemType, List<ItemHolder>> rewards = new EnumMap<LuckyGameItemType, List<ItemHolder>>(LuckyGameItemType.class);
        for (int i = 0; i < this._reading; ++i) {
            final double chance = 100.0 * Rnd.nextDouble();
            double totalChance = 0.0;
            for (final ItemChanceHolder item2 : holder.getCommonReward()) {
                totalChance += item2.getChance();
                if (totalChance >= chance) {
                    rewards.computeIfAbsent(LuckyGameItemType.COMMON, k -> new ArrayList()).add(item2);
                    break;
                }
            }
            if (++playCount >= holder.getMinModifyRewardGame() && playCount <= holder.getMaxModifyRewardGame() && !blackCat) {
                final List<ItemChanceHolder> modifyReward = holder.getModifyReward();
                final double chanceModify = 100.0 * Rnd.nextDouble();
                totalChance = 0.0;
                for (final ItemChanceHolder item3 : modifyReward) {
                    totalChance += item3.getChance();
                    if (totalChance >= chanceModify) {
                        rewards.computeIfAbsent(LuckyGameItemType.RARE, k -> new ArrayList()).add(item3);
                        blackCat = true;
                        break;
                    }
                }
                if (playCount == holder.getMaxModifyRewardGame()) {
                    rewards.computeIfAbsent(LuckyGameItemType.RARE, k -> new ArrayList()).add(modifyReward.get(Rnd.get(modifyReward.size())));
                    blackCat = true;
                }
            }
        }
        final int totalWeight = rewards.values().stream().mapToInt(list -> list.stream().mapToInt(item -> ItemEngine.getInstance().getTemplate(item.getId()).getWeight()).sum()).sum();
        if (rewards.size() > 0 && (!player.getInventory().validateCapacity(rewards.size()) || !player.getInventory().validateWeight(totalWeight))) {
            player.sendPacket((this._type == LuckyGameType.LUXURY) ? ExBettingLuckyGameResult.LUXURY_INVALID_CAPACITY : ExBettingLuckyGameResult.NORMAL_INVALID_CAPACITY);
            player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_EITHER_FULL_OR_OVERWEIGHT);
            return;
        }
        if (!player.destroyItemByItemId("LuckyGame", (this._type == LuckyGameType.LUXURY) ? 23768 : 23767, this._reading, player, true)) {
            player.sendPacket((this._type == LuckyGameType.LUXURY) ? ExBettingLuckyGameResult.LUXURY_INVALID_ITEM_COUNT : ExBettingLuckyGameResult.NORMAL_INVALID_ITEM_COUNT);
            return;
        }
        for (int j = 0; j < this._reading; ++j) {
            final int serverGameNumber = LuckyGameData.getInstance().increaseGame();
            holder.getUniqueReward().stream().filter(reward -> reward.getPoints() == serverGameNumber).forEach(item -> rewards.computeIfAbsent(LuckyGameItemType.UNIQUE, k -> new ArrayList()).add(item));
        }
        player.sendPacket(new ExBettingLuckyGameResult(LuckyGameResultType.SUCCESS, this._type, rewards, (int)((this._type == LuckyGameType.LUXURY) ? player.getInventory().getInventoryItemCount(23768, -1) : player.getInventory().getInventoryItemCount(23767, -1))));
        final InventoryUpdate iu = new InventoryUpdate();
        for (final Map.Entry<LuckyGameItemType, List<ItemHolder>> reward2 : rewards.entrySet()) {
            for (final ItemHolder r : reward2.getValue()) {
                final Item item4 = player.addItem("LuckyGame", r.getId(), r.getCount(), player, true);
                iu.addItem(item4);
                if (reward2.getKey() == LuckyGameItemType.UNIQUE) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CONGRATULATIONS_C1_HAS_OBTAINED_S2_OF_S3_THROUGH_FORTUNE_READING);
                    sm.addPcName(player);
                    sm.addLong(r.getCount());
                    sm.addItemName(item4);
                    player.broadcastPacket(sm, 1000);
                    break;
                }
            }
        }
        player.sendInventoryUpdate(iu);
        player.setFortuneTelling((playCount >= 50) ? (playCount - 50) : playCount);
        if (blackCat && playCount < 50) {
            player.setFortuneTellingBlackCat(true);
        }
    }
}
