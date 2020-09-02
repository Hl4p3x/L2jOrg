// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.raidbossinfo;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Map;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExRaidBossSpawnInfo extends ServerPacket
{
    private final Map<Integer, Integer> _bossIds;
    
    public ExRaidBossSpawnInfo(final Map<Integer, Integer> bossIds) {
        this._bossIds = bossIds;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RAID_BOSS_SPAWN_INFO);
        this.writeInt(this._bossIds.size());
        for (final Map.Entry<Integer, Integer> boss : this._bossIds.entrySet()) {
            this.writeInt((int)boss.getKey());
            this.writeInt((int)boss.getValue());
            this.writeInt(0);
        }
    }
}
