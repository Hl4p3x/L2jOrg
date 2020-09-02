// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import java.util.HashMap;
import org.l2j.gameserver.data.database.data.SeedProduction;
import java.util.Map;
import org.l2j.gameserver.model.Seed;
import java.util.Set;

public class ExShowSeedSetting extends ServerPacket
{
    private final int _manorId;
    private final Set<Seed> _seeds;
    private final Map<Integer, SeedProduction> _current;
    private final Map<Integer, SeedProduction> _next;
    
    public ExShowSeedSetting(final int manorId) {
        this._current = new HashMap<Integer, SeedProduction>();
        this._next = new HashMap<Integer, SeedProduction>();
        final CastleManorManager manor = CastleManorManager.getInstance();
        this._manorId = manorId;
        this._seeds = manor.getSeedsForCastle(this._manorId);
        for (final Seed s : this._seeds) {
            SeedProduction sp = manor.getSeedProduct(manorId, s.getSeedId(), false);
            if (sp != null) {
                this._current.put(s.getSeedId(), sp);
            }
            sp = manor.getSeedProduct(manorId, s.getSeedId(), true);
            if (sp != null) {
                this._next.put(s.getSeedId(), sp);
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_SEED_SETTING);
        this.writeInt(this._manorId);
        this.writeInt(this._seeds.size());
        for (final Seed s : this._seeds) {
            this.writeInt(s.getSeedId());
            this.writeInt(s.getLevel());
            this.writeByte((byte)1);
            this.writeInt(s.getReward(1));
            this.writeByte(1);
            this.writeInt(s.getReward(2));
            this.writeInt(s.getSeedLimit());
            this.writeInt((int)s.getSeedReferencePrice());
            this.writeInt(s.getSeedMinPrice());
            this.writeInt((int)s.getSeedMaxPrice());
            if (this._current.containsKey(s.getSeedId())) {
                final SeedProduction sp = this._current.get(s.getSeedId());
                this.writeLong(sp.getStartAmount());
                this.writeLong(sp.getPrice());
            }
            else {
                this.writeLong(0L);
                this.writeLong(0L);
            }
            if (this._next.containsKey(s.getSeedId())) {
                final SeedProduction sp = this._next.get(s.getSeedId());
                this.writeLong(sp.getStartAmount());
                this.writeLong(sp.getPrice());
            }
            else {
                this.writeLong(0L);
                this.writeLong(0L);
            }
        }
        this._current.clear();
        this._next.clear();
    }
}
