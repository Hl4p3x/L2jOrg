// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPutEnchantSupportItemResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.engine.item.EnchantItemEngine;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.network.GameClient;

public class RequestExTryToPutEnchantSupportItem extends ClientPacket
{
    private int _supportObjectId;
    private int _enchantObjectId;
    
    public void readImpl() {
        this._supportObjectId = this.readInt();
        this._enchantObjectId = this.readInt();
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
        request.setEnchantingItem(this._enchantObjectId);
        request.setSupportItem(this._supportObjectId);
        final Item item = request.getEnchantingItem();
        final Item scroll = request.getEnchantingScroll();
        final Item support = request.getSupportItem();
        if (item == null || scroll == null || support == null) {
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            request.setEnchantingItem(-1);
            request.setSupportItem(-1);
            return;
        }
        if (EnchantItemEngine.getInstance().canEnchant(item, scroll)) {
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            request.setSupportItem(-1);
            activeChar.sendPacket(new ExPutEnchantSupportItemResult(0));
            return;
        }
        request.setSupportItem(support.getObjectId());
        request.setTimestamp(System.currentTimeMillis());
        activeChar.sendPacket(new ExPutEnchantSupportItemResult(this._supportObjectId));
    }
}
