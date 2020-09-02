// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.WorldObject;
import java.util.Iterator;
import java.util.Map;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.Set;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.util.Broadcast;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.entity.Event;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminEventEngine implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    private static String tempBuffer;
    private static String tempName;
    private static boolean npcsDeleted;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        StringTokenizer st = new StringTokenizer(command);
        final String actualCommand = st.nextToken();
        try {
            if (actualCommand.equals("admin_event")) {
                if (Event.eventState != Event.EventState.OFF) {
                    this.showEventControl(activeChar);
                }
                else {
                    this.showMainPage(activeChar);
                }
            }
            else if (actualCommand.equals("admin_event_new")) {
                this.showNewEventPage(activeChar);
            }
            else if (actualCommand.startsWith("admin_add")) {
                AdminEventEngine.tempBuffer = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, AdminEventEngine.tempBuffer, command.substring(10));
                this.showNewEventPage(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_see")) {
                final String eventName = command.substring(16);
                try {
                    final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
                    final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(invokedynamic(makeConcatWithConstants:(Ljava/io/File;Ljava/lang/String;)Ljava/lang/String;, Config.DATAPACK_ROOT, eventName))));
                    final BufferedReader inbr = new BufferedReader(new InputStreamReader(in));
                    adminReply.setFile((Player)null, "data/html/mods/EventEngine/Participation.htm");
                    adminReply.replace("%eventName%", eventName);
                    adminReply.replace("%eventCreator%", inbr.readLine());
                    adminReply.replace("%eventInfo%", inbr.readLine());
                    adminReply.replace("npc_%objectId%_event_participate", "admin_event");
                    adminReply.replace("button value=\"Participate\"", "button value=\"Back\"");
                    activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
                    inbr.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (actualCommand.startsWith("admin_event_del")) {
                final String eventName = command.substring(16);
                final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/io/File;Ljava/lang/String;)Ljava/lang/String;, Config.DATAPACK_ROOT, eventName));
                file.delete();
                this.showMainPage(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_name")) {
                AdminEventEngine.tempName = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, AdminEventEngine.tempName, command.substring(17));
                this.showNewEventPage(activeChar);
            }
            else if (actualCommand.equalsIgnoreCase("admin_delete_buffer")) {
                AdminEventEngine.tempBuffer = "";
                this.showNewEventPage(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_store")) {
                try {
                    final FileOutputStream file2 = new FileOutputStream(new File(Config.DATAPACK_ROOT, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, AdminEventEngine.tempName)));
                    final PrintStream p = new PrintStream(file2);
                    p.println(activeChar.getName());
                    p.println(AdminEventEngine.tempBuffer);
                    file2.close();
                    p.close();
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
                AdminEventEngine.tempBuffer = "";
                AdminEventEngine.tempName = "";
                this.showMainPage(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_set")) {
                Event._eventName = command.substring(16);
                this.showEventParameters(activeChar, 2);
            }
            else if (actualCommand.startsWith("admin_event_change_teams_number")) {
                this.showEventParameters(activeChar, Integer.parseInt(st.nextToken()));
            }
            else if (actualCommand.startsWith("admin_event_panel")) {
                this.showEventControl(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_announce")) {
                Event._npcId = Integer.parseInt(st.nextToken());
                Event._teamsNumber = Integer.parseInt(st.nextToken());
                String temp = " ";
                String temp2 = "";
                while (st.hasMoreElements()) {
                    temp = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, temp, st.nextToken());
                }
                st = new StringTokenizer(temp, "-");
                Integer i = 1;
                while (st.hasMoreElements()) {
                    temp2 = st.nextToken();
                    if (!temp2.equals(" ")) {
                        final Map teamNames = Event._teamNames;
                        final Integer n2 = i;
                        ++i;
                        teamNames.put(n2, temp2.substring(1, temp2.length() - 1));
                    }
                }
                activeChar.sendMessage(Event.startEventParticipation());
                Broadcast.toAllOnlinePlayers(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                final PlaySound _snd = new PlaySound(1, "B03_F", 0, 0, 0, 0, 0);
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)_snd });
                activeChar.broadcastPacket((ServerPacket)_snd);
                final NpcHtmlMessage adminReply2 = new NpcHtmlMessage(0, 1);
                final String replyMSG = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Event._eventName);
                adminReply2.setHtml(replyMSG);
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply2 });
            }
            else if (actualCommand.startsWith("admin_event_control_begin")) {
                activeChar.sendMessage(Event.startEvent());
                this.showEventControl(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_control_finish")) {
                activeChar.sendMessage(Event.finishEvent());
            }
            else if (actualCommand.startsWith("admin_event_control_teleport")) {
                while (st.hasMoreElements()) {
                    final int teamId = Integer.parseInt(st.nextToken());
                    for (final Player player : Event._teams.get(teamId)) {
                        player.setTitle((String)Event._teamNames.get(teamId));
                        player.teleToLocation((ILocational)activeChar.getLocation(), true, activeChar.getInstanceWorld());
                    }
                }
                this.showEventControl(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_control_sit")) {
                while (st.hasMoreElements()) {
                    for (final Player player2 : Event._teams.get(Integer.parseInt(st.nextToken()))) {
                        if (player2.getEventStatus() == null) {
                            continue;
                        }
                        player2.getEventStatus().setSitForced(!player2.getEventStatus().isSitForced());
                        if (player2.getEventStatus().isSitForced()) {
                            player2.sitDown();
                        }
                        else {
                            player2.standUp();
                        }
                    }
                }
                this.showEventControl(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_control_kill")) {
                while (st.hasMoreElements()) {
                    for (final Player player2 : Event._teams.get(Integer.parseInt(st.nextToken()))) {
                        player2.reduceCurrentHp((double)(player2.getMaxHp() + player2.getMaxCp() + 1), (Creature)activeChar, (Skill)null, DamageInfo.DamageType.OTHER);
                    }
                }
                this.showEventControl(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_control_res")) {
                while (st.hasMoreElements()) {
                    for (final Player player2 : Event._teams.get(Integer.parseInt(st.nextToken()))) {
                        if (player2 != null) {
                            if (!player2.isDead()) {
                                continue;
                            }
                            player2.restoreExp(100.0);
                            player2.doRevive();
                            player2.setCurrentHpMp((double)player2.getMaxHp(), (double)player2.getMaxMp());
                            player2.setCurrentCp((double)player2.getMaxCp());
                        }
                    }
                }
                this.showEventControl(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_control_transform")) {
                final int teamId = Integer.parseInt(st.nextToken());
                final int[] transIds = new int[st.countTokens()];
                int j = 0;
                while (st.hasMoreElements()) {
                    transIds[j++] = Integer.parseInt(st.nextToken());
                }
                for (final Player player3 : Event._teams.get(teamId)) {
                    final int transId = transIds[Rnd.get(transIds.length)];
                    if (!player3.transform(transId, true)) {
                        AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, transId));
                    }
                }
                this.showEventControl(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_control_untransform")) {
                while (st.hasMoreElements()) {
                    for (final Player player2 : Event._teams.get(Integer.parseInt(st.nextToken()))) {
                        player2.stopTransformation(true);
                    }
                }
                this.showEventControl(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_control_kick")) {
                if (st.hasMoreElements()) {
                    while (st.hasMoreElements()) {
                        final Player player4 = World.getInstance().findPlayer(st.nextToken());
                        if (player4 != null) {
                            Event.removeAndResetPlayer(player4);
                        }
                    }
                }
                else if (GameUtils.isPlayer(activeChar.getTarget())) {
                    Event.removeAndResetPlayer((Player)activeChar.getTarget());
                }
                this.showEventControl(activeChar);
            }
            else if (actualCommand.startsWith("admin_event_control_prize")) {
                final int[] teamIds = new int[st.countTokens() - 2];
                int k = 0;
                while (st.countTokens() - 2 > 0) {
                    teamIds[k++] = Integer.parseInt(st.nextToken());
                }
                final String[] n = st.nextToken().split("\\*");
                final int itemId = Integer.parseInt(st.nextToken());
                for (final int teamId2 : teamIds) {
                    this.rewardTeam(activeChar, teamId2, Integer.parseInt(n[0]), itemId, (n.length == 2) ? n[1] : "");
                }
                this.showEventControl(activeChar);
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
            AdminData.getInstance().broadcastMessageToGMs("EventEngine: Error! Possible blank boxes while executing a command which requires a value in the box?");
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminEventEngine.ADMIN_COMMANDS;
    }
    
    private String showStoredEvents() {
        final File dir = new File(Config.DATAPACK_ROOT, "/data/events");
        if (dir.isFile()) {
            return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, dir.getAbsolutePath());
        }
        String note = "";
        if (!dir.exists()) {
            note = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, dir.getAbsolutePath());
            if (!dir.mkdirs()) {
                note = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, note, dir.getAbsolutePath());
                return note;
            }
            note = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, note, dir.getAbsolutePath());
        }
        final String[] files = dir.list();
        final StringBuilder result = new StringBuilder(files.length * 500);
        result.append("<table>");
        for (final String fileName : files) {
            result.append("<tr><td align=center>");
            result.append(fileName);
            result.append(" </td></tr><tr><td><table cellspacing=0><tr><td><button value=\"Select Event\" action=\"bypass -h admin_event_set ");
            result.append(fileName);
            result.append("\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"View Event\" action=\"bypass -h admin_event_see ");
            result.append(fileName);
            result.append("\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"Delete Event\" action=\"bypass -h admin_event_del ");
            result.append(fileName);
            result.append("\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table></td></tr><tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>");
        }
        result.append("</table>");
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/StringBuilder;)Ljava/lang/String;, note, result);
    }
    
    private void showMainPage(final Player activeChar) {
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        final String replyMSG = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.showStoredEvents());
        adminReply.setHtml(replyMSG);
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void showNewEventPage(final Player activeChar) {
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        final StringBuilder replyMSG = new StringBuilder(512);
        replyMSG.append("<html><title>[ L2J EVENT ENGINE ]</title><body><br><br><center><font color=LEVEL>Event name:</font><br>");
        if (AdminEventEngine.tempName.isEmpty()) {
            replyMSG.append("You can also use //event_name text to insert a new title");
            replyMSG.append("<center><multiedit var=\"name\" width=260 height=24> <button value=\"Set Event Name\" action=\"bypass -h admin_event_name $name\" width=120 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        }
        else {
            replyMSG.append(AdminEventEngine.tempName);
        }
        replyMSG.append("<br><br><font color=LEVEL>Event description:</font><br></center>");
        if (AdminEventEngine.tempBuffer.isEmpty()) {
            replyMSG.append("You can also use //add text to add text or //delete_buffer to remove the text.");
        }
        else {
            replyMSG.append(AdminEventEngine.tempBuffer);
        }
        replyMSG.append("<center><multiedit var=\"txt\" width=270 height=100> <button value=\"Add text\" action=\"bypass -h admin_add $txt\" width=120 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        replyMSG.append("<button value=\"Remove text\" action=\"bypass -h admin_delete_buffer\" width=120 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        if (!AdminEventEngine.tempName.isEmpty() || !AdminEventEngine.tempBuffer.isEmpty()) {
            replyMSG.append("<br><button value=\"Store Event Data\" action=\"bypass -h admin_event_store\" width=160 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        }
        replyMSG.append("</center></body></html>");
        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void showEventParameters(final Player activeChar, final int teamnumbers) {
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        final StringBuilder sb = new StringBuilder();
        sb.append("<html><body><title>[ L2J EVENT ENGINE ]</title><br><center> Current event: <font color=\"LEVEL\">");
        sb.append(Event._eventName);
        sb.append("</font></center><br>INFO: To start an event, you must first set the number of teams, then type their names in the boxes and finally type the NPC ID that will be the event manager (can be any existing npc) next to the \"Announce Event!\" button.<br><table width=100%>");
        sb.append("<tr><td><button value=\"Announce Event!\" action=\"bypass -h admin_event_announce $event_npcid ");
        sb.append(teamnumbers);
        sb.append(" ");
        for (int i = 1; i - 1 < teamnumbers; ++i) {
            sb.append("$event_teams_name");
            sb.append(i);
            sb.append(" - ");
        }
        sb.append("\" width=140 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        sb.append("<td><edit var=\"event_npcid\" width=100 height=20></td></tr>");
        sb.append("<tr><td><button value=\"Set number of teams\" action=\"bypass -h admin_event_change_teams_number $event_teams_number\" width=140 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        sb.append("<td><edit var=\"event_teams_number\" width=100 height=20></td></tr>");
        sb.append("</table><br><center> <br><br>");
        sb.append("<font color=\"LEVEL\">Teams' names:</font><br><table width=100% cellspacing=8>");
        for (int i = 1; i - 1 < teamnumbers; ++i) {
            sb.append("<tr><td align=center>Team #");
            sb.append(i);
            sb.append(" name:</td><td><edit var=\"event_teams_name");
            sb.append(i);
            sb.append("\" width=150 height=15></td></tr>");
        }
        sb.append("</table></body></html>");
        adminReply.setHtml(sb.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void showEventControl(final Player activeChar) {
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        final StringBuilder sb = new StringBuilder();
        sb.append("<html><title>[ EVENT ENGINE ]</title><body><br><center>Current event: <font color=\"LEVEL\">");
        sb.append(Event._eventName);
        sb.append("</font></center><br><table cellspacing=-1 width=280><tr><td align=center>Type the team ID(s) that will be affected by the commands. Commands with '*' work with only 1 team ID in the field, while '!' - none.</td></tr><tr><td align=center><edit var=\"team_number\" width=100 height=15></td></tr>");
        sb.append("<tr><td>&nbsp;</td></tr><tr><td><table width=200>");
        if (!AdminEventEngine.npcsDeleted) {
            sb.append("<tr><td><button value=\"Start!\" action=\"bypass -h admin_event_control_begin\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Destroys all event npcs so no more people can't participate now on</font></td></tr>");
        }
        sb.append("<tr><td>&nbsp;</td></tr><tr><td><button value=\"Teleport\" action=\"bypass -h admin_event_control_teleport $team_number\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Teleports the specified team to your position</font></td></tr><tr><td>&nbsp;</td></tr><tr><td><button value=\"Sit/Stand\" action=\"bypass -h admin_event_control_sit $team_number\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Sits/Stands up the team</font></td></tr><tr><td>&nbsp;</td></tr><tr><td><button value=\"Kill\" action=\"bypass -h admin_event_control_kill $team_number\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Finish with the life of all the players in the selected team</font></td></tr><tr><td>&nbsp;</td></tr><tr><td><button value=\"Resurrect\" action=\"bypass -h admin_event_control_res $team_number\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Resurrect Team's members</font></td></tr><tr><td>&nbsp;</td></tr><tr><td><table cellspacing=-1><tr><td><button value=\"Transform*\" action=\"bypass -h admin_event_control_transform $team_number $transf_id\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr><tr><td><edit var=\"transf_id\" width=98 height=15></td></tr></table></td><td><font color=\"LEVEL\">Transforms the team into the transformation with the ID specified. Multiple IDs result in randomly chosen one for each player.</font></td></tr><tr><td>&nbsp;</td></tr><tr><td><button value=\"UnTransform\" action=\"bypass -h admin_event_control_untransform $team_number\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Untransforms the team</font></td></tr><tr><td>&nbsp;</td></tr><tr><td><table cellspacing=-1><tr><td><button value=\"Give Item\" action=\"bypass -h admin_event_control_prize $team_number $n $id\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><table><tr><td width=32>Num</td><td><edit var=\"n\" width=60 height=15></td></tr><tr><td>ID</td><td><edit var=\"id\" width=60 height=15></td></tr></table></td><td><font color=\"LEVEL\">Give the specified item id to every single member of the team, you can put 5*level, 5*kills or 5 in the number field for example</font></td></tr><tr><td>&nbsp;</td></tr><tr><td><table cellspacing=-1><tr><td><button value=\"Kick Player\" action=\"bypass -h admin_event_control_kick $player_name\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr><tr><td><edit var=\"player_name\" width=98 height=15></td></tr></table></td><td><font color=\"LEVEL\">Kicks the specified player(s) from the event. Blank field kicks target.</font></td></tr><tr><td>&nbsp;</td></tr><tr><td><button value=\"End!\" action=\"bypass -h admin_event_control_finish\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><font color=\"LEVEL\">Will finish the event teleporting back all the players</font></td></tr><tr><td>&nbsp;</td></tr></table></td></tr></table></body></html>");
        adminReply.setHtml(sb.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void rewardTeam(final Player activeChar, final int team, final int n, final int id, final String type) {
        int num = n;
        for (final Player player : Event._teams.get(team)) {
            if (type.equalsIgnoreCase("level")) {
                num = n * player.getLevel();
            }
            else if (type.equalsIgnoreCase("kills") && player.getEventStatus() != null) {
                num = n * player.getEventStatus().getKills().size();
            }
            else {
                num = n;
            }
            player.addItem("Event", id, (long)num, (WorldObject)activeChar, true);
            final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
            adminReply.setHtml("<html><body> CONGRATULATIONS! You should have been rewarded. </body></html>");
            player.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
        }
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_event", "admin_event_new", "admin_event_choose", "admin_event_store", "admin_event_set", "admin_event_change_teams_number", "admin_event_announce", "admin_event_panel", "admin_event_control_begin", "admin_event_control_teleport", "admin_add", "admin_event_see", "admin_event_del", "admin_delete_buffer", "admin_event_control_sit", "admin_event_name", "admin_event_control_kill", "admin_event_control_res", "admin_event_control_transform", "admin_event_control_untransform", "admin_event_control_prize", "admin_event_control_chatban", "admin_event_control_kick", "admin_event_control_finish" };
        AdminEventEngine.tempBuffer = "";
        AdminEventEngine.tempName = "";
        AdminEventEngine.npcsDeleted = false;
    }
}
