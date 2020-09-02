// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import java.util.Iterator;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.network.GameClient;

public final class RequestStopPledgeWar extends ClientPacket
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
        final Clan playerClan = player.getClan();
        if (playerClan == null) {
            return;
        }
        final Clan clan = ClanTable.getInstance().getClanByName(this._pledgeName);
        if (clan == null) {
            player.sendMessage("No such clan.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (!playerClan.isAtWarWith(clan.getId())) {
            player.sendMessage("You aren't at war with this clan.");
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (!player.hasClanPrivilege(ClanPrivilege.CL_PLEDGE_WAR)) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        for (final ClanMember member : playerClan.getMembers()) {
            if (member != null) {
                if (member.getPlayerInstance() == null) {
                    continue;
                }
                if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(member.getPlayerInstance())) {
                    player.sendPacket(SystemMessageId.A_CEASE_FIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE);
                    return;
                }
                continue;
            }
        }
        playerClan.takeReputationScore(500, true);
        ClanTable.getInstance().deleteClanWars(playerClan.getId(), clan.getId());
        for (final Player member2 : playerClan.getOnlineMembers(0)) {
            member2.broadcastUserInfo();
        }
        for (final Player member2 : clan.getOnlineMembers(0)) {
            member2.broadcastUserInfo();
        }
    }
}
