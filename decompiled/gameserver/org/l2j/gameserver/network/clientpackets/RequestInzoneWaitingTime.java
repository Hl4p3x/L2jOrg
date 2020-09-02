// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExInzoneWaiting;
import org.l2j.gameserver.network.GameClient;

public class RequestInzoneWaitingTime extends ClientPacket
{
    private boolean _hide;
    
    public void readImpl() {
        this._hide = (this.readChar() == '\0');
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(new ExInzoneWaiting(activeChar, this._hide));
    }
}
