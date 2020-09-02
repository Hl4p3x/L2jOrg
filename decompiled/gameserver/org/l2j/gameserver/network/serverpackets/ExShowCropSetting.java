// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import java.util.HashMap;
import org.l2j.gameserver.data.database.data.CropProcure;
import java.util.Map;
import org.l2j.gameserver.model.Seed;
import java.util.Set;

public class ExShowCropSetting extends ServerPacket
{
    private final int _manorId;
    private final Set<Seed> _seeds;
    private final Map<Integer, CropProcure> _current;
    private final Map<Integer, CropProcure> _next;
    
    public ExShowCropSetting(final int manorId) {
        this._current = new HashMap<Integer, CropProcure>();
        this._next = new HashMap<Integer, CropProcure>();
        final CastleManorManager manor = CastleManorManager.getInstance();
        this._manorId = manorId;
        this._seeds = manor.getSeedsForCastle(this._manorId);
        for (final Seed s : this._seeds) {
            CropProcure cp = manor.getCropProcure(manorId, s.getCropId(), false);
            if (cp != null) {
                this._current.put(s.getCropId(), cp);
            }
            cp = manor.getCropProcure(manorId, s.getCropId(), true);
            if (cp != null) {
                this._next.put(s.getCropId(), cp);
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_CROP_SETTING);
        this.writeInt(this._manorId);
        this.writeInt(this._seeds.size());
        for (final Seed s : this._seeds) {
            this.writeInt(s.getCropId());
            this.writeInt(s.getLevel());
            this.writeByte((byte)1);
            this.writeInt(s.getReward(1));
            this.writeByte((byte)1);
            this.writeInt(s.getReward(2));
            this.writeInt(s.getCropLimit());
            this.writeInt(0);
            this.writeInt(s.getCropMinPrice());
            this.writeInt((int)s.getCropMaxPrice());
            if (this._current.containsKey(s.getCropId())) {
                final CropProcure cp = this._current.get(s.getCropId());
                this.writeLong(cp.getStartAmount());
                this.writeLong(cp.getPrice());
                this.writeByte((byte)cp.getReward());
            }
            else {
                this.writeLong(0L);
                this.writeLong(0L);
                this.writeByte((byte)0);
            }
            if (this._next.containsKey(s.getCropId())) {
                final CropProcure cp = this._next.get(s.getCropId());
                this.writeLong(cp.getStartAmount());
                this.writeLong(cp.getPrice());
                this.writeByte((byte)cp.getReward());
            }
            else {
                this.writeLong(0L);
                this.writeLong(0L);
                this.writeByte((byte)0);
            }
        }
        this._next.clear();
        this._current.clear();
    }
}
