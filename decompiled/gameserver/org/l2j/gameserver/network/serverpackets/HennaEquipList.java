// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.model.item.Henna;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;

public class HennaEquipList extends ServerPacket
{
    private final Player _player;
    private final List<Henna> _hennaEquipList;
    
    public HennaEquipList(final Player player) {
        this._player = player;
        this._hennaEquipList = HennaData.getInstance().getHennaList(player.getClassId());
    }
    
    public HennaEquipList(final Player player, final List<Henna> list) {
        this._player = player;
        this._hennaEquipList = list;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.HENNA_EQUIP_LIST);
        this.writeLong(this._player.getAdena());
        this.writeInt(3);
        this.writeInt(this._hennaEquipList.size());
        for (final Henna henna : this._hennaEquipList) {
            if (this._player.getInventory().getItemByItemId(henna.getDyeItemId()) != null) {
                this.writeInt(henna.getDyeId());
                this.writeInt(henna.getDyeItemId());
                this.writeLong((long)henna.getWearCount());
                this.writeLong((long)henna.getWearFee());
                this.writeInt((int)(henna.isAllowedClass(this._player.getClassId()) ? 1 : 0));
            }
        }
    }
}
