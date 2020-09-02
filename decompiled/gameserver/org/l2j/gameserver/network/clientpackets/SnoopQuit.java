// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.world.World;

public final class SnoopQuit extends ClientPacket
{
    private int _snoopID;
    
    public void readImpl() {
        this._snoopID = this.readInt();
    }
    
    public void runImpl() {
        final Player player = World.getInstance().findPlayer(this._snoopID);
        if (player == null) {
            return;
        }
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        player.removeSnooper(activeChar);
        activeChar.removeSnooped(player);
    }
}
