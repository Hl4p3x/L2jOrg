// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class PlayerAuthRequest extends SendablePacket
{
    private String account;
    private int playOkID1;
    private int playOkID2;
    private int loginOkID1;
    private int loginOkID2;
    
    public PlayerAuthRequest(final GameClient client) {
        this.account = client.getAccountName();
        this.playOkID1 = client.getSessionId().getGameServerSessionId();
        this.playOkID2 = client.getSessionId().getGameServerAccountId();
        this.loginOkID1 = client.getSessionId().getAuthAccountId();
        this.loginOkID2 = client.getSessionId().getAuthKey();
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte(2);
        this.writeString((CharSequence)this.account);
        this.writeInt(this.playOkID1);
        this.writeInt(this.playOkID2);
        this.writeInt(this.loginOkID1);
        this.writeInt(this.loginOkID2);
    }
}
