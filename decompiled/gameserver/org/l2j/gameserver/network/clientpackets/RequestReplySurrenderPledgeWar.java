// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestReplySurrenderPledgeWar extends ClientPacket
{
    private static final Logger LOGGER;
    private String _reqName;
    private int _answer;
    
    public void readImpl() {
        this._reqName = this.readString();
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
            RequestReplySurrenderPledgeWar.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), this._answer, this._reqName));
        }
        activeChar.onTransactionRequest(requestor);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestReplySurrenderPledgeWar.class);
    }
}
