// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.request.EnchantItemAttributeRequest;
import org.l2j.gameserver.network.GameClient;

public class RequestExEnchantItemAttribute extends ClientPacket
{
    public void readImpl() {
        final int _objectId = this.readInt();
        final long _count = this.readLong();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final EnchantItemAttributeRequest request = player.getRequest(EnchantItemAttributeRequest.class);
        if (request == null) {
            return;
        }
        ((GameClient)this.client).sendPacket(SystemMessageId.ELEMENTAL_POWER_ENHANCER_USAGE_REQUIREMENT_IS_NOT_SUFFICIENT);
        player.removeRequest(request.getClass());
    }
}
