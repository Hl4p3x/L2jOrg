// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.data.database.data.ClanData;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ClanDAO;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class ClanWarsList implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public boolean useUserCommand(final int id, final Player player) {
        if (!Util.contains(ClanWarsList.COMMAND_IDS, id)) {
            return false;
        }
        final Clan clan = player.getClan();
        if (Objects.isNull(clan)) {
            player.sendPacket(SystemMessageId.NOT_JOINED_IN_ANY_CLAN);
            return false;
        }
        switch (id) {
            case 88: {
                this.sendAttackList(player);
                break;
            }
            case 89: {
                this.sendUnderAttackList(player);
                break;
            }
            default: {
                this.sendWarList(player);
                break;
            }
        }
        return true;
    }
    
    private void sendWarList(final Player player) {
        player.sendPacket(SystemMessageId.CLAN_WAR_LIST);
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).findWarList(player.getClanId()).forEach(clanData -> this.sendClanInfo(player, clanData));
        player.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
    }
    
    private void sendUnderAttackList(final Player player) {
        player.sendPacket(SystemMessageId.CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU);
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).findUnderAttackList(player.getClanId()).forEach(clanData -> this.sendClanInfo(player, clanData));
        player.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
    }
    
    private void sendAttackList(final Player player) {
        player.sendPacket(SystemMessageId.CLANS_YOU_VE_DECLARED_WAR_ON);
        ((ClanDAO)DatabaseAccess.getDAO((Class)ClanDAO.class)).findAttackList(player.getClanId()).forEach(clanData -> this.sendClanInfo(player, clanData));
        player.sendPacket(SystemMessageId.SEPARATOR_EQUALS);
    }
    
    private void sendClanInfo(final Player player, final ClanData clanData) {
        SystemMessage message;
        if (Util.isNotEmpty(clanData.getAllyName())) {
            message = (SystemMessage)((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.S1_S2_ALLIANCE).addString(clanData.getName())).addString(clanData.getAllyName());
        }
        else {
            message = (SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.S1_NO_ALLIANCE_EXISTS).addString(clanData.getName());
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)message });
    }
    
    public int[] getUserCommandList() {
        return ClanWarsList.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 88, 89, 90 };
    }
}
