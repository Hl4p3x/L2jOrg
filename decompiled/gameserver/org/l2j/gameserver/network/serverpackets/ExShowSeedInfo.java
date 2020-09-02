// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Seed;
import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.data.database.data.SeedProduction;
import java.util.List;

public class ExShowSeedInfo extends ServerPacket
{
    private final List<SeedProduction> _seeds;
    private final int _manorId;
    private final boolean _hideButtons;
    
    public ExShowSeedInfo(final int manorId, final boolean nextPeriod, final boolean hideButtons) {
        this._manorId = manorId;
        this._hideButtons = hideButtons;
        final CastleManorManager manor = CastleManorManager.getInstance();
        this._seeds = ((nextPeriod && !manor.isManorApproved()) ? null : manor.getSeedProduction(manorId, nextPeriod));
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_SEED_INFO);
        this.writeByte((byte)(byte)(this._hideButtons ? 1 : 0));
        this.writeInt(this._manorId);
        this.writeInt(0);
        if (this._seeds == null) {
            this.writeInt(0);
            return;
        }
        this.writeInt(this._seeds.size());
        for (final SeedProduction seed : this._seeds) {
            this.writeInt(seed.getSeedId());
            this.writeLong(seed.getAmount());
            this.writeLong(seed.getStartAmount());
            this.writeLong(seed.getPrice());
            final Seed s = CastleManorManager.getInstance().getSeed(seed.getSeedId());
            if (s == null) {
                this.writeInt(0);
                this.writeByte((byte)1);
                this.writeInt(0);
                this.writeByte((byte)1);
                this.writeInt(0);
            }
            else {
                this.writeInt(s.getLevel());
                this.writeByte((byte)1);
                this.writeInt(s.getReward(1));
                this.writeByte((byte)1);
                this.writeInt(s.getReward(2));
            }
        }
    }
}
