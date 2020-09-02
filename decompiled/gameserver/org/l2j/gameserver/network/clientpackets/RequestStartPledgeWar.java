// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.ClanMember;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;

public final class RequestStartPledgeWar extends ClientPacket
{
    private String _pledgeName;
    
    public void readImpl() {
        this._pledgeName = this.readString();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final Clan clanDeclaringWar = player.getClan();
        if (clanDeclaringWar == null) {
            return;
        }
        if (clanDeclaringWar.getLevel() < 3 || clanDeclaringWar.getMembersCount() < Config.ALT_CLAN_MEMBERS_FOR_WAR) {
            ((GameClient)this.client).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_15_OR_GREATER));
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (!player.hasClanPrivilege(ClanPrivilege.CL_PLEDGE_WAR)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (clanDeclaringWar.getWarCount() >= 30) {
            ((GameClient)this.client).sendPacket(SystemMessageId.A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CAN_T_BE_MADE_AT_THE_SAME_TIME);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Clan clanDeclaredWar = ClanTable.getInstance().getClanByName(this._pledgeName);
        if (clanDeclaredWar == null) {
            ((GameClient)this.client).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_CANNOT_BE_DECLARED_AGAINST_A_CLAN_THAT_DOES_NOT_EXIST));
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (clanDeclaredWar == clanDeclaringWar) {
            ((GameClient)this.client).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN));
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (clanDeclaringWar.getAllyId() == clanDeclaredWar.getAllyId() && clanDeclaringWar.getAllyId() != 0) {
            ((GameClient)this.client).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CAN_T_BE_MADE));
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (clanDeclaredWar.getLevel() < 3 || clanDeclaredWar.getMembersCount() < Config.ALT_CLAN_MEMBERS_FOR_WAR) {
            ((GameClient)this.client).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_15_OR_GREATER));
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (clanDeclaredWar.getDissolvingExpiryTime() > System.currentTimeMillis()) {
            ((GameClient)this.client).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.A_CLAN_WAR_CAN_NOT_BE_DECLARED_AGAINST_A_CLAN_THAT_IS_BEING_DISSOLVED));
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final ClanWar clanWar = clanDeclaringWar.getWarWith(clanDeclaredWar.getId());
        if (clanWar == null) {
            final ClanWar newClanWar = new ClanWar(clanDeclaringWar, clanDeclaredWar);
            newClanWar.save();
            clanDeclaringWar.getMembers().stream().filter(Objects::nonNull).filter(ClanMember::isOnline).forEach(p -> p.getPlayerInstance().broadcastUserInfo(UserInfoType.CLAN));
            clanDeclaredWar.getMembers().stream().filter(Objects::nonNull).filter(ClanMember::isOnline).forEach(p -> p.getPlayerInstance().broadcastUserInfo(UserInfoType.CLAN));
            return;
        }
        if (clanWar.getClanWarState(clanDeclaringWar) == ClanWarState.WIN) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_CAN_T_DECLARE_A_WAR_BECAUSE_THE_21_DAY_PERIOD_HASN_T_PASSED_AFTER_A_DEFEAT_DECLARATION_WITH_THE_S1_CLAN);
            sm.addString(clanDeclaredWar.getName());
            ((GameClient)this.client).sendPacket(sm);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_DECLARE_WAR_AGAIN);
        sm.addString(clanDeclaredWar.getName());
        ((GameClient)this.client).sendPacket(sm);
        ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
    }
}
