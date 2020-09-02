// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.mentoring;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExMentorAdd extends ServerPacket
{
    final Player _mentor;
    
    public ExMentorAdd(final Player mentor) {
        this._mentor = mentor;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MENTOR_ADD);
        this.writeString((CharSequence)this._mentor.getName());
        this.writeInt(this._mentor.getActiveClass());
        this.writeInt(this._mentor.getLevel());
    }
}
