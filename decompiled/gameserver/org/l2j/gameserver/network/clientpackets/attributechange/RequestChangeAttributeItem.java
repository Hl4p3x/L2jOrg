// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.attributechange;

import java.util.Iterator;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.attributechange.ExChangeAttributeOk;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.serverpackets.attributechange.ExChangeAttributeFail;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestChangeAttributeItem extends ClientPacket
{
    private int _consumeItemId;
    private int _itemObjId;
    private int _newElementId;
    
    public void readImpl() {
        this._consumeItemId = this.readInt();
        this._itemObjId = this.readInt();
        this._newElementId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final PlayerInventory inventory = activeChar.getInventory();
        final Item item = inventory.getItemByObjectId(this._itemObjId);
        if (activeChar.getInventory().destroyItemByItemId("ChangeAttribute", this._consumeItemId, 1L, activeChar, item) == null) {
            ((GameClient)this.client).sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            ((GameClient)this.client).sendPacket(ExChangeAttributeFail.STATIC);
            GameUtils.handleIllegalPlayerAction(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
            return;
        }
        final int oldElementId = item.getAttackAttributeType().getClientId();
        final int elementValue = item.getAttackAttribute().getValue();
        item.clearAllAttributes();
        item.setAttribute(new AttributeHolder(AttributeType.findByClientId(this._newElementId), elementValue), true);
        final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.S1_S_S2_ATTRIBUTE_HAS_SUCCESSFULLY_CHANGED_TO_S3_ATTRIBUTE);
        msg.addItemName(item);
        msg.addAttribute(oldElementId);
        msg.addAttribute(this._newElementId);
        activeChar.sendPacket(msg);
        final InventoryUpdate iu = new InventoryUpdate();
        iu.addModifiedItem(item);
        for (final Item i : activeChar.getInventory().getItemsByItemId(this._consumeItemId)) {
            iu.addItem(i);
        }
        activeChar.sendPacket(iu);
        activeChar.broadcastUserInfo();
        activeChar.sendPacket(ExChangeAttributeOk.STATIC);
    }
}
