// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.entity.ClanHall;
import java.util.Collection;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowAgitInfo extends ServerPacket
{
    public static final ExShowAgitInfo STATIC_PACKET;
    
    private ExShowAgitInfo() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_AGIT_INFO);
        final Collection<ClanHall> clanHalls = ClanHallManager.getInstance().getClanHalls();
        this.writeInt(clanHalls.size());
        clanHalls.forEach(clanHall -> {
            this.writeInt(clanHall.getId());
            this.writeString((CharSequence)((clanHall.getOwnerId() <= 0) ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getName()));
            this.writeString((CharSequence)((clanHall.getOwnerId() <= 0) ? "" : ClanTable.getInstance().getClan(clanHall.getOwnerId()).getLeaderName()));
            this.writeInt(clanHall.getType().getClientVal());
        });
    }
    
    static {
        STATIC_PACKET = new ExShowAgitInfo();
    }
}
