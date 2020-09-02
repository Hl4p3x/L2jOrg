// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import java.util.ArrayList;
import org.l2j.gameserver.data.database.data.SeedProduction;
import java.util.List;

public final class BuyListSeed extends ServerPacket
{
    private final int _manorId;
    private final long _money;
    private final List<SeedProduction> _list;
    
    public BuyListSeed(final long currentMoney, final int castleId) {
        this._list = new ArrayList<SeedProduction>();
        this._money = currentMoney;
        this._manorId = castleId;
        for (final SeedProduction s : CastleManorManager.getInstance().getSeedProduction(castleId, false)) {
            if (s.getAmount() > 0L && s.getPrice() > 0L) {
                this._list.add(s);
            }
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.BUY_LIST_SEED);
        this.writeLong(this._money);
        this.writeInt(0);
        this.writeInt(this._manorId);
        if (!this._list.isEmpty()) {
            this.writeShort((short)this._list.size());
            for (final SeedProduction s : this._list) {
                this.writeByte((byte)0);
                this.writeInt(s.getSeedId());
                this.writeInt(s.getSeedId());
                this.writeByte((byte)(-1));
                this.writeLong(s.getAmount());
                this.writeByte((byte)5);
                this.writeByte((byte)0);
                this.writeShort((short)0);
                this.writeLong(0L);
                this.writeShort((short)0);
                this.writeInt(-1);
                this.writeInt(-9999);
                this.writeByte((byte)1);
                this.writeLong(s.getPrice());
            }
            this._list.clear();
        }
        else {
            this.writeShort((short)0);
        }
    }
}
