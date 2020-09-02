// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.fishing;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExUserInfoFishing extends ServerPacket
{
    private final Player _activeChar;
    private final boolean _isFishing;
    private final ILocational _baitLocation;
    
    public ExUserInfoFishing(final Player activeChar, final boolean isFishing, final ILocational baitLocation) {
        this._activeChar = activeChar;
        this._isFishing = isFishing;
        this._baitLocation = baitLocation;
    }
    
    public ExUserInfoFishing(final Player activeChar, final boolean isFishing) {
        this._activeChar = activeChar;
        this._isFishing = isFishing;
        this._baitLocation = null;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_USER_INFO_FISHING);
        this.writeInt(this._activeChar.getObjectId());
        this.writeByte((byte)(byte)(this._isFishing ? 1 : 0));
        if (this._baitLocation == null) {
            this.writeInt(0);
            this.writeInt(0);
            this.writeInt(0);
        }
        else {
            this.writeInt(this._baitLocation.getX());
            this.writeInt(this._baitLocation.getY());
            this.writeInt(this._baitLocation.getZ());
        }
    }
}
