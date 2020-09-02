// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class ExShowBeautyMenu extends ServerPacket
{
    public static final int MODIFY_APPEARANCE = 0;
    public static final int RESTORE_APPEARANCE = 1;
    private final Player _activeChar;
    private final int _type;
    
    public ExShowBeautyMenu(final Player activeChar, final int type) {
        this._activeChar = activeChar;
        this._type = type;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SHOW_BEAUTY_MENU);
        this.writeInt(this._type);
        this.writeInt(this._activeChar.getVisualHair());
        this.writeInt(this._activeChar.getVisualHairColor());
        this.writeInt(this._activeChar.getVisualFace());
    }
}
