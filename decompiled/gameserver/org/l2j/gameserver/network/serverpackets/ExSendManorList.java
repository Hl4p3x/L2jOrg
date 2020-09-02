// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import java.util.Collection;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public final class ExSendManorList extends ServerPacket
{
    public static final ExSendManorList STATIC_PACKET;
    
    private ExSendManorList() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SEND_MANOR_LIST);
        final Collection<Castle> castles = CastleManager.getInstance().getCastles();
        this.writeInt(castles.size());
        for (final Castle castle : castles) {
            this.writeInt(castle.getId());
        }
    }
    
    static {
        STATIC_PACKET = new ExSendManorList();
    }
}
