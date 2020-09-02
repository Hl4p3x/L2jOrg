// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExBrBuffEventState extends ServerPacket
{
    private final int _type;
    private final int _value;
    private final int _state;
    private final int _endtime;
    
    public ExBrBuffEventState(final int type, final int value, final int state, final int endtime) {
        this._type = type;
        this._value = value;
        this._state = state;
        this._endtime = endtime;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BR_BUFF_EVENT_STATE);
        this.writeInt(this._type);
        this.writeInt(this._value);
        this.writeInt(this._state);
        this.writeInt(this._endtime);
    }
}
