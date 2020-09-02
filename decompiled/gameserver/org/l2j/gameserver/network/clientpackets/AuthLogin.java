// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerAuthRequest;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ServerClose;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.commons.network.SessionKey;
import org.l2j.gameserver.network.GameClient;

public final class AuthLogin extends ClientPacket
{
    private String account;
    private int sessionId;
    private int accountId;
    private int authAccountId;
    private int authKey;
    
    public void readImpl() {
        this.account = this.readString().toLowerCase();
        this.accountId = this.readInt();
        this.sessionId = this.readInt();
        this.authAccountId = this.readInt();
        this.authKey = this.readInt();
    }
    
    public void runImpl() {
        if (this.account.isEmpty() || !((GameClient)this.client).isProtocolOk()) {
            ((GameClient)this.client).closeNow();
            return;
        }
        if (((GameClient)this.client).getAccountName() == null) {
            ((GameClient)this.client).setAccountName(this.account);
            final SessionKey key = new SessionKey(this.authAccountId, this.authKey, this.sessionId, this.accountId);
            ((GameClient)this.client).setSessionId(key);
            final GameClient oldClient = AuthServerCommunication.getInstance().addWaitingClient((GameClient)this.client);
            if (oldClient != null) {
                oldClient.close(ServerClose.STATIC_PACKET);
            }
            AuthServerCommunication.getInstance().sendPacket(new PlayerAuthRequest((GameClient)this.client));
        }
    }
}
