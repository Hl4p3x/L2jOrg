// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.network.GameClient;

public final class RequestReplyStopPledgeWar extends ClientPacket
{
    private int _answer;
    
    public void readImpl() {
        this.readString();
        this._answer = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Player requestor = activeChar.getActiveRequester();
        if (requestor == null) {
            return;
        }
        if (this._answer == 1) {
            ClanTable.getInstance().deleteClanWars(requestor.getClanId(), activeChar.getClanId());
        }
        else {
            requestor.sendPacket(SystemMessageId.REQUEST_TO_END_WAR_HAS_BEEN_DENIED);
        }
        activeChar.setActiveRequester(null);
        requestor.onTransactionResponse();
    }
}
