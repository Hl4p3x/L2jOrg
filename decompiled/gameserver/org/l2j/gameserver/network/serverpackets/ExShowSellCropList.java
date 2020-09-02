// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Seed;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import java.util.HashMap;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.data.database.data.CropProcure;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Map;

public final class ExShowSellCropList extends ServerPacket
{
    private final int _manorId;
    private final Map<Integer, Item> _cropsItems;
    private final Map<Integer, CropProcure> _castleCrops;
    
    public ExShowSellCropList(final PlayerInventory inventory, final int manorId) {
        this._cropsItems = new HashMap<Integer, Item>();
        this._castleCrops = new HashMap<Integer, CropProcure>();
        this._manorId = manorId;
        CastleManorManager.getInstance().getCropIds().forEach(cropId -> Util.doIfNonNull((Object)inventory.getItemByItemId(cropId), item -> this._cropsItems.put(cropId, item)));
        for (final CropProcure crop : CastleManorManager.getInstance().getCropProcure(this._manorId, false)) {
            if (this._cropsItems.containsKey(crop.getSeedId()) && crop.getAmount() > 0L) {
                this._castleCrops.put(crop.getSeedId(), crop);
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_SELL_CROP_LIST);
        this.writeInt(this._manorId);
        this.writeInt(this._cropsItems.size());
        for (final Item item : this._cropsItems.values()) {
            final Seed seed = CastleManorManager.getInstance().getSeedByCrop(item.getId());
            this.writeInt(item.getObjectId());
            this.writeInt(item.getId());
            this.writeInt(seed.getLevel());
            this.writeByte((byte)1);
            this.writeInt(seed.getReward(1));
            this.writeByte((byte)1);
            this.writeInt(seed.getReward(2));
            if (this._castleCrops.containsKey(item.getId())) {
                final CropProcure crop = this._castleCrops.get(item.getId());
                this.writeInt(this._manorId);
                this.writeLong(crop.getAmount());
                this.writeLong(crop.getPrice());
                this.writeByte((byte)crop.getReward());
            }
            else {
                this.writeInt(-1);
                this.writeLong(0L);
                this.writeLong(0L);
                this.writeByte((byte)0);
            }
            this.writeLong(item.getCount());
        }
    }
}
