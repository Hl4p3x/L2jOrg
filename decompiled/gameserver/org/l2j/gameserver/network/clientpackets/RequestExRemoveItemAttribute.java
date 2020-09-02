// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExBaseAttributeCancelResult;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.network.GameClient;

public class RequestExRemoveItemAttribute extends ClientPacket
{
    private int _objectId;
    private long _price;
    private byte _element;
    
    public void readImpl() {
        this._objectId = this.readInt();
        this._element = (byte)this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Item targetItem = activeChar.getInventory().getItemByObjectId(this._objectId);
        if (targetItem == null) {
            return;
        }
        final AttributeType type = AttributeType.findByClientId(this._element);
        if (type == null) {
            return;
        }
        if (targetItem.getAttributes() == null || targetItem.getAttribute(type) == null) {
            return;
        }
        if (activeChar.reduceAdena("RemoveElement", this.getPrice(targetItem), activeChar, true)) {
            targetItem.clearAttribute(type);
            ((GameClient)this.client).sendPacket(new UserInfo(activeChar));
            final InventoryUpdate iu = new InventoryUpdate();
            iu.addModifiedItem(targetItem);
            activeChar.sendInventoryUpdate(iu);
            final AttributeType realElement = targetItem.isArmor() ? type.getOpposite() : type;
            SystemMessage sm;
            if (targetItem.getEnchantLevel() > 0) {
                if (targetItem.isArmor()) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_S_S3_ATTRIBUTE_WAS_REMOVED_SO_RESISTANCE_TO_S4_WAS_DECREASED);
                }
                else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_S_S3_ATTRIBUTE_HAS_BEEN_REMOVED);
                }
                sm.addInt(targetItem.getEnchantLevel());
                sm.addItemName(targetItem);
                if (targetItem.isArmor()) {
                    sm.addAttribute(realElement.getClientId());
                    sm.addAttribute(realElement.getOpposite().getClientId());
                }
            }
            else {
                if (targetItem.isArmor()) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S_S2_ATTRIBUTE_WAS_REMOVED_AND_RESISTANCE_TO_S3_WAS_DECREASED);
                }
                else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S_S2_ATTRIBUTE_HAS_BEEN_REMOVED);
                }
                sm.addItemName(targetItem);
                if (targetItem.isArmor()) {
                    sm.addAttribute(realElement.getClientId());
                    sm.addAttribute(realElement.getOpposite().getClientId());
                }
            }
            ((GameClient)this.client).sendPacket(sm);
            ((GameClient)this.client).sendPacket(new ExBaseAttributeCancelResult(targetItem.getObjectId(), this._element));
        }
        else {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_FUNDS_TO_CANCEL_THIS_ATTRIBUTE);
        }
    }
    
    private long getPrice(final Item item) {
        switch (item.getTemplate().getCrystalType()) {
            case S: {
                if (item.getTemplate() instanceof Weapon) {
                    this._price = 50000L;
                    break;
                }
                this._price = 40000L;
                break;
            }
        }
        return this._price;
    }
}
