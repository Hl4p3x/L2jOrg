// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.HashMap;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Location;
import java.util.Map;

public class PartyMemberPosition extends ServerPacket
{
    private final Map<Integer, Location> locations;
    
    public PartyMemberPosition(final Party party) {
        this.locations = new HashMap<Integer, Location>();
        this.reuse(party);
    }
    
    public void reuse(final Party party) {
        this.locations.clear();
        for (final Player member : party.getMembers()) {
            if (member == null) {
                continue;
            }
            this.locations.put(member.getObjectId(), member.getLocation());
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PARTY_MEMBER_POSITION);
        this.writeInt(this.locations.size());
        for (final Map.Entry<Integer, Location> entry : this.locations.entrySet()) {
            final Location loc = entry.getValue();
            this.writeInt((int)entry.getKey());
            this.writeInt(loc.getX());
            this.writeInt(loc.getY());
            this.writeInt(loc.getZ());
        }
    }
}
