// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowMemberListAll;
import org.l2j.gameserver.network.serverpackets.pledge.ExPledgeCount;
import org.l2j.gameserver.network.serverpackets.pledge.PledgeShowInfoUpdate;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListAdd;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.JoinPledge;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class RequestPledgeWaitingUserAccept extends ClientPacket
{
    private boolean _acceptRequest;
    private int _playerId;
    private int _clanId;
    
    public void readImpl() {
        this._acceptRequest = (this.readInt() == 1);
        this._playerId = this.readInt();
        this._clanId = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null || activeChar.getClan() == null) {
            return;
        }
        if (this._acceptRequest) {
            final Player player = World.getInstance().findPlayer(this._playerId);
            final Clan clan = activeChar.getClan();
            if (player != null && player.getClan() == null && clan != null) {
                player.sendPacket(new JoinPledge(clan.getId()));
                player.setPowerGrade(5);
                clan.addClanMember(player);
                player.setClanPrivileges(player.getClan().getRankPrivs(player.getPowerGrade()));
                player.sendPacket(SystemMessageId.ENTERED_THE_CLAN);
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_JOINED_THE_CLAN);
                sm.addString(player.getName());
                clan.broadcastToOnlineMembers(sm);
                if (clan.getCastleId() > 0) {
                    CastleManager.getInstance().getCastleByOwner(clan).giveResidentialSkills(player);
                }
                player.sendSkillList();
                clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(player), player);
                clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
                clan.broadcastToOnlineMembers(new ExPledgeCount(clan));
                PledgeShowMemberListAll.sendAllTo(player);
                player.setClanJoinExpiryTime(0L);
                player.broadcastUserInfo();
                ClanEntryManager.getInstance().removePlayerApplication(this._clanId, this._playerId);
            }
        }
        else {
            ClanEntryManager.getInstance().removePlayerApplication(this._clanId, this._playerId);
        }
    }
}
