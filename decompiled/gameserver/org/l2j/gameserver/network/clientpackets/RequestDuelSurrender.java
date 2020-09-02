// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.instancemanager.DuelManager;

public final class RequestDuelSurrender extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        DuelManager.getInstance().doSurrender(((GameClient)this.client).getPlayer());
    }
}
