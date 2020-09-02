// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.handler.CommunityBoardHandler;

public final class RequestBBSwrite extends ClientPacket
{
    private String _url;
    private String _arg1;
    private String _arg2;
    private String _arg3;
    private String _arg4;
    private String _arg5;
    
    public final void readImpl() {
        this._url = this.readString();
        this._arg1 = this.readString();
        this._arg2 = this.readString();
        this._arg3 = this.readString();
        this._arg4 = this.readString();
        this._arg5 = this.readString();
    }
    
    public final void runImpl() {
        CommunityBoardHandler.getInstance().handleWriteCommand(((GameClient)this.client).getPlayer(), this._url, this._arg1, this._arg2, this._arg3, this._arg4, this._arg5);
    }
}
