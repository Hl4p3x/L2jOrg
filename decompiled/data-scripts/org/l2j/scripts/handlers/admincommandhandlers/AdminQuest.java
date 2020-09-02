// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Arrays;
import io.github.joealisson.primitive.IntSet;
import java.util.Iterator;
import java.util.Set;
import org.l2j.gameserver.model.quest.QuestTimer;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.IntFunction;
import java.util.Objects;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import org.l2j.gameserver.model.events.EventType;
import java.util.TreeSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import java.nio.file.Paths;
import org.l2j.gameserver.engine.scripting.ScriptEngineManager;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.quest.Quest;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminQuest implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] ADMIN_COMMANDS;
    
    private static Quest findScript(final String script) {
        if (Util.isDigit(script)) {
            return QuestManager.getInstance().getQuest(Integer.parseInt(script));
        }
        return QuestManager.getInstance().getQuest(script);
    }
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_quest_reload")) {
            final StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
            if (!st.hasMoreTokens()) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //quest_reload <questName> or <questId>");
                return false;
            }
            final String script = st.nextToken();
            final Quest quest = findScript(script);
            if (quest == null) {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, script));
                return false;
            }
            if (!quest.reload()) {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, script));
                return false;
            }
            BuilderUtil.sendSysMessage(activeChar, "Script successful reloaded.");
        }
        else if (command.startsWith("admin_script_load")) {
            final StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
            if (!st.hasMoreTokens()) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //script_load path/to/script.java");
                return false;
            }
            final String script = st.nextToken();
            try {
                ScriptEngineManager.getInstance().executeScript(Paths.get(script, new String[0]));
                BuilderUtil.sendSysMessage(activeChar, "Script loaded seccessful!");
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Failed to load script!");
                AdminQuest.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, script), (Throwable)e);
            }
        }
        else if (command.startsWith("admin_script_unload")) {
            final StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
            if (!st.hasMoreTokens()) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //script_load path/to/script.java");
                return false;
            }
            final String script = st.nextToken();
            final Quest quest = findScript(script);
            if (quest == null) {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, script));
                return false;
            }
            quest.unload();
            BuilderUtil.sendSysMessage(activeChar, "Script successful unloaded!");
        }
        else if (command.startsWith("admin_script_dir")) {
            final String[] parts = command.split(" ");
            if (parts.length == 1) {
                this.showDir(null, activeChar);
            }
            else {
                this.showDir(parts[1], activeChar);
            }
        }
        else if (command.startsWith("admin_show_quests")) {
            if (activeChar.getTarget() == null) {
                BuilderUtil.sendSysMessage(activeChar, "Get a target first.");
            }
            else if (!GameUtils.isCreature(activeChar.getTarget())) {
                BuilderUtil.sendSysMessage(activeChar, "Invalid Target.");
            }
            else {
                final Creature character = (Creature)activeChar.getTarget();
                final StringBuilder sb = new StringBuilder();
                final Set<String> questNames = new TreeSet<String>();
                for (final EventType type : EventType.values()) {
                    for (final AbstractEventListener listener : character.getListeners(type)) {
                        if (listener.getOwner() instanceof Quest) {
                            final Quest quest2 = (Quest)listener.getOwner();
                            if (!questNames.add(quest2.getName())) {
                                continue;
                            }
                            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, quest2.getName(), quest2.getName()));
                        }
                    }
                }
                final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
                msg.setFile(activeChar, "data/html/admin/npc-quests.htm");
                msg.replace("%quests%", sb.toString());
                msg.replace("%objid%", character.getObjectId());
                msg.replace("%questName%", "");
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)msg });
            }
        }
        else if (command.startsWith("admin_quest_info ")) {
            final String questName = command.substring("admin_quest_info ".length());
            final Quest quest3 = QuestManager.getInstance().getQuest(questName);
            String events = "";
            String npcs = "";
            String items = "";
            String timers = "";
            int counter = 0;
            if (quest3 == null) {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, questName));
                return false;
            }
            final Set<EventType> listenerTypes = new TreeSet<EventType>();
            for (final AbstractEventListener listener2 : quest3.getListeners()) {
                if (listenerTypes.add(listener2.getType())) {
                    events = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, events, listener2.getType().name());
                    ++counter;
                }
                if (counter > 10) {
                    counter = 0;
                    break;
                }
            }
            final IntSet npcIds = quest3.getRegisteredIds(ListenerRegisterType.NPC);
            npcs = npcIds.stream().limit(50L).mapToObj((IntFunction<?>)Objects::toString).collect((Collector<? super Object, ?, String>)Collectors.joining(","));
            if (!events.isEmpty()) {
                events = invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, listenerTypes.size(), events.substring(2));
            }
            if (!npcs.isEmpty()) {
                npcs = invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, npcIds.size(), npcs.substring(2));
            }
            if (quest3.getRegisteredItemIds() != null) {
                for (final int itemId : quest3.getRegisteredItemIds()) {
                    items = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, items, itemId);
                    if (++counter > 20) {
                        counter = 0;
                        break;
                    }
                }
                items = invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, quest3.getRegisteredItemIds().length, items.substring(2));
            }
            for (final List<QuestTimer> list : quest3.getQuestTimers().values()) {
                for (final QuestTimer timer : list) {
                    timers = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;ZZLorg/l2j/gameserver/model/actor/instance/Player;Lorg/l2j/gameserver/model/actor/Npc;)Ljava/lang/String;, timers, timer.getName(), timer.getIsActive(), timer.getIsRepeating(), timer.getPlayer(), timer.getNpc());
                    if (++counter > 10) {
                        break;
                    }
                }
            }
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, quest3.getId()));
            sb2.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, quest3.getName()));
            sb2.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, quest3.getPath()));
            sb2.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, events));
            if (!npcs.isEmpty()) {
                sb2.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, npcs));
            }
            if (!items.isEmpty()) {
                sb2.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, items));
            }
            if (!timers.isEmpty()) {
                sb2.append("<tr><td colspan=\"4\"><table width=270 border=0 bgcolor=131210><tr><td width=270><font color=\"LEVEL\">Timers:</font> <font color=00FF00></font></td></tr></table></td></tr>");
                sb2.append(timers);
            }
            final NpcHtmlMessage msg2 = new NpcHtmlMessage(0, 1);
            msg2.setFile(activeChar, "data/html/admin/npc-quests.htm");
            msg2.replace("%quests%", sb2.toString());
            msg2.replace("%questName%", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, quest3.getName(), quest3.getName(), quest3.getName(), quest3.getName()));
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)msg2 });
        }
        return true;
    }
    
    private void showDir(final String dir, final Player activeChar) {
        String replace = null;
        String currentPath = "/";
        if (dir == null || dir.trim().isEmpty() || dir.contains("..")) {
            final StringBuilder sb = new StringBuilder(200);
            final File path = ScriptEngineManager.SCRIPT_FOLDER.toFile();
            final String[] children = path.list();
            Arrays.sort(children);
            for (final String c : children) {
                final File n = new File(path, c);
                if (!n.isHidden()) {
                    if (!n.getName().startsWith(".")) {
                        if (n.isDirectory()) {
                            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, c, c));
                        }
                        else if (c.endsWith(".java") || c.endsWith(".py")) {
                            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, c, c));
                        }
                    }
                }
            }
            replace = sb.toString();
        }
        else {
            final File path = new File(ScriptEngineManager.SCRIPT_FOLDER.toFile(), dir);
            if (!path.isDirectory()) {
                BuilderUtil.sendSysMessage(activeChar, "Wrong path.");
                return;
            }
            currentPath = dir;
            final boolean questReducedNames = currentPath.equalsIgnoreCase("quests");
            final StringBuilder sb2 = new StringBuilder(200);
            sb2.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getUpPath(currentPath)));
            final String[] children2 = path.list();
            Arrays.sort(children2);
            for (final String c2 : children2) {
                final File n2 = new File(path, c2);
                if (!n2.isHidden()) {
                    if (!n2.getName().startsWith(".")) {
                        if (n2.isDirectory()) {
                            sb2.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, currentPath, c2, questReducedNames ? this.getQuestName(c2) : c2));
                        }
                        else if (c2.endsWith(".java") || c2.endsWith(".py")) {
                            sb2.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, currentPath, c2, c2));
                        }
                    }
                }
            }
            replace = sb2.toString();
            if (questReducedNames) {
                currentPath = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, currentPath);
            }
        }
        if (replace.length() > 17200) {
            replace = replace.substring(0, 17200);
        }
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(activeChar, "data/html/admin/scriptdirectory.htm");
        html.replace("%path%", currentPath);
        html.replace("%list%", replace);
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    private String getUpPath(final String full) {
        final int index = full.lastIndexOf("/");
        if (index == -1) {
            return "";
        }
        return full.substring(0, index);
    }
    
    private String getQuestName(final String full) {
        return full.split("_")[0];
    }
    
    public String[] getAdminCommandList() {
        return AdminQuest.ADMIN_COMMANDS;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminQuest.class);
        ADMIN_COMMANDS = new String[] { "admin_quest_reload", "admin_script_load", "admin_script_unload", "admin_script_dir", "admin_show_quests", "admin_quest_info" };
    }
}
