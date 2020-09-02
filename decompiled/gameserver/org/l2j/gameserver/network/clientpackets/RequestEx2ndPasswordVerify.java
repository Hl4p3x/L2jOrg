// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.xml.SecondaryAuthManager;

public class RequestEx2ndPasswordVerify extends ClientPacket
{
    private String password;
    
    public void readImpl() {
        this.password = this.readString();
    }
    
    public void runImpl() {
        if (!SecondaryAuthManager.getInstance().isEnabled()) {
            return;
        }
        ((GameClient)this.client).checkPassword(this.password, false);
    }
}
