// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.ServerType;
import org.l2j.commons.util.Util;
import java.util.StringTokenizer;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminLogin implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_server_gm_only")) {
            this.gmOnly();
            BuilderUtil.sendSysMessage(activeChar, "Server is now GM only");
            this.showMainPage(activeChar);
        }
        else if (command.equals("admin_server_all")) {
            this.allowToAll();
            BuilderUtil.sendSysMessage(activeChar, "Server is not GM only anymore");
            this.showMainPage(activeChar);
        }
        else if (command.startsWith("admin_server_max_player")) {
            final StringTokenizer st = new StringTokenizer(command);
            if (st.countTokens() > 1) {
                st.nextToken();
                final String number = st.nextToken();
                try {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, number));
                    this.showMainPage(activeChar);
                }
                catch (NumberFormatException e) {
                    BuilderUtil.sendSysMessage(activeChar, "Max players must be a number.");
                }
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, "Format is server_max_player <max>");
            }
        }
        else if (command.startsWith("admin_server_list_type")) {
            final StringTokenizer st = new StringTokenizer(command);
            final int tokens = st.countTokens();
            if (tokens > 1) {
                st.nextToken();
                final String[] modes = new String[tokens - 1];
                boolean isNumeric = true;
                for (int i = 0; i < tokens - 1; ++i) {
                    final boolean b = isNumeric;
                    final String[] array = modes;
                    final int n = i;
                    final String trim = st.nextToken().trim();
                    array[n] = trim;
                    isNumeric = (b & Util.isInteger(trim));
                }
                int newType = 0;
                if (isNumeric) {
                    for (final String mode : modes) {
                        newType |= Integer.parseInt(mode);
                    }
                }
                else {
                    newType = ServerType.maskOf(modes);
                }
                final ServerSettings serverSettings = (ServerSettings)Configurator.getSettings((Class)ServerSettings.class);
                if (serverSettings.type() != newType) {
                    serverSettings.setType(newType);
                    AuthServerCommunication.getInstance().sendServerType(newType);
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getServerTypeName(newType)));
                    this.showMainPage(activeChar);
                }
                else {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getServerTypeName(newType)));
                    this.showMainPage(activeChar);
                }
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, "Format is server_list_type <Normal | Relax | Test | Restricted | Event | Free | New |Classic>");
            }
        }
        else if (command.startsWith("admin_server_list_age")) {
            final StringTokenizer st = new StringTokenizer(command);
            if (st.countTokens() > 1) {
                st.nextToken();
                final String mode2 = st.nextToken();
                try {
                    final int age = Integer.parseInt(mode2);
                    if (Config.SERVER_LIST_AGE != age) {
                        Config.SERVER_LIST_AGE = age;
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, age));
                        this.showMainPage(activeChar);
                    }
                    else {
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, age));
                        this.showMainPage(activeChar);
                    }
                }
                catch (NumberFormatException e2) {
                    BuilderUtil.sendSysMessage(activeChar, "Age must be a number");
                }
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, "Format is server_list_age <number>");
            }
        }
        else if (command.equals("admin_server_login")) {
            this.showMainPage(activeChar);
        }
        return true;
    }
    
    private void showMainPage(final Player activeChar) {
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(activeChar, "data/html/admin/login.htm");
        html.replace("%type%", this.getServerTypeName(((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).type()));
        html.replace("%brackets%", String.valueOf(Config.SERVER_LIST_BRACKET));
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    private String getServerTypeName(final int serverType) {
        return Arrays.stream(ServerType.values()).filter(type -> (serverType & type.getMask()) != 0x0).map((Function<? super ServerType, ?>)Enum::toString).collect((Collector<? super Object, ?, String>)Collectors.joining(", "));
    }
    
    private void allowToAll() {
        Config.SERVER_GMONLY = false;
    }
    
    private void gmOnly() {
        Config.SERVER_GMONLY = true;
    }
    
    public String[] getAdminCommandList() {
        return AdminLogin.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_server_gm_only", "admin_server_all", "admin_server_max_player", "admin_server_list_type", "admin_server_list_age", "admin_server_login" };
    }
}
