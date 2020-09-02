// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.instancemanager.CastleManager;
import java.util.HashMap;
import org.l2j.gameserver.data.database.data.CropProcure;
import java.util.Map;

public class ExShowProcureCropDetail extends ServerPacket
{
    private final int _cropId;
    private final Map<Integer, CropProcure> _castleCrops;
    
    public ExShowProcureCropDetail(final int cropId) {
        this._castleCrops = new HashMap<Integer, CropProcure>();
        this._cropId = cropId;
        for (final Castle c : CastleManager.getInstance().getCastles()) {
            final CropProcure cropItem = CastleManorManager.getInstance().getCropProcure(c.getId(), cropId, false);
            if (cropItem != null && cropItem.getAmount() > 0L) {
                this._castleCrops.put(c.getId(), cropItem);
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_PROCURE_CROP_DETAIL);
        this.writeInt(this._cropId);
        this.writeInt(this._castleCrops.size());
        for (final Map.Entry<Integer, CropProcure> entry : this._castleCrops.entrySet()) {
            final CropProcure crop = entry.getValue();
            this.writeInt((int)entry.getKey());
            this.writeLong(crop.getAmount());
            this.writeLong(crop.getPrice());
            this.writeByte((byte)crop.getReward());
        }
    }
}
