// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.pvpbook;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExNewPk extends ServerPacket
{
    private final Player killer;
    
    public ExNewPk(final Player killer) {
        this.killer = killer;
    }
    
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_PVPBOOK_NEW_PK);
        this.writeSizedString((CharSequence)this.killer.getName());
    }
}
