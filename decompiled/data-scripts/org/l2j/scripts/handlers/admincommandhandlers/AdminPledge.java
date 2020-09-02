// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.Iterator;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.GMViewPledgeInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminPledge implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command);
        final String cmd = st.nextToken();
        final WorldObject target = activeChar.getTarget();
        final Player targetPlayer = GameUtils.isPlayer(target) ? ((Player)target) : null;
        Clan clan = (targetPlayer != null) ? targetPlayer.getClan() : null;
        if (targetPlayer == null) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            this.showMainPage(activeChar);
            return false;
        }
        final String s = cmd;
        switch (s) {
            case "admin_pledge": {
                if (!st.hasMoreTokens()) {
                    BuilderUtil.sendSysMessage(activeChar, "Missing parameters!");
                    break;
                }
                final String action = st.nextToken();
                if (!st.hasMoreTokens()) {
                    BuilderUtil.sendSysMessage(activeChar, "Missing parameters!");
                    break;
                }
                final String param = st.nextToken();
                final String s2 = action;
                switch (s2) {
                    case "create": {
                        if (clan != null) {
                            BuilderUtil.sendSysMessage(activeChar, "Target player has clan!");
                            break;
                        }
                        final long penalty = targetPlayer.getClanCreateExpiryTime();
                        targetPlayer.setClanCreateExpiryTime(0L);
                        clan = ClanTable.getInstance().createClan(targetPlayer, param);
                        if (clan != null) {
                            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, param, targetPlayer.getName()));
                            break;
                        }
                        targetPlayer.setClanCreateExpiryTime(penalty);
                        BuilderUtil.sendSysMessage(activeChar, "There was a problem while creating the clan.");
                        break;
                    }
                    case "dismiss": {
                        if (clan == null) {
                            BuilderUtil.sendSysMessage(activeChar, "Target player has no clan!");
                            break;
                        }
                        if (!targetPlayer.isClanLeader()) {
                            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER);
                            sm.addString(targetPlayer.getName());
                            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)sm });
                            this.showMainPage(activeChar);
                            return false;
                        }
                        ClanTable.getInstance().destroyClan(clan);
                        clan = targetPlayer.getClan();
                        if (clan == null) {
                            BuilderUtil.sendSysMessage(activeChar, "Clan disbanded.");
                            break;
                        }
                        BuilderUtil.sendSysMessage(activeChar, "There was a problem while destroying the clan.");
                        break;
                    }
                    case "info": {
                        if (clan == null) {
                            BuilderUtil.sendSysMessage(activeChar, "Target player has no clan!");
                            break;
                        }
                        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new GMViewPledgeInfo(clan, targetPlayer) });
                        break;
                    }
                    case "setlevel": {
                        if (clan == null) {
                            BuilderUtil.sendSysMessage(activeChar, "Target player has no clan!");
                            break;
                        }
                        if (param == null) {
                            BuilderUtil.sendSysMessage(activeChar, "Usage: //pledge <setlevel|rep> <number>");
                            break;
                        }
                        final int level = Integer.parseInt(param);
                        if (level >= 0 && level < 12) {
                            clan.changeLevel(level);
                            for (final Player member : clan.getOnlineMembers(0)) {
                                member.broadcastUserInfo(new UserInfoType[] { UserInfoType.RELATION, UserInfoType.CLAN });
                            }
                            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, level, clan.getName()));
                            break;
                        }
                        BuilderUtil.sendSysMessage(activeChar, "Level incorrect.");
                        break;
                    }
                    case "rep": {
                        if (clan == null) {
                            BuilderUtil.sendSysMessage(activeChar, "Target player has no clan!");
                            break;
                        }
                        if (clan.getLevel() < 5) {
                            BuilderUtil.sendSysMessage(activeChar, "Only clans of level 5 or above may receive reputation points.");
                            this.showMainPage(activeChar);
                            return false;
                        }
                        try {
                            final int points = Integer.parseInt(param);
                            clan.addReputationScore(points, true);
                            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, (points > 0) ? "add " : "remove ", Math.abs(points), (points > 0) ? "to " : "from ", clan.getName(), clan.getReputationScore()));
                        }
                        catch (Exception e) {
                            BuilderUtil.sendSysMessage(activeChar, "Usage: //pledge <rep> <number>");
                        }
                        break;
                    }
                }
                break;
            }
        }
        this.showMainPage(activeChar);
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminPledge.ADMIN_COMMANDS;
    }
    
    private void showMainPage(final Player activeChar) {
        AdminHtml.showAdminHtml(activeChar, "game_menu.htm");
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_pledge" };
    }
}
