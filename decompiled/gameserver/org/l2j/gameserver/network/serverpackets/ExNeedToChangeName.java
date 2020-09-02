// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExNeedToChangeName extends ServerPacket
{
    private final int _type;
    private final int _subType;
    private final String _name;
    
    public ExNeedToChangeName(final int type, final int subType, final String name) {
        this._type = type;
        this._subType = subType;
        this._name = name;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_NEED_TO_CHANGE_NAME);
        this.writeInt(this._type);
        this.writeInt(this._subType);
        this.writeString((CharSequence)this._name);
    }
}
