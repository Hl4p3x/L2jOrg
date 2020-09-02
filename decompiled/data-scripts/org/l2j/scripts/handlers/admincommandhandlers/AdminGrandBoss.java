// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.StatsSet;
import java.util.Objects;
import java.text.SimpleDateFormat;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.NoRestartZone;
import org.l2j.gameserver.instancemanager.GrandBossManager;
import java.util.Arrays;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminGrandBoss implements IAdminCommandHandler
{
    private static final int ANTHARAS = 29068;
    private static final int ANTHARAS_ZONE = 70050;
    private static final int VALAKAS = 29028;
    private static final int BAIUM = 29020;
    private static final int BAIUM_ZONE = 70051;
    private static final int QUEENANT = 29001;
    private static final int ORFEN = 29014;
    private static final int CORE = 29006;
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        final String lowerCase = actualCommand.toLowerCase();
        switch (lowerCase) {
            case "admin_grandboss": {
                if (st.hasMoreTokens()) {
                    final int grandBossId = Integer.parseInt(st.nextToken());
                    this.manageHtml(activeChar, grandBossId);
                    break;
                }
                final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
                html.setHtml(HtmCache.getInstance().getHtm(activeChar, "data/html/admin/grandboss.htm"));
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
                break;
            }
            case "admin_grandboss_skip": {
                if (st.hasMoreTokens()) {
                    final int grandBossId = Integer.parseInt(st.nextToken());
                    if (grandBossId == 29068) {
                        this.antharasAi().notifyEvent("SKIP_WAITING", (Npc)null, activeChar);
                        this.manageHtml(activeChar, grandBossId);
                    }
                    else {
                        BuilderUtil.sendSysMessage(activeChar, "Wrong ID!");
                    }
                    break;
                }
                BuilderUtil.sendSysMessage(activeChar, "Usage: //grandboss_skip Id");
                break;
            }
            case "admin_grandboss_respawn": {
                if (st.hasMoreTokens()) {
                    final int grandBossId = Integer.parseInt(st.nextToken());
                    switch (grandBossId) {
                        case 29068: {
                            this.antharasAi().notifyEvent("RESPAWN_ANTHARAS", (Npc)null, activeChar);
                            this.manageHtml(activeChar, grandBossId);
                            break;
                        }
                        case 29020: {
                            this.baiumAi().notifyEvent("RESPAWN_BAIUM", (Npc)null, activeChar);
                            this.manageHtml(activeChar, grandBossId);
                            break;
                        }
                        default: {
                            BuilderUtil.sendSysMessage(activeChar, "Wrong ID!");
                            break;
                        }
                    }
                    break;
                }
                BuilderUtil.sendSysMessage(activeChar, "Usage: //grandboss_respawn Id");
                break;
            }
            case "admin_grandboss_minions": {
                if (st.hasMoreTokens()) {
                    final int grandBossId = Integer.parseInt(st.nextToken());
                    switch (grandBossId) {
                        case 29068: {
                            this.antharasAi().notifyEvent("DESPAWN_MINIONS", (Npc)null, activeChar);
                            break;
                        }
                        case 29020: {
                            this.baiumAi().notifyEvent("DESPAWN_MINIONS", (Npc)null, activeChar);
                            break;
                        }
                        default: {
                            BuilderUtil.sendSysMessage(activeChar, "Wrong ID!");
                            break;
                        }
                    }
                    break;
                }
                BuilderUtil.sendSysMessage(activeChar, "Usage: //grandboss_minions Id");
                break;
            }
            case "admin_grandboss_abort": {
                if (st.hasMoreTokens()) {
                    final int grandBossId = Integer.parseInt(st.nextToken());
                    switch (grandBossId) {
                        case 29068: {
                            this.antharasAi().notifyEvent("ABORT_FIGHT", (Npc)null, activeChar);
                            this.manageHtml(activeChar, grandBossId);
                            break;
                        }
                        case 29020: {
                            this.baiumAi().notifyEvent("ABORT_FIGHT", (Npc)null, activeChar);
                            this.manageHtml(activeChar, grandBossId);
                            break;
                        }
                        default: {
                            BuilderUtil.sendSysMessage(activeChar, "Wrong ID!");
                            break;
                        }
                    }
                    break;
                }
                BuilderUtil.sendSysMessage(activeChar, "Usage: //grandboss_abort Id");
                break;
            }
        }
        return true;
    }
    
    private void manageHtml(final Player activeChar, final int grandBossId) {
        if (Arrays.asList(29068, 29028, 29020, 29001, 29014, 29006).contains(grandBossId)) {
            final int bossStatus = GrandBossManager.getInstance().getBossStatus(grandBossId);
            NoRestartZone bossZone = null;
            String textColor = null;
            String text = null;
            String htmlPatch = null;
            int deadStatus = 0;
            switch (grandBossId) {
                case 29068: {
                    bossZone = (NoRestartZone)ZoneManager.getInstance().getZoneById(70050, (Class)NoRestartZone.class);
                    htmlPatch = "data/html/admin/grandboss_antharas.htm";
                    break;
                }
                case 29028: {
                    htmlPatch = "data/html/admin/grandboss_valakas.htm";
                    break;
                }
                case 29020: {
                    bossZone = (NoRestartZone)ZoneManager.getInstance().getZoneById(70051, (Class)NoRestartZone.class);
                    htmlPatch = "data/html/admin/grandboss_baium.htm";
                    break;
                }
                case 29001: {
                    htmlPatch = "data/html/admin/grandboss_queenant.htm";
                    break;
                }
                case 29014: {
                    htmlPatch = "data/html/admin/grandboss_orfen.htm";
                    break;
                }
                case 29006: {
                    htmlPatch = "data/html/admin/grandboss_core.htm";
                    break;
                }
            }
            if (Arrays.asList(29068, 29028, 29020).contains(grandBossId)) {
                deadStatus = 3;
                switch (bossStatus) {
                    case 0: {
                        textColor = "00FF00";
                        text = "Alive";
                        break;
                    }
                    case 1: {
                        textColor = "FFFF00";
                        text = "Waiting";
                        break;
                    }
                    case 2: {
                        textColor = "FF9900";
                        text = "In Fight";
                        break;
                    }
                    case 3: {
                        textColor = "FF0000";
                        text = "Dead";
                        break;
                    }
                    default: {
                        textColor = "FFFFFF";
                        text = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, bossStatus);
                        break;
                    }
                }
            }
            else {
                deadStatus = 1;
                switch (bossStatus) {
                    case 0: {
                        textColor = "00FF00";
                        text = "Alive";
                        break;
                    }
                    case 1: {
                        textColor = "FF0000";
                        text = "Dead";
                        break;
                    }
                    default: {
                        textColor = "FFFFFF";
                        text = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, bossStatus);
                        break;
                    }
                }
            }
            final StatsSet info = GrandBossManager.getInstance().getStatsSet(grandBossId);
            final String bossRespawn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(info.getLong("respawn_time"));
            final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
            html.setHtml(HtmCache.getInstance().getHtm(activeChar, htmlPatch));
            html.replace("%bossStatus%", text);
            html.replace("%bossColor%", textColor);
            html.replace("%respawnTime%", (bossStatus == deadStatus) ? bossRespawn : "Already respawned!");
            html.replace("%playersInside%", Objects.nonNull(bossZone) ? String.valueOf(bossZone.getPlayersInsideCount()) : "Zone not found!");
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        else {
            BuilderUtil.sendSysMessage(activeChar, "Wrong ID!");
        }
    }
    
    private Quest antharasAi() {
        return QuestManager.getInstance().getQuest("Antharas");
    }
    
    private Quest baiumAi() {
        return QuestManager.getInstance().getQuest("Baium");
    }
    
    public String[] getAdminCommandList() {
        return AdminGrandBoss.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_grandboss", "admin_grandboss_skip", "admin_grandboss_respawn", "admin_grandboss_minions", "admin_grandboss_abort" };
    }
}
