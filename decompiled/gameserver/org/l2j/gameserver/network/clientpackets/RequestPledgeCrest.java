// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.PledgeCrest;
import org.l2j.gameserver.network.GameClient;

public final class RequestPledgeCrest extends ClientPacket
{
    private int _crestId;
    
    public void readImpl() {
        this._crestId = this.readInt();
        this.readInt();
    }
    
    public void runImpl() {
        ((GameClient)this.client).sendPacket(new PledgeCrest(this._crestId));
    }
}
