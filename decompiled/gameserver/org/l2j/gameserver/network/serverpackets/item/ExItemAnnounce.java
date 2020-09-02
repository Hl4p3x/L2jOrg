// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.item;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExItemAnnounce extends ServerPacket
{
    private final Item item;
    private final Player player;
    private final ItemAnnounceType type;
    private int sourceItemId;
    
    public ExItemAnnounce(final ItemAnnounceType type, final Player player, final Item item) {
        this.item = item;
        this.player = player;
        this.type = type;
    }
    
    public ExItemAnnounce withSourceItem(final int itemId) {
        this.sourceItemId = itemId;
        return this;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_ITEM_ANNOUNCE);
        this.writeByte(this.type.ordinal());
        this.writeSizedString((CharSequence)this.player.getName());
        this.writeInt(this.item.getId());
        this.writeByte(this.item.getEnchantLevel());
        this.writeInt(this.sourceItemId);
    }
}
