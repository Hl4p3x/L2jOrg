// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class PlayerLogout extends SendablePacket
{
    private String account;
    
    public PlayerLogout(final String account) {
        this.account = account;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)4);
        this.writeString((CharSequence)this.account);
    }
}
