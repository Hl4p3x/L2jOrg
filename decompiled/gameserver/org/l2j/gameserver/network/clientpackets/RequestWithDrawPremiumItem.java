// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExGetPremiumItemList;
import org.l2j.gameserver.model.PremiumItem;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;

public final class RequestWithDrawPremiumItem extends ClientPacket
{
    private int _itemNum;
    private int _charId;
    private long _itemCount;
    
    public void readImpl() {
        this._itemNum = this.readInt();
        this._charId = this.readInt();
        this._itemCount = this.readLong();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (this._itemCount <= 0L) {
            return;
        }
        if (activeChar.getObjectId() != this._charId) {
            GameUtils.handleIllegalPlayerAction(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
            return;
        }
        if (activeChar.getPremiumItemList().isEmpty()) {
            GameUtils.handleIllegalPlayerAction(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
            return;
        }
        if (activeChar.getWeightPenalty() >= 3 || !activeChar.isInventoryUnder90(false)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_THE_DIMENSIONAL_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHT_QUANTITY_LIMIT);
            return;
        }
        if (activeChar.isProcessingTransaction()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_CANNOT_RECEIVE_A_DIMENSIONAL_ITEM_DURING_AN_EXCHANGE);
            return;
        }
        final PremiumItem _item = activeChar.getPremiumItemList().get(this._itemNum);
        if (_item == null) {
            return;
        }
        if (_item.getCount() < this._itemCount) {
            return;
        }
        final long itemsLeft = _item.getCount() - this._itemCount;
        activeChar.addItem("PremiumItem", _item.getItemId(), this._itemCount, activeChar.getTarget(), true);
        if (itemsLeft > 0L) {
            _item.updateCount(itemsLeft);
            activeChar.updatePremiumItem(this._itemNum, itemsLeft);
        }
        else {
            activeChar.getPremiumItemList().remove(this._itemNum);
            activeChar.deletePremiumItem(this._itemNum);
        }
        if (activeChar.getPremiumItemList().isEmpty()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.THERE_ARE_NO_MORE_DIMENSIONAL_ITEMS_TO_BE_FOUND);
        }
        else {
            ((GameClient)this.client).sendPacket(new ExGetPremiumItemList(activeChar));
        }
    }
}
