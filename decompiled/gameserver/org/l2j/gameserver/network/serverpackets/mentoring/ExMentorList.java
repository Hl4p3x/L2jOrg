// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.mentoring;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Collections;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Mentee;
import java.util.Collection;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExMentorList extends ServerPacket
{
    private final int _type;
    private final Collection<Mentee> _mentees;
    
    public ExMentorList(final Player activeChar) {
        if (activeChar.isMentor()) {
            this._type = 1;
            this._mentees = MentorManager.getInstance().getMentees(activeChar.getObjectId());
        }
        else if (activeChar.isMentee()) {
            this._type = 2;
            this._mentees = Collections.singletonList(MentorManager.getInstance().getMentor(activeChar.getObjectId()));
        }
        else {
            this._mentees = (Collection<Mentee>)Collections.emptyList();
            this._type = 0;
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_MENTOR_LIST);
        this.writeInt(this._type);
        this.writeInt(0);
        this.writeInt(this._mentees.size());
        for (final Mentee mentee : this._mentees) {
            this.writeInt(mentee.getObjectId());
            this.writeString((CharSequence)mentee.getName());
            this.writeInt(mentee.getClassId());
            this.writeInt(mentee.getLevel());
            this.writeInt(mentee.isOnlineInt());
        }
    }
}
