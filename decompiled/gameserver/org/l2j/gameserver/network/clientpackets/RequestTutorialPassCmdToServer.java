// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.network.GameClient;

public class RequestTutorialPassCmdToServer extends ClientPacket
{
    private String bypass;
    
    public RequestTutorialPassCmdToServer() {
        this.bypass = null;
    }
    
    public void readImpl() {
        this.bypass = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (this.bypass.startsWith("admin_")) {
            AdminCommandHandler.getInstance().useAdminCommand(player, this.bypass, true);
        }
        else {
            final IBypassHandler handler = BypassHandler.getInstance().getHandler(this.bypass);
            if (handler != null) {
                handler.useBypass(this.bypass, player, null);
            }
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerBypass(player, this.bypass), player);
    }
}
