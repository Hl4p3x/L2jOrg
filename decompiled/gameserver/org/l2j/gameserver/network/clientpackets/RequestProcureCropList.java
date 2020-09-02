// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.holders.UniqueItemHolder;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.data.database.data.CropProcure;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.Config;
import java.util.List;

public class RequestProcureCropList extends ClientPacket
{
    private static final int BATCH_LENGTH = 20;
    private List<CropHolder> _items;
    
    public RequestProcureCropList() {
        this._items = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        final int count = this.readInt();
        if (count <= 0 || count > Config.MAX_ITEM_IN_PACKET || count * 20 != this.available()) {
            throw new InvalidDataPacketException();
        }
        this._items = new ArrayList<CropHolder>(count);
        for (int i = 0; i < count; ++i) {
            final int objId = this.readInt();
            final int itemId = this.readInt();
            final int manorId = this.readInt();
            final long cnt = this.readLong();
            if (objId < 1 || itemId < 1 || manorId < 0 || cnt < 0L) {
                this._items = null;
                throw new InvalidDataPacketException();
            }
            this._items.add(new CropHolder(objId, itemId, cnt, manorId));
        }
    }
    
    public void runImpl() {
        if (this._items == null) {
            return;
        }
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final CastleManorManager manor = CastleManorManager.getInstance();
        if (manor.isUnderMaintenance()) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Npc manager = player.getLastFolkNPC();
        if (!(manager instanceof Merchant) || !manager.canInteract(player)) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final int castleId = manager.getCastle().getId();
        if (manager.getParameters().getInt("manor_id", -1) != castleId) {
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        int slots = 0;
        int weight = 0;
        for (final CropHolder i : this._items) {
            final Item item = player.getInventory().getItemByObjectId(i.getObjectId());
            if (item == null || item.getCount() < i.getCount() || item.getId() != i.getId()) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            final CropProcure cp = i.getCropProcure();
            if (cp == null || cp.getAmount() < i.getCount()) {
                ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(i.getRewardId());
            weight += (int)(i.getCount() * template.getWeight());
            if (!template.isStackable()) {
                slots += (int)i.getCount();
            }
            else {
                if (player.getInventory().getItemByItemId(i.getRewardId()) != null) {
                    continue;
                }
                ++slots;
            }
        }
        if (!player.getInventory().validateWeight(weight)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return;
        }
        if (!player.getInventory().validateCapacity(slots)) {
            player.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            return;
        }
        final int updateListSize = 0;
        final List<CropProcure> updateList = new ArrayList<CropProcure>(0);
        for (final CropHolder j : this._items) {
            final long rewardPrice = ItemEngine.getInstance().getTemplate(j.getRewardId()).getReferencePrice();
            if (rewardPrice == 0L) {
                continue;
            }
            final long rewardItemCount = j.getPrice() / rewardPrice;
            if (rewardItemCount < 1L) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.FAILED_IN_TRADING_S2_OF_S1_CROPS);
                sm.addItemName(j.getId());
                sm.addLong(j.getCount());
                player.sendPacket(sm);
            }
            else {
                final long fee = (castleId == j.getManorId()) ? 0L : ((long)(j.getPrice() * 0.05));
                if (fee != 0L && player.getAdena() < fee) {
                    SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.FAILED_IN_TRADING_S2_OF_S1_CROPS);
                    sm2.addItemName(j.getId());
                    sm2.addLong(j.getCount());
                    player.sendPacket(sm2);
                    sm2 = SystemMessage.getSystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
                    player.sendPacket(sm2);
                }
                else {
                    final CropProcure cp2 = j.getCropProcure();
                    if (!cp2.decreaseAmount(j.getCount()) || (fee > 0L && !player.reduceAdena("Manor", fee, manager, true))) {
                        continue;
                    }
                    if (!player.destroyItem("Manor", j.getObjectId(), j.getCount(), manager, true)) {
                        continue;
                    }
                    player.addItem("Manor", j.getRewardId(), rewardItemCount, manager, true);
                }
            }
        }
    }
    
    private final class CropHolder extends UniqueItemHolder
    {
        private final int _manorId;
        private CropProcure _cp;
        private int _rewardId;
        
        public CropHolder(final int objectId, final int id, final long count, final int manorId) {
            super(id, objectId, count);
            this._rewardId = 0;
            this._manorId = manorId;
        }
        
        public final int getManorId() {
            return this._manorId;
        }
        
        public final long getPrice() {
            return this.getCount() * this._cp.getPrice();
        }
        
        public final CropProcure getCropProcure() {
            if (this._cp == null) {
                this._cp = CastleManorManager.getInstance().getCropProcure(this._manorId, this.getId(), false);
            }
            return this._cp;
        }
        
        public final int getRewardId() {
            if (this._rewardId == 0) {
                this._rewardId = CastleManorManager.getInstance().getSeedByCrop(this._cp.getSeedId()).getReward(this._cp.getReward());
            }
            return this._rewardId;
        }
    }
}
