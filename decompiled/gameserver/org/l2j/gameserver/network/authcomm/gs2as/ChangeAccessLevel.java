// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class ChangeAccessLevel extends SendablePacket
{
    private String account;
    private int level;
    private int banExpire;
    
    public ChangeAccessLevel(final String account, final int level, final int banExpire) {
        this.account = account;
        this.level = level;
        this.banExpire = banExpire;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)17);
        this.writeString((CharSequence)this.account);
        this.writeInt(this.level);
        this.writeInt(this.banExpire);
    }
}
