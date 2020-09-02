// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class SetAccountInfo extends SendablePacket
{
    private String _account;
    private int _size;
    
    public SetAccountInfo(final String account, final int size) {
        this._account = account;
        this._size = size;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)5);
        this.writeString((CharSequence)this._account);
        this.writeByte((byte)this._size);
    }
}
