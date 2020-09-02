// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PingResponse;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;

public class PingRequest extends ReceivablePacket
{
    public void readImpl() {
    }
    
    @Override
    protected void runImpl() {
        AuthServerCommunication.getInstance().sendPacket(new PingResponse());
    }
}
