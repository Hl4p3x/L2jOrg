// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.network.serverpackets.ExShowQuestMark;
import org.l2j.gameserver.network.serverpackets.QuestList;
import org.l2j.gameserver.enums.QuestType;
import java.util.Iterator;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.data.database.data.QuestData;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.QuestDAO;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminShowQuests implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    private static final String[] _states;
    
    public boolean useAdminCommand(final String command, final Player player) {
        final String[] cmdParams = command.split(" ");
        Player target = null;
        final String[] val = new String[4];
        if (cmdParams.length > 2) {
            final String playerName = cmdParams[1];
            target = World.getInstance().findPlayer(playerName);
            val[0] = "var";
            final String s = cmdParams[2];
            switch (s) {
                case "0": {
                    val[1] = "Start";
                    break;
                }
                case "1": {
                    val[1] = "Started";
                    break;
                }
                case "2": {
                    val[1] = "Completed";
                    break;
                }
                case "3": {
                    val[0] = "full";
                    break;
                }
                default: {
                    if (cmdParams[2].contains("_")) {
                        val[0] = "name";
                        val[1] = cmdParams[2];
                        break;
                    }
                    if (cmdParams.length > 3 && cmdParams[3].equals("custom")) {
                        val[0] = "custom";
                        val[1] = cmdParams[2];
                        break;
                    }
                    break;
                }
            }
        }
        else {
            final WorldObject targetObject = player.getTarget();
            if (GameUtils.isPlayer(targetObject)) {
                target = targetObject.getActingPlayer();
            }
        }
        if (Objects.isNull(target)) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            return false;
        }
        if (command.startsWith("admin_charquestmenu")) {
            if (Objects.nonNull(val[0])) {
                this.showQuestMenu(target, player, val);
            }
            else {
                showFirstQuestMenu(target, player);
            }
        }
        else if (command.startsWith("admin_setcharquest")) {
            if (cmdParams.length < 5) {
                return false;
            }
            val[0] = cmdParams[2];
            val[1] = cmdParams[3];
            val[2] = cmdParams[4];
            if (cmdParams.length == 6) {
                val[3] = cmdParams[5];
            }
            this.setQuestVar(target, player, val);
        }
        return true;
    }
    
    private static void showFirstQuestMenu(final Player target, final Player actor) {
        final StringBuilder replyMSG = new StringBuilder(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, target.getName()));
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        final int ID = target.getObjectId();
        replyMSG.append("Quest Menu for <font color=\"LEVEL\">").append(target.getName()).append("</font> (ID:").append(ID).append(")<br><center>");
        replyMSG.append("<table width=250><tr><td><button value=\"CREATED\" action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" 0\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        replyMSG.append("<tr><td><button value=\"STARTED\" action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" 1\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        replyMSG.append("<tr><td><button value=\"COMPLETED\" action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" 2\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        replyMSG.append("<tr><td><br><button value=\"All\" action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" 3\" width=85 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        replyMSG.append("<tr><td><br><br>Manual Edit by Quest number:<br></td></tr>");
        replyMSG.append("<tr><td><edit var=\"qn\" width=50 height=15><br><button value=\"Edit\" action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" $qn custom\" width=50 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        replyMSG.append("</table></center></body></html>");
        adminReply.setHtml(replyMSG.toString());
        actor.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void showQuestMenu(final Player target, final Player actor, final String[] val) {
        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        final String s = val[0];
        switch (s) {
            case "full": {
                this.fullQuestList(target, replyMSG);
                break;
            }
            case "name": {
                this.questListByName(target, val[1], replyMSG);
                break;
            }
            case "var": {
                this.questByStateValue(target, val[1], replyMSG);
                break;
            }
            case "custom": {
                this.questCustomList(target, val[1], replyMSG);
                break;
            }
        }
        adminReply.setHtml(replyMSG.toString());
        actor.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void questCustomList(final Player target, final String questId, final StringBuilder replyMSG) {
        boolean questFound = true;
        boolean questStatFound = true;
        final int qnumber = Integer.parseInt(questId);
        String qname = null;
        QuestState qs = null;
        final Quest quest = QuestManager.getInstance().getQuest(qnumber);
        if (Objects.nonNull(quest)) {
            qname = quest.getName();
            qs = target.getQuestState(qname);
        }
        else {
            questFound = false;
        }
        String state;
        if (Objects.nonNull(qs)) {
            state = AdminShowQuests._states[qs.getState()];
        }
        else {
            questStatFound = false;
            state = "N/A";
        }
        if (questFound) {
            replyMSG.append("Character: <font color=\"LEVEL\">").append(target.getName()).append("</font><br>Quest: <font color=\"LEVEL\">").append(qname).append("</font><br>State: <font color=\"LEVEL\">").append(state).append("</font><br><br>");
            if (questStatFound) {
                replyMSG.append("<center><table width=250><tr><td>Var</td><td>Value</td><td>New Value</td><td>&nbsp;</td></tr>");
                final int id = target.getObjectId();
                for (final QuestData questData : ((QuestDAO)DatabaseAccess.getDAO((Class)QuestDAO.class)).findByPlayerAndNameExcludeState(id, qname)) {
                    this.questDataToEditLine(target, qname, replyMSG, questData);
                }
                replyMSG.append("</table><br><br><table width=250><tr><td>Repeatable quest:</td><td>Unrepeatable quest:</td></tr>");
                replyMSG.append("<tr><td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(" ").append(qname).append(" state COMPLETED 1\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                replyMSG.append("<td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(" ").append(qname).append(" state COMPLETED 0\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
                replyMSG.append("</table><br><br><font color=\"ff0000\">Delete Quest from DB:</font><br><button value=\"Quest Delete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(" ").append(qname).append(" state DELETE\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
            }
            else {
                replyMSG.append("<center>Start this Quest for player:<br>");
                replyMSG.append("<button value=\"Create Quest\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(" ").append(qnumber).append(" state CREATE\" width=100 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br><br>");
                replyMSG.append("<font color=\"ee0000\">Only for Unrepeateble quests:</font><br>");
                replyMSG.append("<button value=\"Create & Complete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(" ").append(qnumber).append(" state CC\" width=130 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br><br>");
            }
            replyMSG.append("</center></body></html>");
        }
        else {
            replyMSG.append("<center><font color=\"ee0000\">Quest with number </font><font color=\"LEVEL\">").append(qnumber).append("</font><font color=\"ee0000\"> doesn't exist!</font></center></body></html>");
        }
    }
    
    private void questByStateValue(final Player target, final String value, final StringBuilder replyMSG) {
        replyMSG.append("Character: <font color=\"LEVEL\">").append(target.getName()).append("</font><br>Quests with state: <font color=\"LEVEL\">").append(value).append("</font><br>");
        replyMSG.append("<table width=250>");
        ((QuestDAO)DatabaseAccess.getDAO((Class)QuestDAO.class)).findQuestNameByPlayerAndStateValue(target.getObjectId(), value).forEach(questName -> replyMSG.append("<tr><td><a action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" ").append(questName).append("\">").append(questName).append("</a></td></tr>"));
        replyMSG.append("</table></body></html>");
    }
    
    private void fullQuestList(final Player target, final StringBuilder replyMSG) {
        final int id = target.getObjectId();
        replyMSG.append("<table width=250><tr><td>Full Quest List for <font color=\"LEVEL\">").append(target.getName()).append("</font> (ID:").append(id).append(")</td></tr>");
        ((QuestDAO)DatabaseAccess.getDAO((Class)QuestDAO.class)).findQuestNameByPlayerAndState(id).forEach(questName -> replyMSG.append("<tr><td><a action=\"bypass -h admin_charquestmenu ").append(target.getName()).append(" ").append(questName).append("\">").append(questName).append("</a></td></tr>"));
        replyMSG.append("</table></body></html>");
    }
    
    private void questListByName(final Player target, final String name, final StringBuilder replyMSG) {
        final QuestState qs = target.getQuestState(name);
        final String state = Objects.nonNull(qs) ? AdminShowQuests._states[qs.getState()] : "CREATED";
        final int id = target.getObjectId();
        replyMSG.append("Player: <font color=\"LEVEL\">").append(target.getName()).append("</font><br>Quest: <font color=\"LEVEL\">").append(name).append("</font><br>State: <font color=\"LEVEL\">").append(state).append("</font><br><br>");
        replyMSG.append("<center><table width=250><tr><td>Var</td><td>Value</td><td>New Value</td><td>&nbsp;</td></tr>");
        ((QuestDAO)DatabaseAccess.getDAO((Class)QuestDAO.class)).findByPlayerAndNameExcludeState(id, name).forEach(questData -> this.questDataToEditLine(target, name, replyMSG, questData));
        replyMSG.append("</table><br><br><table width=250><tr><td>Repeatable quest:</td><td>Unrepeatable quest:</td></tr>");
        replyMSG.append("<tr><td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(" ").append(name).append(" state COMPLETED 1\" width=120 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
        replyMSG.append("<td><button value=\"Quest Complete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(" ").append(name).append(" state COMPLETED 0\" width=120 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        replyMSG.append("</table><br><br><font color=\"ff0000\">Delete Quest from DB:</font><br><button value=\"Quest Delete\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(" ").append(name).append(" state DELETE\" width=120 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        replyMSG.append("</center></body></html>");
    }
    
    private void questDataToEditLine(final Player target, final String name, final StringBuilder replyMSG, final QuestData questData) {
        replyMSG.append("<tr><td>").append(questData.getVar()).append("</td><td>").append(questData.getValue()).append("</td><td><edit var=\"var").append(questData.getVar()).append("\" width=80 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(" ").append(name).append(" ").append(questData.getVar()).append(" $var").append(questData.getVar()).append("\" width=30 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"Del\" action=\"bypass -h admin_setcharquest ").append(target.getName()).append(" ").append(name).append(" ").append(questData.getVar()).append(" delete\" width=30 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
    }
    
    private void setQuestVar(final Player target, final Player actor, final String[] val) {
        QuestState qs = target.getQuestState(val[0]);
        final String[] outval = new String[3];
        if (val[1].equals("state")) {
            final String s = val[2];
            switch (s) {
                case "COMPLETED": {
                    qs.exitQuest(val[3].equals("1") ? QuestType.REPEATABLE : QuestType.ONE_TIME);
                    break;
                }
                case "DELETE": {
                    Quest.deleteQuestInDb(qs, true);
                    qs.exitQuest(QuestType.REPEATABLE);
                    target.sendPacket(new ServerPacket[] { (ServerPacket)new QuestList(target) });
                    target.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()) });
                    break;
                }
                case "CREATE": {
                    qs = QuestManager.getInstance().getQuest(Integer.parseInt(val[0])).newQuestState(target);
                    qs.setState((byte)1);
                    qs.set("cond", "1");
                    target.sendPacket(new ServerPacket[] { (ServerPacket)new QuestList(target) });
                    target.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()) });
                    val[0] = qs.getQuest().getName();
                    break;
                }
                case "CC": {
                    qs = QuestManager.getInstance().getQuest(Integer.parseInt(val[0])).newQuestState(target);
                    qs.exitQuest(QuestType.ONE_TIME);
                    target.sendPacket(new ServerPacket[] { (ServerPacket)new QuestList(target) });
                    target.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()) });
                    val[0] = qs.getQuest().getName();
                    break;
                }
            }
        }
        else {
            if (val[2].equals("delete")) {
                qs.unset(val[1]);
            }
            else {
                qs.set(val[1], val[2]);
            }
            target.sendPacket(new ServerPacket[] { (ServerPacket)new QuestList(target) });
            target.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowQuestMark(qs.getQuest().getId(), qs.getCond()) });
        }
        actor.sendMessage("");
        outval[0] = "name";
        outval[1] = val[0];
        this.showQuestMenu(target, actor, outval);
    }
    
    public String[] getAdminCommandList() {
        return AdminShowQuests.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_charquestmenu", "admin_setcharquest" };
        _states = new String[] { "CREATED", "STARTED", "COMPLETED" };
    }
}
