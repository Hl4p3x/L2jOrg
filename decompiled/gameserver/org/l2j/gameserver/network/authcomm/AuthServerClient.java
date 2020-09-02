// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm;

import org.l2j.gameserver.network.authcomm.gs2as.AuthRequest;
import io.github.joealisson.mmocore.Buffer;
import io.github.joealisson.mmocore.WritablePacket;
import io.github.joealisson.mmocore.Connection;
import io.github.joealisson.mmocore.Client;

public class AuthServerClient extends Client<Connection<AuthServerClient>>
{
    AuthServerClient(final Connection<AuthServerClient> connection) {
        super((Connection)connection);
    }
    
    public void sendPacket(final SendablePacket packet) {
        this.writePacket((WritablePacket)packet);
    }
    
    public boolean encrypt(final Buffer data, final int offset, final int size) {
        return true;
    }
    
    public boolean decrypt(final Buffer data, final int offset, final int size) {
        return true;
    }
    
    protected void onDisconnection() {
        AuthServerCommunication.getInstance().restart();
    }
    
    public void onConnected() {
        this.writePacket((WritablePacket)new AuthRequest());
    }
}
