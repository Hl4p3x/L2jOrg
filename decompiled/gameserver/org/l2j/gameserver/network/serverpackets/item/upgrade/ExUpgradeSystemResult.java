// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.item.upgrade;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExUpgradeSystemResult extends ServerPacket
{
    private final int upgradedId;
    
    public ExUpgradeSystemResult(final int upgradedId) {
        this.upgradedId = upgradedId;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_UPGRADE_SYSTEM_RESULT);
        this.writeShort(true);
        this.writeInt(this.upgradedId);
    }
}
