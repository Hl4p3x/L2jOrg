// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.mentoring.ExMentorAdd;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestMenteeAdd extends ClientPacket
{
    private String _target;
    
    public void readImpl() {
        this._target = this.readString();
    }
    
    public void runImpl() {
        final Player mentor = ((GameClient)this.client).getPlayer();
        if (mentor == null) {
            return;
        }
        final Player mentee = World.getInstance().findPlayer(this._target);
        if (mentee == null) {
            return;
        }
        if (ConfirmMenteeAdd.validate(mentor, mentee)) {
            mentor.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OFFERED_TO_BECOME_S1_S_MENTOR)).addString(mentee.getName()));
            mentee.sendPacket(new ExMentorAdd(mentor));
        }
    }
}
