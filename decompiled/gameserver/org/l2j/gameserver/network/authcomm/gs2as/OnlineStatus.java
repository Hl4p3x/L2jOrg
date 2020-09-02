// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class OnlineStatus extends SendablePacket
{
    private boolean _online;
    
    public OnlineStatus(final boolean online) {
        this._online = online;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte(1);
        this.writeByte(this._online);
    }
}
