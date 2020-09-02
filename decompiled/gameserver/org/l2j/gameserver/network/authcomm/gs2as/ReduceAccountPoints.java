// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class ReduceAccountPoints extends SendablePacket
{
    private String account;
    private int count;
    
    public ReduceAccountPoints(final String account, final int count) {
        this.account = account;
        this.count = count;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)18);
        this.writeString((CharSequence)this.account);
        this.writeInt(this.count);
    }
}
