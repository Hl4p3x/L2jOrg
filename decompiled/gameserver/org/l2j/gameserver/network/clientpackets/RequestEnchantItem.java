// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.item.EnchantItemEngine;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.EnchantResult;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import java.util.Objects;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.network.GameClient;

public final class RequestEnchantItem extends ClientPacket
{
    private int objectId;
    private int supportId;
    
    public void readImpl() {
        this.objectId = this.readInt();
        this.supportId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        final EnchantItemRequest request = player.getRequest(EnchantItemRequest.class);
        if (Objects.isNull(request) || request.isProcessing()) {
            return;
        }
        request.setEnchantingItem(this.objectId);
        request.setProcessing(true);
        if (!player.isOnline() || ((GameClient)this.client).isDetached()) {
            player.removeRequest(EnchantItemRequest.class);
            return;
        }
        if (player.isProcessingTransaction() || player.isInStoreMode()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            player.removeRequest(EnchantItemRequest.class);
            return;
        }
        if (request.getTimestamp() == 0L || System.currentTimeMillis() - request.getTimestamp() < 2000L) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, player));
            player.removeRequest(EnchantItemRequest.class);
            ((GameClient)this.client).sendPacket(EnchantResult.error());
            return;
        }
        final Item item = request.getEnchantingItem();
        final Item scroll = request.getEnchantingScroll();
        if (Objects.isNull(item) || Objects.isNull(scroll)) {
            player.removeRequest(EnchantItemRequest.class);
            return;
        }
        EnchantItemEngine.getInstance().enchant(player, item, scroll);
        request.setProcessing(false);
    }
}
