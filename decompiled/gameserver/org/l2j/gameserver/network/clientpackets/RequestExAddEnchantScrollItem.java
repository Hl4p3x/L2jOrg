// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.item.EnchantItemEngine;
import org.l2j.gameserver.network.serverpackets.ExPutEnchantScrollItemResult;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.network.GameClient;

public class RequestExAddEnchantScrollItem extends ClientPacket
{
    private int _scrollObjectId;
    private int _enchantObjectId;
    
    public void readImpl() {
        this._scrollObjectId = this.readInt();
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
        request.setEnchantingScroll(this._scrollObjectId);
        final Item item = request.getEnchantingItem();
        final Item scroll = request.getEnchantingScroll();
        if (item == null || scroll == null) {
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            activeChar.sendPacket(new ExPutEnchantScrollItemResult(0));
            request.setEnchantingItem(-1);
            request.setEnchantingScroll(-1);
            return;
        }
        if (!EnchantItemEngine.getInstance().existsScroll(scroll)) {
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            activeChar.sendPacket(new ExPutEnchantScrollItemResult(0));
            request.setEnchantingScroll(-1);
            return;
        }
        request.setTimestamp(System.currentTimeMillis());
        activeChar.sendPacket(new ExPutEnchantScrollItemResult(this._scrollObjectId));
    }
}
