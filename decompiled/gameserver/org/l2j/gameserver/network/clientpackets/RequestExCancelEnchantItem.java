// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.EnchantResult;
import org.l2j.gameserver.network.GameClient;

public class RequestExCancelEnchantItem extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(EnchantResult.error());
        activeChar.removeRequest(EnchantItemRequest.class);
    }
}
