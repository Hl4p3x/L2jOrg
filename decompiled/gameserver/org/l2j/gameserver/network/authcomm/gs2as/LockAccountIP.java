// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class LockAccountIP extends SendablePacket
{
    String _account;
    String _IP;
    int _time;
    
    public LockAccountIP(final String account, final String IP, final int time) {
        this._account = account;
        this._IP = IP;
        this._time = time;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)11);
        this.writeString((CharSequence)this._account);
        this.writeString((CharSequence)this._IP);
        this.writeInt(this._time);
    }
}
