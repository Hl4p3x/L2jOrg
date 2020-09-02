// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.SystemMessageId;

public class ConfirmDlg extends AbstractMessagePacket<ConfirmDlg>
{
    private int _time;
    private int _requesterId;
    
    public ConfirmDlg(final SystemMessageId smId) {
        super(smId);
    }
    
    public ConfirmDlg(final String text) {
        this(SystemMessageId.S1);
        this.addString(text);
    }
    
    public ConfirmDlg addTime(final int time) {
        this._time = time;
        return this;
    }
    
    public ConfirmDlg addRequesterId(final int id) {
        this._requesterId = id;
        return this;
    }
    
    @Override
    protected void writeParamsSize(final int size) {
        this.writeInt(size);
    }
    
    @Override
    protected void writeParamType(final int type) {
        this.writeInt(type);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CONFIRM_DLG);
        this.writeInt(this.getId());
        this.writeMe();
        this.writeInt(this._time);
        this.writeInt(this._requesterId);
    }
}
