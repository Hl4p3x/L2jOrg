// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.CrestData;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExPledgeEmblem;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.sql.impl.CrestTable;

public final class RequestExPledgeCrestLarge extends ClientPacket
{
    private int _crestId;
    private int _clanId;
    
    public void readImpl() {
        this._crestId = this.readInt();
        this._clanId = this.readInt();
    }
    
    public void runImpl() {
        final CrestData crest = CrestTable.getInstance().getCrest(this._crestId);
        final byte[] data = (byte[])((crest != null) ? crest.getData() : null);
        if (data != null) {
            for (int i = 0; i <= 4; ++i) {
                final int size = Math.max(Math.min(14336, data.length - 14336 * i), 0);
                if (size != 0) {
                    final byte[] chunk = new byte[size];
                    System.arraycopy(data, 14336 * i, chunk, 0, size);
                    ((GameClient)this.client).sendPacket(new ExPledgeEmblem(this._crestId, chunk, this._clanId, i));
                }
            }
        }
    }
}
