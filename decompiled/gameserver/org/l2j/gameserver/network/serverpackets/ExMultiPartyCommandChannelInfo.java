// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Objects;
import org.l2j.gameserver.model.CommandChannel;

public class ExMultiPartyCommandChannelInfo extends ServerPacket
{
    private final CommandChannel _channel;
    
    public ExMultiPartyCommandChannelInfo(final CommandChannel channel) {
        Objects.requireNonNull(channel);
        this._channel = channel;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MULTI_PARTY_COMMAND_CHANNEL_INFO);
        this.writeString((CharSequence)this._channel.getLeader().getName());
        this.writeInt(0);
        this.writeInt(this._channel.getMemberCount());
        this.writeInt(this._channel.getPartys().size());
        for (final Party p : this._channel.getPartys()) {
            this.writeString((CharSequence)p.getLeader().getName());
            this.writeInt(p.getLeaderObjectId());
            this.writeInt(p.getMemberCount());
        }
    }
}
