// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class ChangeAllowedIp extends SendablePacket
{
    private String account;
    private String ip;
    
    public ChangeAllowedIp(final String account, final String ip) {
        this.account = account;
        this.ip = ip;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)7);
        this.writeString((CharSequence)this.account);
        this.writeString((CharSequence)this.ip);
    }
}
