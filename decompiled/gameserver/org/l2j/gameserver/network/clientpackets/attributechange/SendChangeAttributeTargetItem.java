// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.attributechange;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.attributechange.ExChangeAttributeInfo;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class SendChangeAttributeTargetItem extends ClientPacket
{
    private int _crystalItemId;
    private int _itemObjId;
    
    public void readImpl() {
        this._crystalItemId = this.readInt();
        this._itemObjId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Item item = activeChar.getInventory().getItemByObjectId(this._itemObjId);
        if (item == null || !item.isWeapon()) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        activeChar.sendPacket(new ExChangeAttributeInfo(this._crystalItemId, item));
    }
}
