// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.enums.ChatType;

public class Snoop extends ServerPacket
{
    private final int _convoId;
    private final String _name;
    private final ChatType _type;
    private final String _speaker;
    private final String _msg;
    
    public Snoop(final int id, final String name, final ChatType type, final String speaker, final String msg) {
        this._convoId = id;
        this._name = name;
        this._type = type;
        this._speaker = speaker;
        this._msg = msg;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SNOOP);
        this.writeInt(this._convoId);
        this.writeString((CharSequence)this._name);
        this.writeInt(0);
        this.writeInt(this._type.getClientId());
        this.writeString((CharSequence)this._speaker);
        this.writeString((CharSequence)this._msg);
    }
}
