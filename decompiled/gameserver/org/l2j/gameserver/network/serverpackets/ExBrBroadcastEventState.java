// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExBrBroadcastEventState extends ServerPacket
{
    public static final int APRIL_FOOLS = 20090401;
    public static final int EVAS_INFERNO = 20090801;
    public static final int HALLOWEEN_EVENT = 20091031;
    public static final int RAISING_RUDOLPH = 20091225;
    public static final int LOVERS_JUBILEE = 20100214;
    private final int _eventId;
    private final int _eventState;
    private int _param0;
    private int _param1;
    private int _param2;
    private int _param3;
    private int _param4;
    private String _param5;
    private String _param6;
    
    public ExBrBroadcastEventState(final int eventId, final int eventState) {
        this._eventId = eventId;
        this._eventState = eventState;
    }
    
    public ExBrBroadcastEventState(final int eventId, final int eventState, final int param0, final int param1, final int param2, final int param3, final int param4, final String param5, final String param6) {
        this._eventId = eventId;
        this._eventState = eventState;
        this._param0 = param0;
        this._param1 = param1;
        this._param2 = param2;
        this._param3 = param3;
        this._param4 = param4;
        this._param5 = param5;
        this._param6 = param6;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BR_BROADCAST_EVENT_STATE);
        this.writeInt(this._eventId);
        this.writeInt(this._eventState);
        this.writeInt(this._param0);
        this.writeInt(this._param1);
        this.writeInt(this._param2);
        this.writeInt(this._param3);
        this.writeInt(this._param4);
        this.writeString((CharSequence)this._param5);
        this.writeString((CharSequence)this._param6);
    }
}
