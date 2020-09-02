// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Seed;
import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.data.database.data.CropProcure;
import java.util.List;

public class ExShowCropInfo extends ServerPacket
{
    private final List<CropProcure> _crops;
    private final int _manorId;
    private final boolean _hideButtons;
    
    public ExShowCropInfo(final int manorId, final boolean nextPeriod, final boolean hideButtons) {
        this._manorId = manorId;
        this._hideButtons = hideButtons;
        final CastleManorManager manor = CastleManorManager.getInstance();
        this._crops = ((nextPeriod && !manor.isManorApproved()) ? null : manor.getCropProcure(manorId, nextPeriod));
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_CROP_INFO);
        this.writeByte((byte)(byte)(this._hideButtons ? 1 : 0));
        this.writeInt(this._manorId);
        this.writeInt(0);
        if (this._crops != null) {
            this.writeInt(this._crops.size());
            for (final CropProcure crop : this._crops) {
                this.writeInt(crop.getSeedId());
                this.writeLong(crop.getAmount());
                this.writeLong(crop.getStartAmount());
                this.writeLong(crop.getPrice());
                this.writeByte((byte)crop.getReward());
                final Seed seed = CastleManorManager.getInstance().getSeedByCrop(crop.getSeedId());
                if (seed == null) {
                    this.writeInt(0);
                    this.writeByte((byte)1);
                    this.writeInt(0);
                    this.writeByte((byte)1);
                    this.writeInt(0);
                }
                else {
                    this.writeInt(seed.getLevel());
                    this.writeByte((byte)1);
                    this.writeInt(seed.getReward(1));
                    this.writeByte((byte)1);
                    this.writeInt(seed.getReward(2));
                }
            }
        }
    }
}
