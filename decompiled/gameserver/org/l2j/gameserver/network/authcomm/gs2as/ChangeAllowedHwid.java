// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class ChangeAllowedHwid extends SendablePacket
{
    private String account;
    private String hwid;
    
    public ChangeAllowedHwid(final String account, final String hwid) {
        this.account = account;
        this.hwid = hwid;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)9);
        this.writeString((CharSequence)this.account);
        this.writeString((CharSequence)this.hwid);
    }
}
