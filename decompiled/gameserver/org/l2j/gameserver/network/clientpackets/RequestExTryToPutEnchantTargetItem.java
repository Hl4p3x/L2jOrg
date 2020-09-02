// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExPutEnchantTargetItemResult;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.engine.item.EnchantItemEngine;
import java.util.Objects;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.network.GameClient;

public class RequestExTryToPutEnchantTargetItem extends ClientPacket
{
    private int objectId;
    
    public void readImpl() {
        this.objectId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        final EnchantItemRequest request = player.getRequest(EnchantItemRequest.class);
        if (Objects.isNull(request) || request.isProcessing()) {
            return;
        }
        request.setEnchantingItem(this.objectId);
        final Item item = request.getEnchantingItem();
        final Item scroll = request.getEnchantingScroll();
        if (Objects.isNull(item) || Objects.isNull(scroll)) {
            return;
        }
        if (!EnchantItemEngine.getInstance().canEnchant(item, scroll)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.removeRequest(EnchantItemRequest.class);
            ((GameClient)this.client).sendPacket(new ExPutEnchantTargetItemResult(0));
        }
        else {
            request.setTimestamp(System.currentTimeMillis());
            ((GameClient)this.client).sendPacket(new ExPutEnchantTargetItemResult(this.objectId));
        }
    }
}
