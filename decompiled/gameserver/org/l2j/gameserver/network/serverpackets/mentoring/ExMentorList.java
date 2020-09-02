// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.mentoring;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExMentorList extends ServerPacket
{
    public ExMentorList(final Player activeChar) {
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MENTOR_LIST);
    }
}
