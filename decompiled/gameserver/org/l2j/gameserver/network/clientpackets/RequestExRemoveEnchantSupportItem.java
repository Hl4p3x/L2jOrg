// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExRemoveEnchantSupportItemResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.network.GameClient;

public class RequestExRemoveEnchantSupportItem extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final EnchantItemRequest request = activeChar.getRequest(EnchantItemRequest.class);
        if (request == null || request.isProcessing()) {
            return;
        }
        final Item supportItem = request.getSupportItem();
        if (supportItem == null || supportItem.getCount() < 1L) {
            request.setSupportItem(-1);
        }
        request.setTimestamp(System.currentTimeMillis());
        activeChar.sendPacket(ExRemoveEnchantSupportItemResult.STATIC_PACKET);
    }
}
