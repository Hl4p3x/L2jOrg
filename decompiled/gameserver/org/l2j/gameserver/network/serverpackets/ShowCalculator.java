// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class ShowCalculator extends ServerPacket
{
    private final int _calculatorId;
    
    public ShowCalculator(final int calculatorId) {
        this._calculatorId = calculatorId;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SHOW_CALC);
        this.writeInt(this._calculatorId);
    }
}
