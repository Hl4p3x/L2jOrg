// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.HennaRemoveList;
import org.l2j.gameserver.network.GameClient;

public final class RequestHennaRemoveList extends ClientPacket
{
    private int _unknown;
    
    public void readImpl() {
        this._unknown = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new HennaRemoveList(activeChar));
    }
}
