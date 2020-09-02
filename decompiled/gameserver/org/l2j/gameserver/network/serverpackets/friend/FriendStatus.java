// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class FriendStatus extends ServerPacket
{
    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;
    public static final int LEVEL = 2;
    public static final int CLASS = 3;
    private final int _type;
    private final int _objectId;
    private final int _classId;
    private final int _level;
    private final String _name;
    
    public FriendStatus(final Player player, final int type) {
        this._objectId = player.getObjectId();
        this._classId = player.getActiveClass();
        this._level = player.getLevel();
        this._name = player.getName();
        this._type = type;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.FRIEND_STATUS);
        this.writeInt(this._type);
        this.writeString((CharSequence)this._name);
        switch (this._type) {
            case 0: {
                this.writeInt(this._objectId);
                break;
            }
            case 2: {
                this.writeInt(this._level);
                break;
            }
            case 3: {
                this.writeInt(this._classId);
                break;
            }
        }
    }
}
