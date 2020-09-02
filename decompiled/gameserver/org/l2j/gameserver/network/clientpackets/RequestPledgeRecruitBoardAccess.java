// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.clan.entry.PledgeRecruitInfo;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;

public class RequestPledgeRecruitBoardAccess extends ClientPacket
{
    private int _applyType;
    private int _karma;
    private String _information;
    private String _datailedInformation;
    private int _applicationType;
    private int _recruitingType;
    
    public void readImpl() {
        this._applyType = this.readInt();
        this._karma = this.readInt();
        this._information = this.readString();
        this._datailedInformation = this.readString();
        this._applicationType = this.readInt();
        this._recruitingType = this.readInt();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Clan clan = activeChar.getClan();
        if (clan == null) {
            activeChar.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN);
            return;
        }
        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_MANAGE_RANKS)) {
            activeChar.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN);
            return;
        }
        final PledgeRecruitInfo pledgeRecruitInfo = new PledgeRecruitInfo(clan.getId(), this._karma, this._information, this._datailedInformation, this._applicationType, this._recruitingType);
        switch (this._applyType) {
            case 0: {
                ClanEntryManager.getInstance().removeFromClanList(clan.getId());
                break;
            }
            case 1: {
                if (ClanEntryManager.getInstance().addToClanList(clan.getId(), pledgeRecruitInfo)) {
                    activeChar.sendPacket(SystemMessageId.ENTRY_APPLICATION_COMPLETE_USE_ENTRY_APPLICATION_INFO_TO_CHECK_OR_CANCEL_YOUR_APPLICATION_APPLICATION_IS_AUTOMATICALLY_CANCELLED_AFTER_30_DAYS_IF_YOU_CANCEL_APPLICATION_YOU_CANNOT_APPLY_AGAIN_FOR_5_MINUTES);
                    break;
                }
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
                sm.addLong(ClanEntryManager.getInstance().getClanLockTime(clan.getId()));
                activeChar.sendPacket(sm);
                break;
            }
            case 2: {
                if (ClanEntryManager.getInstance().updateClanList(clan.getId(), pledgeRecruitInfo)) {
                    activeChar.sendPacket(SystemMessageId.ENTRY_APPLICATION_COMPLETE_USE_ENTRY_APPLICATION_INFO_TO_CHECK_OR_CANCEL_YOUR_APPLICATION_APPLICATION_IS_AUTOMATICALLY_CANCELLED_AFTER_30_DAYS_IF_YOU_CANCEL_APPLICATION_YOU_CANNOT_APPLY_AGAIN_FOR_5_MINUTES);
                    break;
                }
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
                sm.addLong(ClanEntryManager.getInstance().getClanLockTime(clan.getId()));
                activeChar.sendPacket(sm);
                break;
            }
        }
    }
}
