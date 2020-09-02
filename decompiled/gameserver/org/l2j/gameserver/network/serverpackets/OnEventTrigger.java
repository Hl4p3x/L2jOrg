// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class OnEventTrigger extends ServerPacket
{
    private final int _emitterId;
    private final int _enabled;
    
    public OnEventTrigger(final int emitterId, final boolean enabled) {
        this._emitterId = emitterId;
        this._enabled = (enabled ? 1 : 0);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.EVENT_TRIGGER);
        this.writeInt(this._emitterId);
        this.writeByte((byte)this._enabled);
    }
}
