// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExResponseBeautyRegistReset extends ServerPacket
{
    public static final int FAILURE = 0;
    public static final int SUCCESS = 1;
    public static final int CHANGE = 0;
    public static final int RESTORE = 1;
    private final Player _activeChar;
    private final int _type;
    private final int _result;
    
    public ExResponseBeautyRegistReset(final Player activeChar, final int type, final int result) {
        this._activeChar = activeChar;
        this._type = type;
        this._result = result;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_RESPONSE_BEAUTY_REGIST_RESET);
        this.writeLong(this._activeChar.getAdena());
        this.writeLong(this._activeChar.getBeautyTickets());
        this.writeInt(this._type);
        this.writeInt(this._result);
        this.writeInt(this._activeChar.getVisualHair());
        this.writeInt(this._activeChar.getVisualFace());
        this.writeInt(this._activeChar.getVisualHairColor());
    }
}
