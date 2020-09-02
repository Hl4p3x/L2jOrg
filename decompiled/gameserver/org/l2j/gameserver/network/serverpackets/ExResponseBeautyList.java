// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.model.beautyshop.BeautyItem;
import java.util.Map;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExResponseBeautyList extends ServerPacket
{
    public static final int SHOW_FACESHAPE = 1;
    public static final int SHOW_HAIRSTYLE = 0;
    private final Player _activeChar;
    private final int _type;
    private final Map<Integer, BeautyItem> _beautyItem;
    
    public ExResponseBeautyList(final Player activeChar, final int type) {
        this._activeChar = activeChar;
        this._type = type;
        if (type == 0) {
            this._beautyItem = BeautyShopData.getInstance().getBeautyData(activeChar.getRace(), activeChar.getAppearance().getSexType()).getHairList();
        }
        else {
            this._beautyItem = BeautyShopData.getInstance().getBeautyData(activeChar.getRace(), activeChar.getAppearance().getSexType()).getFaceList();
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_BEAUTY_LIST);
        this.writeLong(this._activeChar.getAdena());
        this.writeLong(this._activeChar.getBeautyTickets());
        this.writeInt(this._type);
        this.writeInt(this._beautyItem.size());
        for (final BeautyItem item : this._beautyItem.values()) {
            this.writeInt(item.getId());
            this.writeInt(1);
        }
        this.writeInt(0);
    }
}
