// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import io.github.joealisson.primitive.IntMap;

public class PackageToList extends ServerPacket
{
    private final IntMap<String> players;
    
    public PackageToList(final IntMap<String> players) {
        this.players = players;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PACKAGE_TO_LIST);
        this.writeInt(this.players.size());
        for (final IntMap.Entry<String> entry : this.players.entrySet()) {
            this.writeInt(entry.getKey());
            this.writeString((CharSequence)entry.getValue());
        }
    }
}
