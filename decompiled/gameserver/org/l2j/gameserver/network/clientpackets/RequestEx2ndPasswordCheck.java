// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordCheck;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.xml.SecondaryAuthManager;

public class RequestEx2ndPasswordCheck extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        if (!SecondaryAuthManager.getInstance().isEnabled() || ((GameClient)this.client).isSecondaryAuthed()) {
            ((GameClient)this.client).sendPacket(new Ex2ndPasswordCheck(2));
            return;
        }
        ((GameClient)this.client).openSecondaryAuthDialog();
    }
}
