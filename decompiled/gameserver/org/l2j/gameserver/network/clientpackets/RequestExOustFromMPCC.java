// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.world.World;

public final class RequestExOustFromMPCC extends ClientPacket
{
    private String _name;
    
    public void readImpl() {
        this._name = this.readString();
    }
    
    public void runImpl() {
        final Player target = World.getInstance().findPlayer(this._name);
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (target != null && target.isInParty() && activeChar.isInParty() && activeChar.getParty().isInCommandChannel() && target.getParty().isInCommandChannel() && activeChar.getParty().getCommandChannel().getLeader().equals(activeChar) && activeChar.getParty().getCommandChannel().equals(target.getParty().getCommandChannel())) {
            if (activeChar.equals(target)) {
                return;
            }
            target.getParty().getCommandChannel().removeParty(target.getParty());
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL);
            target.getParty().broadcastPacket(sm);
            if (activeChar.getParty().isInCommandChannel()) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_HAS_BEEN_DISMISSED_FROM_THE_COMMAND_CHANNEL);
                sm.addString(target.getParty().getLeader().getName());
                activeChar.getParty().getCommandChannel().broadcastPacket(sm);
            }
        }
        else {
            activeChar.sendPacket(SystemMessageId.YOUR_TARGET_CANNOT_BE_FOUND);
        }
    }
}
