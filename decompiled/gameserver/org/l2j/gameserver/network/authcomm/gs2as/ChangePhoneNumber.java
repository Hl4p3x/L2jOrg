// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class ChangePhoneNumber extends SendablePacket
{
    private final String _account;
    private final long _phoneNumber;
    
    public ChangePhoneNumber(final String account, final long phoneNumber) {
        this._account = account;
        this._phoneNumber = phoneNumber;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)12);
        this.writeString((CharSequence)this._account);
        this.writeLong(this._phoneNumber);
    }
}
