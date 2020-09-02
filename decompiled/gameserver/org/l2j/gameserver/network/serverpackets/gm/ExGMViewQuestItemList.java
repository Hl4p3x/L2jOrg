// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.gm;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Collection;
import org.l2j.gameserver.network.serverpackets.AbstractItemPacket;

public class ExGMViewQuestItemList extends AbstractItemPacket
{
    private final Collection<Item> items;
    private final String playerName;
    private final int sendType;
    
    public ExGMViewQuestItemList(final int sendType, final Player player) {
        this.sendType = sendType;
        this.playerName = player.getName();
        this.items = player.getInventory().getItems().stream().filter(Item::isQuestItem).collect((Collector<? super Item, ?, Collection<Item>>)Collectors.toList());
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_GM_VIEW_QUEST_ITEMLIST);
        this.writeByte(this.sendType);
        if (this.sendType == 2) {
            this.writeInt(this.items.size());
            this.writeInt(this.items.size());
            for (final Item item : this.items) {
                this.writeItem(item);
            }
        }
        else {
            this.writeInt(100);
            this.writeInt(this.items.size());
        }
        this.writeShort(0);
    }
}
