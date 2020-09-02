// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class PlayerInGame extends SendablePacket
{
    private String[] accounts;
    
    public PlayerInGame(final String... accounts) {
        this.accounts = accounts;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)3);
        this.writeShort((short)this.accounts.length);
        for (final String account : this.accounts) {
            this.writeString((CharSequence)account);
        }
    }
}
