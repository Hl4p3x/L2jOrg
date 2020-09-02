// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExVariationResult extends ServerPacket
{
    private final int _option1;
    private final int _option2;
    private final int _success;
    
    public ExVariationResult(final int option1, final int option2, final boolean success) {
        this._option1 = option1;
        this._option2 = option2;
        this._success = (success ? 1 : 0);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_VARIATION_RESULT);
        this.writeInt(this._option1);
        this.writeInt(this._option2);
        this.writeInt(this._success);
    }
}
