// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.PremiumItem;
import java.util.Map;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExGetPremiumItemList extends ServerPacket
{
    private final Player _activeChar;
    private final Map<Integer, PremiumItem> _map;
    
    public ExGetPremiumItemList(final Player activeChar) {
        this._activeChar = activeChar;
        this._map = this._activeChar.getPremiumItemList();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PREMIUM_ITEM_LIST);
        this.writeInt(this._map.size());
        for (final Map.Entry<Integer, PremiumItem> entry : this._map.entrySet()) {
            final PremiumItem item = entry.getValue();
            this.writeLong((long)entry.getKey());
            this.writeInt(item.getItemId());
            this.writeLong(item.getCount());
            this.writeInt(0);
            this.writeString((CharSequence)item.getSender());
        }
    }
}
