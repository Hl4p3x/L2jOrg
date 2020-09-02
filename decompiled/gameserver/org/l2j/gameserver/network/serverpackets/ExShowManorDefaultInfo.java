// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.model.Seed;
import java.util.List;

public final class ExShowManorDefaultInfo extends ServerPacket
{
    private final List<Seed> _crops;
    private final boolean _hideButtons;
    
    public ExShowManorDefaultInfo(final boolean hideButtons) {
        this._crops = CastleManorManager.getInstance().getCrops();
        this._hideButtons = hideButtons;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_MANOR_DEFAULT_INFO);
        this.writeByte((byte)(byte)(this._hideButtons ? 1 : 0));
        this.writeInt(this._crops.size());
        for (final Seed crop : this._crops) {
            this.writeInt(crop.getCropId());
            this.writeInt(crop.getLevel());
            this.writeInt((int)crop.getSeedReferencePrice());
            this.writeInt((int)crop.getCropReferencePrice());
            this.writeByte((byte)1);
            this.writeInt(crop.getReward(1));
            this.writeByte((byte)1);
            this.writeInt(crop.getReward(2));
        }
    }
}
