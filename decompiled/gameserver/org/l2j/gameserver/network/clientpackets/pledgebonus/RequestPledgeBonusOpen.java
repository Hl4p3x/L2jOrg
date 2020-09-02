// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pledgebonus;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPledgeClassicRaidInfo;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeBonusOpen;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestPledgeBonusOpen extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null || player.getClan() == null) {
            return;
        }
        player.sendPacket(new ExPledgeBonusOpen(player));
        player.sendPacket(new ExPledgeClassicRaidInfo());
    }
}
