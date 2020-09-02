// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.GraciaSeedsManager;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExShowSeedMapInfo extends ServerPacket
{
    public static final ExShowSeedMapInfo STATIC_PACKET;
    
    private ExShowSeedMapInfo() {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_SEED_MAP_INFO);
        this.writeInt(2);
        this.writeInt(1);
        this.writeInt(2770 + GraciaSeedsManager.getInstance().getSoDState());
        this.writeInt(2);
        this.writeInt(2766);
    }
    
    static {
        STATIC_PACKET = new ExShowSeedMapInfo();
    }
}
