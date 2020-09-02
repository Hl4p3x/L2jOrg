// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.friend;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class FriendListPacket extends AbstractFriendListPacket
{
    public FriendListPacket(final Player player) {
        super(player);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.L2_FRIEND_LIST);
        this.writeInt(this.info.size());
        for (final FriendInfo info : this.info) {
            this.writeInt(info.objectId);
            this.writeString((CharSequence)info.name);
            this.writeInt(info.online);
            this.writeInt(info.online ? info.objectId : 0);
            this.writeInt(info.level);
            this.writeInt(info.classId);
            this.writeShort((short)0);
        }
    }
}
