// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.util.GMAudit;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.Config;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.util.GameUtils;
import java.util.Date;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.cache.HtmCache;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminPunishment implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] ADMIN_COMMANDS;
    private static SimpleDateFormat DATE_FORMATTER;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        if (!st.hasMoreTokens()) {
            return false;
        }
        final String nextToken;
        final String cmd = nextToken = st.nextToken();
        switch (nextToken) {
            case "admin_punishment": {
                if (!st.hasMoreTokens()) {
                    String content = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/punishment.htm");
                    if (content != null) {
                        content = content.replaceAll("%punishments%", CommonUtil.implode((Object[])PunishmentType.values(), ";"));
                        content = content.replaceAll("%affects%", CommonUtil.implode((Object[])PunishmentAffect.values(), ";"));
                        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new NpcHtmlMessage(0, 1, content) });
                    }
                    else {
                        AdminPunishment.LOGGER.warn(": data/html/admin/punishment.htm is missing");
                    }
                    break;
                }
                final String nextToken2;
                final String subcmd = nextToken2 = st.nextToken();
                switch (nextToken2) {
                    case "info": {
                        String key = st.hasMoreTokens() ? st.nextToken() : null;
                        final String af = st.hasMoreTokens() ? st.nextToken() : null;
                        final String name = key;
                        if (key == null || af == null) {
                            BuilderUtil.sendSysMessage(activeChar, "Not enough data specified!");
                            break;
                        }
                        final PunishmentAffect affect = PunishmentAffect.getByName(af);
                        if (affect == null) {
                            BuilderUtil.sendSysMessage(activeChar, "Incorrect value specified for affect type!");
                            break;
                        }
                        if (affect == PunishmentAffect.CHARACTER) {
                            key = findCharId(key);
                        }
                        String content2 = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/punishment-info.htm");
                        if (content2 != null) {
                            final StringBuilder sb = new StringBuilder();
                            for (final PunishmentType type : PunishmentType.values()) {
                                if (PunishmentManager.getInstance().hasPunishment((Object)key, affect, type)) {
                                    final long expiration = PunishmentManager.getInstance().getPunishmentExpiration((Object)key, affect, type);
                                    String expire = "never";
                                    if (expiration > 0L) {
                                        synchronized (AdminPunishment.DATE_FORMATTER) {
                                            expire = AdminPunishment.DATE_FORMATTER.format(new Date(expiration));
                                        }
                                    }
                                    sb.append(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/punishment/PunishmentType;Ljava/lang/String;Ljava/lang/String;Lorg/l2j/gameserver/model/punishment/PunishmentAffect;Lorg/l2j/gameserver/model/punishment/PunishmentType;)Ljava/lang/String;, type, expire, name, affect, type));
                                }
                            }
                            content2 = content2.replaceAll("%player_name%", name);
                            content2 = content2.replaceAll("%punishments%", sb.toString());
                            content2 = content2.replaceAll("%affects%", CommonUtil.implode((Object[])PunishmentAffect.values(), ";"));
                            content2 = content2.replaceAll("%affect_type%", affect.name());
                            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new NpcHtmlMessage(0, 1, content2) });
                            break;
                        }
                        AdminPunishment.LOGGER.warn(": data/html/admin/punishment-info.htm is missing");
                        break;
                    }
                    case "player": {
                        Player target = null;
                        if (st.hasMoreTokens()) {
                            final String playerName = st.nextToken();
                            if (playerName.isEmpty() && !GameUtils.isPlayer(activeChar.getTarget())) {
                                return this.useAdminCommand("admin_punishment", activeChar);
                            }
                            target = World.getInstance().findPlayer(playerName);
                        }
                        if (target == null && !GameUtils.isPlayer(activeChar.getTarget())) {
                            BuilderUtil.sendSysMessage(activeChar, "You must target player!");
                            break;
                        }
                        if (target == null) {
                            target = activeChar.getTarget().getActingPlayer();
                        }
                        String content3 = HtmCache.getInstance().getHtm(activeChar, "data/html/admin/punishment-player.htm");
                        if (content3 != null) {
                            content3 = content3.replaceAll("%player_name%", target.getName());
                            content3 = content3.replaceAll("%punishments%", CommonUtil.implode((Object[])PunishmentType.values(), ";"));
                            content3 = content3.replaceAll("%acc%", target.getAccountName());
                            content3 = content3.replaceAll("%char%", target.getName());
                            content3 = content3.replaceAll("%ip%", target.getIPAddress());
                            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new NpcHtmlMessage(0, 1, content3) });
                            break;
                        }
                        AdminPunishment.LOGGER.warn(": data/html/admin/punishment-player.htm is missing");
                        break;
                    }
                }
                break;
            }
            case "admin_punishment_add": {
                String key2 = st.hasMoreTokens() ? st.nextToken() : null;
                final String af2 = st.hasMoreTokens() ? st.nextToken() : null;
                final String t = st.hasMoreTokens() ? st.nextToken() : null;
                final String exp = st.hasMoreTokens() ? st.nextToken() : null;
                String reason = st.hasMoreTokens() ? st.nextToken() : null;
                if (reason != null) {
                    while (st.hasMoreTokens()) {
                        reason = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, reason, st.nextToken());
                    }
                    if (!reason.isEmpty()) {
                        reason = reason.replaceAll("\\$", "\\\\\\$");
                        reason = reason.replaceAll("\r\n", "<br1>");
                        reason = reason.replace("<", "&lt;");
                        reason = reason.replace(">", "&gt;");
                    }
                }
                final String name = key2;
                if (key2 == null || af2 == null || t == null || exp == null || reason == null) {
                    BuilderUtil.sendSysMessage(activeChar, "Please fill all the fields!");
                    break;
                }
                if (!Util.isInteger(exp) && !exp.equals("-1")) {
                    BuilderUtil.sendSysMessage(activeChar, "Incorrect value specified for expiration time!");
                    break;
                }
                long expirationTime = Integer.parseInt(exp);
                if (expirationTime > 0L) {
                    expirationTime = System.currentTimeMillis() + expirationTime * 60L * 1000L;
                }
                final PunishmentAffect affect2 = PunishmentAffect.getByName(af2);
                final PunishmentType type2 = PunishmentType.getByName(t);
                if (affect2 == null || type2 == null) {
                    BuilderUtil.sendSysMessage(activeChar, "Incorrect value specified for affect/punishment type!");
                    break;
                }
                if (affect2 == PunishmentAffect.CHARACTER) {
                    key2 = findCharId(key2);
                }
                else if (affect2 == PunishmentAffect.IP) {
                    try {
                        final InetAddress addr = InetAddress.getByName(key2);
                        if (addr.isLoopbackAddress()) {
                            throw new UnknownHostException("You cannot ban any local address!");
                        }
                        if (Config.GAME_SERVER_HOSTS.contains(addr.getHostAddress())) {
                            throw new UnknownHostException("You cannot ban your gameserver's address!");
                        }
                    }
                    catch (UnknownHostException e) {
                        BuilderUtil.sendSysMessage(activeChar, "You've entered an incorrect IP address!");
                        activeChar.sendMessage(e.getMessage());
                        break;
                    }
                }
                if (PunishmentManager.getInstance().hasPunishment((Object)key2, affect2, type2)) {
                    BuilderUtil.sendSysMessage(activeChar, "Target is already affected by that punishment.");
                    break;
                }
                PunishmentManager.getInstance().startPunishment(new PunishmentTask((Object)key2, affect2, type2, expirationTime, reason, activeChar.getName()));
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/punishment/PunishmentAffect;Ljava/lang/String;)Ljava/lang/String;, type2.name(), affect2, name));
                GMAudit.auditGMAction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, activeChar.getName(), activeChar.getObjectId()), cmd, affect2.name(), name);
                return this.useAdminCommand(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, affect2.name()), activeChar);
            }
            case "admin_punishment_remove": {
                String key2 = st.hasMoreTokens() ? st.nextToken() : null;
                final String af2 = st.hasMoreTokens() ? st.nextToken() : null;
                final String t = st.hasMoreTokens() ? st.nextToken() : null;
                final String name2 = key2;
                if (key2 == null || af2 == null || t == null) {
                    BuilderUtil.sendSysMessage(activeChar, "Not enough data specified!");
                    break;
                }
                final PunishmentAffect affect3 = PunishmentAffect.getByName(af2);
                final PunishmentType type3 = PunishmentType.getByName(t);
                if (affect3 == null || type3 == null) {
                    BuilderUtil.sendSysMessage(activeChar, "Incorrect value specified for affect/punishment type!");
                    break;
                }
                if (affect3 == PunishmentAffect.CHARACTER) {
                    key2 = findCharId(key2);
                }
                if (!PunishmentManager.getInstance().hasPunishment((Object)key2, affect3, type3)) {
                    BuilderUtil.sendSysMessage(activeChar, "Target is not affected by that punishment!");
                    break;
                }
                PunishmentManager.getInstance().stopPunishment((Object)key2, affect3, type3);
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/model/punishment/PunishmentAffect;Ljava/lang/String;)Ljava/lang/String;, type3.name(), affect3, name2));
                GMAudit.auditGMAction(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, activeChar.getName(), activeChar.getObjectId()), cmd, affect3.name(), name2);
                return this.useAdminCommand(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name2, affect3.name()), activeChar);
            }
            case "admin_ban_char": {
                if (st.hasMoreTokens()) {
                    return this.useAdminCommand(String.format("admin_punishment_add %s %s %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.BAN, 0, "Banned by admin"), activeChar);
                }
            }
            case "admin_unban_char": {
                if (st.hasMoreTokens()) {
                    return this.useAdminCommand(String.format("admin_punishment_remove %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.BAN), activeChar);
                }
            }
            case "admin_ban_acc": {
                if (st.hasMoreTokens()) {
                    return this.useAdminCommand(String.format("admin_punishment_add %s %s %s %s %s", st.nextToken(), PunishmentAffect.ACCOUNT, PunishmentType.BAN, 0, "Banned by admin"), activeChar);
                }
            }
            case "admin_unban_acc": {
                if (st.hasMoreTokens()) {
                    return this.useAdminCommand(String.format("admin_punishment_remove %s %s %s", st.nextToken(), PunishmentAffect.ACCOUNT, PunishmentType.BAN), activeChar);
                }
            }
            case "admin_ban_chat": {
                if (st.hasMoreTokens()) {
                    return this.useAdminCommand(String.format("admin_punishment_add %s %s %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, 0, "Chat banned by admin"), activeChar);
                }
            }
            case "admin_unban_chat": {
                if (st.hasMoreTokens()) {
                    return this.useAdminCommand(String.format("admin_punishment_remove %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN), activeChar);
                }
            }
            case "admin_jail": {
                if (st.hasMoreTokens()) {
                    return this.useAdminCommand(String.format("admin_punishment_add %s %s %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.JAIL, 0, "Jailed by admin"), activeChar);
                }
            }
            case "admin_unjail": {
                if (st.hasMoreTokens()) {
                    return this.useAdminCommand(String.format("admin_punishment_remove %s %s %s", st.nextToken(), PunishmentAffect.CHARACTER, PunishmentType.JAIL), activeChar);
                }
                break;
            }
        }
        return true;
    }
    
    private static String findCharId(final String key) {
        final int charId = PlayerNameTable.getInstance().getIdByName(key);
        if (charId > 0) {
            return Integer.toString(charId);
        }
        return key;
    }
    
    public String[] getAdminCommandList() {
        return AdminPunishment.ADMIN_COMMANDS;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminPunishment.class);
        ADMIN_COMMANDS = new String[] { "admin_punishment", "admin_punishment_add", "admin_punishment_remove", "admin_ban_acc", "admin_unban_acc", "admin_ban_chat", "admin_unban_chat", "admin_ban_char", "admin_unban_char", "admin_jail", "admin_unjail" };
        AdminPunishment.DATE_FORMATTER = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    }
}
