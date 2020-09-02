// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.friend;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class FriendAddRequestResult extends ServerPacket
{
    private final int _result;
    private final int _charId;
    private final String _charName;
    private final int _isOnline;
    private final int _charObjectId;
    private final int _charLevel;
    private final int _charClassId;
    
    public FriendAddRequestResult(final Player activeChar, final int result) {
        this._result = result;
        this._charId = activeChar.getObjectId();
        this._charName = activeChar.getName();
        this._isOnline = activeChar.isOnlineInt();
        this._charObjectId = activeChar.getObjectId();
        this._charLevel = activeChar.getLevel();
        this._charClassId = activeChar.getActiveClass();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.FRIEND_ADD_REQUEST_RESULT);
        this.writeInt(this._result);
        this.writeInt(this._charId);
        this.writeString((CharSequence)this._charName);
        this.writeInt(this._isOnline);
        this.writeInt(this._charObjectId);
        this.writeInt(this._charLevel);
        this.writeInt(this._charClassId);
        this.writeShort((short)0);
    }
}
