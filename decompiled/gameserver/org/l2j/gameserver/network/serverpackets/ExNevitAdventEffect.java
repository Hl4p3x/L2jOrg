// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExNevitAdventEffect extends ServerPacket
{
    private final int _timeLeft;
    
    public ExNevitAdventEffect(final int timeLeft) {
        this._timeLeft = timeLeft;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_CHANNELING_EFFECT);
        this.writeInt(this._timeLeft);
    }
}
