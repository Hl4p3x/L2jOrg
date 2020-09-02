// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerTutorialEvent;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.GameClient;

public class RequestTutorialClientEvent extends ClientPacket
{
    private int event;
    
    public void readImpl() {
        this.event = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerTutorialEvent(player, this.event), player);
    }
}
