// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeRecruitApplyInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.ClanEntryStatus;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestPledgeRecruitApplyInfo extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        ClanEntryStatus status;
        if (player.getClan() != null && player.isClanLeader() && ClanEntryManager.getInstance().isClanRegistred(player.getClanId())) {
            status = ClanEntryStatus.ORDERED;
        }
        else if (player.getClan() == null && ClanEntryManager.getInstance().isPlayerRegistred(player.getObjectId())) {
            status = ClanEntryStatus.WAITING;
        }
        else {
            status = ClanEntryStatus.DEFAULT;
        }
        player.sendPacket(new ExPledgeRecruitApplyInfo(status));
    }
}
