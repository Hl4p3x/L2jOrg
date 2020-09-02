// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.function.Function;
import org.l2j.gameserver.model.ClanMember;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;

public final class RequestSurrenderPledgeWar extends ClientPacket
{
    private String _pledgeName;
    
    public void readImpl() {
        this._pledgeName = this.readString();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Clan myClan = activeChar.getClan();
        if (myClan == null) {
            return;
        }
        if (myClan.getMembers().stream().filter(Objects::nonNull).filter(ClanMember::isOnline).map((Function<? super ClanMember, ?>)ClanMember::getPlayerInstance).anyMatch(p -> !p.isInCombat())) {
            activeChar.sendPacket(SystemMessageId.A_CEASE_FIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final Clan targetClan = ClanTable.getInstance().getClanByName(this._pledgeName);
        if (targetClan == null) {
            activeChar.sendMessage("No such clan.");
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_PLEDGE_WAR)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final ClanWar clanWar = myClan.getWarWith(targetClan.getId());
        if (clanWar == null) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_NOT_DECLARED_A_CLAN_WAR_AGAINST_THE_CLAN_S1);
            sm.addString(targetClan.getName());
            activeChar.sendPacket(sm);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (clanWar.getState() == ClanWarState.BLOOD_DECLARATION) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_DECLARE_DEFEAT_AS_IT_HAS_NOT_BEEN_7_DAYS_SINCE_STARTING_A_CLAN_WAR_WITH_CLAN_S1);
            ((GameClient)this.client).sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        clanWar.cancel(activeChar, myClan);
    }
}
