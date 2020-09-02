// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class PingResponse extends SendablePacket
{
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)(-1));
        this.writeLong(System.currentTimeMillis());
    }
}
