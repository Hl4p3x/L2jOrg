// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pledge;

import java.util.OptionalInt;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeWaitingListApplied;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestPledgeWaitingApplied extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.getClan() != null) {
            return;
        }
        final OptionalInt clanId = ClanEntryManager.getInstance().getClanIdForPlayerApplication(player.getObjectId());
        if (clanId.isPresent()) {
            player.sendPacket(new ExPledgeWaitingListApplied(clanId.getAsInt(), player.getObjectId()));
        }
    }
}
