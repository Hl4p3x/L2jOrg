// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExPledgeRecruitApplyInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.ClanEntryStatus;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.GameClient;

public class RequestPledgeRecruitApplyInfo extends ClientPacket
{
    public void readImpl() {
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        ClanEntryStatus status;
        if (activeChar.getClan() != null && activeChar.isClanLeader() && ClanEntryManager.getInstance().isClanRegistred(activeChar.getClanId())) {
            status = ClanEntryStatus.ORDERED;
        }
        else if (activeChar.getClan() == null && ClanEntryManager.getInstance().isPlayerRegistred(activeChar.getObjectId())) {
            status = ClanEntryStatus.WAITING;
        }
        else {
            status = ClanEntryStatus.DEFAULT;
        }
        activeChar.sendPacket(new ExPledgeRecruitApplyInfo(status));
    }
}
