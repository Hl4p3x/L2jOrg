// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pvpbook;

import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.database.data.KillerData;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class PvpBookList extends ServerPacket
{
    private final List<KillerData> killers;
    
    public PvpBookList(final List<KillerData> killers) {
        this.killers = killers;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PVPBOOK_LIST);
        final Player player = client.getPlayer();
        this.writeInt(player.getRevengeUsableLocation());
        this.writeInt(player.getRevengeUsableTeleport());
        this.writeInt(this.killers.size());
        for (final KillerData killer : this.killers) {
            this.writeSizedString((CharSequence)killer.getName());
            this.writeSizedString((CharSequence)killer.getClan());
            this.writeInt(killer.getLevel());
            this.writeInt(killer.getRace());
            this.writeInt(killer.getActiveClass());
            this.writeInt(killer.getKillTime());
            this.writeByte(killer.isOnline());
        }
    }
}
