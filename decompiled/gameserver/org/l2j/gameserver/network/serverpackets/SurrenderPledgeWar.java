// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public final class SurrenderPledgeWar extends ServerPacket
{
    private final String _pledgeName;
    private final String _playerName;
    
    public SurrenderPledgeWar(final String pledge, final String charName) {
        this._pledgeName = pledge;
        this._playerName = charName;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SURRENDER_PLEDGE_WAR);
        this.writeString((CharSequence)this._pledgeName);
        this.writeString((CharSequence)this._playerName);
    }
}
