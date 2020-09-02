// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.network.GameClient;

public final class RequestOustPartyMember extends ClientPacket
{
    private String _name;
    
    public void readImpl() {
        this._name = this.readString();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.isInParty() && activeChar.getParty().isLeader(activeChar)) {
            activeChar.getParty().removePartyMember(this._name, Party.MessageType.EXPELLED);
        }
    }
}
