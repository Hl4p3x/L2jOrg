// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeWaitingListAlarm;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeRecruitApplyInfo;
import org.l2j.gameserver.enums.ClanEntryStatus;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.data.database.data.PledgeApplicantData;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestPledgeWaitingApply extends ClientPacket
{
    private int _karma;
    private int _clanId;
    private String _message;
    
    public void readImpl() {
        this._karma = this.readInt();
        this._clanId = this.readInt();
        this._message = this.readString();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || activeChar.getClan() != null) {
            return;
        }
        final Clan clan = ClanTable.getInstance().getClan(this._clanId);
        if (clan == null) {
            return;
        }
        final PledgeApplicantData info = new PledgeApplicantData(activeChar.getObjectId(), activeChar.getName(), activeChar.getLevel(), this._karma, this._clanId, this._message);
        if (ClanEntryManager.getInstance().addPlayerApplicationToClan(this._clanId, info)) {
            ((GameClient)this.client).sendPacket(new ExPledgeRecruitApplyInfo(ClanEntryStatus.WAITING));
            final Player clanLeader = World.getInstance().findPlayer(clan.getLeaderId());
            if (clanLeader != null) {
                clanLeader.sendPacket(ExPledgeWaitingListAlarm.STATIC_PACKET);
            }
        }
        else {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
            sm.addLong(ClanEntryManager.getInstance().getPlayerLockTime(activeChar.getObjectId()));
            ((GameClient)this.client).sendPacket(sm);
        }
    }
}
