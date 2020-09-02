// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.OptionalInt;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPledgeWaitingListApplied;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.GameClient;

public class RequestPledgeWaitingApplied extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || activeChar.getClan() != null) {
            return;
        }
        final OptionalInt clanId = ClanEntryManager.getInstance().getClanIdForPlayerApplication(activeChar.getObjectId());
        if (clanId.isPresent()) {
            activeChar.sendPacket(new ExPledgeWaitingListApplied(clanId.getAsInt(), activeChar.getObjectId()));
        }
    }
}
