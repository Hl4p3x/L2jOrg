// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;

public class AnswerPartyLootModification extends ClientPacket
{
    public int _answer;
    
    public void readImpl() {
        this._answer = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Party party = activeChar.getParty();
        if (party != null) {
            party.answerLootChangeRequest(activeChar, this._answer == 1);
        }
    }
}
