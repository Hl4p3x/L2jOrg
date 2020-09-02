// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.costume.ExSendCostumeListFull;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestCostumeList extends ClientPacket
{
    private int type;
    
    @Override
    protected void readImpl() throws Exception {
        this.type = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        ((GameClient)this.client).sendPacket(new ExSendCostumeListFull());
    }
}
