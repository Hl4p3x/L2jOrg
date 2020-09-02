// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class HennaRemoveList extends ServerPacket
{
    private final Player _player;
    
    public HennaRemoveList(final Player player) {
        this._player = player;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.HENNA_UNEQUIP_LIST);
        this.writeLong(this._player.getAdena());
        this.writeInt(3);
        this.writeInt(3 - this._player.getHennaEmptySlots());
        for (final Henna henna : this._player.getHennaList()) {
            if (henna != null) {
                this.writeInt(henna.getDyeId());
                this.writeInt(henna.getDyeItemId());
                this.writeLong((long)henna.getCancelCount());
                this.writeLong((long)henna.getCancelFee());
                this.writeInt(0);
            }
        }
    }
}
