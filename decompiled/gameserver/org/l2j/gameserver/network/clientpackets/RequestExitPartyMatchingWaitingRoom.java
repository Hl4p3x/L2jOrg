// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.network.GameClient;

public final class RequestExitPartyMatchingWaitingRoom extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        MatchingRoomManager.getInstance().removeFromWaitingList(player);
    }
}
