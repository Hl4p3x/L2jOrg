// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExConfirmAddingContact extends ServerPacket
{
    private final String _charName;
    private final boolean _added;
    
    public ExConfirmAddingContact(final String charName, final boolean added) {
        this._charName = charName;
        this._added = added;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_AGIT_AUCTION_CMD);
        this.writeString((CharSequence)this._charName);
        this.writeInt((int)(this._added ? 1 : 0));
    }
}
