// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.GameClient;

public class RequestChangeNicknameColor extends ClientPacket
{
    private static final int[] COLORS;
    private int _colorNum;
    private int _itemObjectId;
    private String _title;
    
    public void readImpl() {
        this._colorNum = this.readInt();
        this._title = this.readString();
        this._itemObjectId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (this._colorNum < 0 || this._colorNum >= RequestChangeNicknameColor.COLORS.length) {
            return;
        }
        final Item item = activeChar.getInventory().getItemByObjectId(this._itemObjectId);
        if (item == null || item.getEtcItem() == null || item.getEtcItem().getHandlerName() == null || !item.getEtcItem().getHandlerName().equalsIgnoreCase("NicknameColor")) {
            return;
        }
        if (activeChar.destroyItem("Consume", item, 1L, null, true)) {
            activeChar.setTitle(this._title);
            activeChar.getAppearance().setTitleColor(RequestChangeNicknameColor.COLORS[this._colorNum]);
            activeChar.broadcastUserInfo();
        }
    }
    
    static {
        COLORS = new int[] { 9671679, 8145404, 9959676, 16423662, 16735635, 64672, 10528257, 7903407, 4743829, 10066329 };
    }
}
