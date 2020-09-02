// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExBrPremiumState extends ServerPacket
{
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BR_NOTIFY_PREMIUM_STATE);
        final Player activeChar = client.getPlayer();
        this.writeInt(activeChar.getObjectId());
        this.writeByte((byte)(byte)((activeChar.getVipTier() > 0) ? 1 : 0));
    }
}
