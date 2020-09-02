// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.EnumMap;

public class ShopPreviewInfo extends ServerPacket
{
    private final EnumMap<InventorySlot, Integer> items;
    
    public ShopPreviewInfo(final EnumMap<InventorySlot, Integer> items) {
        this.items = items;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.BUY_PREVIEW_INFO);
        this.writeInt(60);
        final InventorySlot[] paperdool = this.getPaperdollOrder();
        for (int i = 0; i < 19; ++i) {
            this.writeInt(this.getFromList(paperdool[i]));
        }
    }
    
    private int getFromList(final InventorySlot key) {
        return this.items.getOrDefault(key, 0);
    }
}
