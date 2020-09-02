// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class TutorialEnableClientEvent extends ServerPacket
{
    private int _eventId;
    
    public TutorialEnableClientEvent(final int event) {
        this._eventId = 0;
        this._eventId = event;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.TUTORIAL_ENABLE_CLIENT_EVENT);
        this.writeInt(this._eventId);
    }
}
