// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.olympiad;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public final class RequestOlympiadMatchList extends ClientPacket
{
    private static final String COMMAND = "arenalist";
    
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || !activeChar.inObserverMode()) {
            return;
        }
        final IBypassHandler handler = BypassHandler.getInstance().getHandler("arenalist");
        if (handler != null) {
            handler.useBypass("arenalist", activeChar, null);
        }
    }
}
