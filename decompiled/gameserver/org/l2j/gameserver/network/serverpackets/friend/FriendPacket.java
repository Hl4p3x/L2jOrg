// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class FriendPacket extends ServerPacket
{
    private final boolean _action;
    private final boolean _online;
    private final int _objid;
    private final String _name;
    
    public FriendPacket(final boolean action, final int objId) {
        this._action = action;
        this._objid = objId;
        this._name = PlayerNameTable.getInstance().getNameById(objId);
        this._online = (World.getInstance().findPlayer(objId) != null);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.L2_FRIEND);
        this.writeInt(this._action ? 1 : 3);
        this.writeInt(this._objid);
        this.writeString((CharSequence)this._name);
        this.writeInt((int)(this._online ? 1 : 0));
        this.writeInt(this._online ? this._objid : 0);
    }
}
