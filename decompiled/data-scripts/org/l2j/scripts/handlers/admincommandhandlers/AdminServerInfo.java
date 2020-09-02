// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.GameServer;
import java.util.Date;
import org.l2j.gameserver.engine.geo.settings.GeoEngineSettings;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.actor.instance.Player;
import java.text.SimpleDateFormat;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminServerInfo implements IAdminCommandHandler
{
    private static final SimpleDateFormat fmt;
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_serverinfo")) {
            final NpcHtmlMessage html = new NpcHtmlMessage();
            final Runtime RunTime = Runtime.getRuntime();
            final int mb = 1048576;
            html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/serverinfo.htm"));
            html.replace("%os_name%", System.getProperty("os.name"));
            html.replace("%os_ver%", System.getProperty("os.version"));
            html.replace("%slots%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, this.getPlayersCount("ALL"), ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).maximumOnlineUsers()));
            html.replace("%gameTime%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, WorldTimeController.getInstance().getGameHour(), WorldTimeController.getInstance().getGameMinute()));
            html.replace("%dayNight%", WorldTimeController.getInstance().isNight() ? "Night" : "Day");
            html.replace("%geodata%", ((GeoEngineSettings)Configurator.getSettings((Class)GeoEngineSettings.class)).isEnabledPathFinding() ? "Enabled" : "Disabled");
            html.replace("%serverTime%", AdminServerInfo.fmt.format(new Date(System.currentTimeMillis())));
            html.replace("%serverUpTime%", GameServer.getInstance().getUptime());
            html.replace("%onlineAll%", this.getPlayersCount("ALL"));
            html.replace("%offlineTrade%", this.getPlayersCount("OFF_TRADE"));
            html.replace("%onlineGM%", this.getPlayersCount("GM"));
            html.replace("%onlineReal%", this.getPlayersCount("ALL_REAL"));
            html.replace("%usedMem%", RunTime.maxMemory() / 1048576L - (RunTime.maxMemory() - RunTime.totalMemory() + RunTime.freeMemory()) / 1048576L);
            html.replace("%freeMem%", (RunTime.maxMemory() - RunTime.totalMemory() + RunTime.freeMemory()) / 1048576L);
            html.replace("%totalMem%", Runtime.getRuntime().maxMemory() / 1048576L);
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        return true;
    }
    
    private int getPlayersCount(final String type) {
        switch (type) {
            case "ALL": {
                return World.getInstance().getPlayers().size();
            }
            case "OFF_TRADE": {
                int offlineCount = 0;
                final Collection<Player> objs = (Collection<Player>)World.getInstance().getPlayers();
                for (final Player player : objs) {
                    if (player.getClient() == null) {
                        ++offlineCount;
                    }
                }
                return offlineCount;
            }
            case "GM": {
                int onlineGMcount = 0;
                for (final Player gm : AdminData.getInstance().getAllGms(true)) {
                    if (gm != null && gm.isOnline() && gm.getClient() != null) {
                        ++onlineGMcount;
                    }
                }
                return onlineGMcount;
            }
            case "ALL_REAL": {
                final Set<String> realPlayers = new HashSet<String>();
                for (final Player onlinePlayer : World.getInstance().getPlayers()) {
                    if (onlinePlayer != null && onlinePlayer.getClient() != null) {
                        realPlayers.add(onlinePlayer.getIPAddress());
                    }
                }
                return realPlayers.size();
            }
            default: {
                return 0;
            }
        }
    }
    
    public String[] getAdminCommandList() {
        return AdminServerInfo.ADMIN_COMMANDS;
    }
    
    static {
        fmt = new SimpleDateFormat("hh:mm a");
        ADMIN_COMMANDS = new String[] { "admin_serverinfo" };
    }
}
