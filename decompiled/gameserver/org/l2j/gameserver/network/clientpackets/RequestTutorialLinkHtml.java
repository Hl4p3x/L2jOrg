// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.network.GameClient;

public class RequestTutorialLinkHtml extends ClientPacket
{
    private String _bypass;
    
    public void readImpl() {
        this.readInt();
        this._bypass = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        if (this._bypass.startsWith("admin_")) {
            AdminCommandHandler.getInstance().useAdminCommand(player, this._bypass, true);
        }
        else {
            final IBypassHandler handler = BypassHandler.getInstance().getHandler(this._bypass);
            if (handler != null) {
                handler.useBypass(this._bypass, player, null);
            }
        }
    }
}
