// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerChangeToAwakenedClass;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.GameClient;

public class RequestChangeToAwakenedClass extends ClientPacket
{
    private boolean _change;
    
    public void readImpl() {
        this._change = (this.readInt() == 1);
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (this._change) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerChangeToAwakenedClass(player), player);
        }
        else {
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}
