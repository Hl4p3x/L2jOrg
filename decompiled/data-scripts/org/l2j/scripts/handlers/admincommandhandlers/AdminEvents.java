// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.model.quest.Event;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminEvents implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public String[] getAdminCommandList() {
        return AdminEvents.ADMIN_COMMANDS;
    }
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (activeChar == null) {
            return false;
        }
        String event_name = "";
        String _event_bypass = "";
        final StringTokenizer st = new StringTokenizer(command, " ");
        st.nextToken();
        if (st.hasMoreTokens()) {
            event_name = st.nextToken();
        }
        if (st.hasMoreTokens()) {
            _event_bypass = st.nextToken();
        }
        if (command.contains("_menu")) {
            this.showMenu(activeChar);
        }
        if (command.startsWith("admin_event_start")) {
            try {
                if (event_name != null) {
                    final Event event = (Event)QuestManager.getInstance().getQuest(event_name);
                    if (event != null) {
                        if (event.eventStart(activeChar)) {
                            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event_name));
                            return true;
                        }
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event_name));
                        return true;
                    }
                }
                return false;
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //event_start <eventname>");
                e.printStackTrace();
                return false;
            }
        }
        if (command.startsWith("admin_event_stop")) {
            try {
                if (event_name != null) {
                    final Event event = (Event)QuestManager.getInstance().getQuest(event_name);
                    if (event != null) {
                        if (event.eventStop()) {
                            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event_name));
                            return true;
                        }
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, event_name));
                        return true;
                    }
                }
                return false;
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //event_start <eventname>");
                e.printStackTrace();
                return false;
            }
        }
        if (command.startsWith("admin_event_bypass")) {
            try {
                if (event_name != null) {
                    final Event event = (Event)QuestManager.getInstance().getQuest(event_name);
                    if (event != null) {
                        event.eventBypass(activeChar, _event_bypass);
                    }
                }
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //event_bypass <eventname> <bypass>");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    private void showMenu(final Player activeChar) {
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(activeChar, "data/html/admin/gm_events.htm");
        final StringBuilder cList = new StringBuilder(500);
        for (final Quest event : QuestManager.getInstance().getScripts().values()) {
            if (event instanceof Event) {
                cList.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, event.getName(), event.getName(), event.getName()));
            }
        }
        html.replace("%LIST%", cList.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_event_menu", "admin_event_start", "admin_event_stop", "admin_event_start_menu", "admin_event_stop_menu", "admin_event_bypass" };
    }
}
