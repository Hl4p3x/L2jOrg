// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;

public class RecipeShopMsg extends ServerPacket
{
    private final Player _activeChar;
    
    public RecipeShopMsg(final Player player) {
        this._activeChar = player;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.RECIPE_STORE_MSG);
        this.writeInt(this._activeChar.getObjectId());
        this.writeString((CharSequence)this._activeChar.getStoreName());
    }
}
