// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.StringTokenizer;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import java.util.List;
import org.l2j.gameserver.Config;
import java.util.ArrayList;
import java.util.Iterator;
import org.l2j.gameserver.datatables.SchemeBufferTable;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class SchemeBuffer extends Folk
{
    private static final int PAGE_LIMIT = 6;
    
    public SchemeBuffer(final NpcTemplate template) {
        super(template);
    }
    
    private static String getTypesFrame(final String groupType, final String schemeName) {
        final StringBuilder sb = new StringBuilder(500);
        sb.append("<table>");
        int count = 0;
        for (final String type : SchemeBufferTable.getInstance().getSkillTypes()) {
            if (count == 0) {
                sb.append("<tr>");
            }
            if (groupType.equalsIgnoreCase(type)) {
                sb.append("<td width=65>").append(type).append("</td>");
            }
            else {
                sb.append("<td width=65><a action=\"bypass -h npc_%objectId%_editschemes;").append(type).append(";").append(schemeName).append(";1\">").append(type).append("</a></td>");
            }
            if (++count == 4) {
                sb.append("</tr>");
                count = 0;
            }
        }
        if (!sb.toString().endsWith("</tr>")) {
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }
    
    private static int getFee(final ArrayList<Integer> list) {
        if (Config.BUFFER_STATIC_BUFF_COST > 0) {
            return list.size() * Config.BUFFER_STATIC_BUFF_COST;
        }
        int fee = 0;
        for (final int sk : list) {
            fee += SchemeBufferTable.getInstance().getAvailableBuff(sk).getPrice();
        }
        return fee;
    }
    
    private static int countPagesNumber(final int objectsSize, final int pageSize) {
        return objectsSize / pageSize + ((objectsSize % pageSize != 0) ? 1 : 0);
    }
    
    private static long getCountOf(final List<Integer> skills, final boolean dances) {
        return skills.stream().filter(sId -> SkillEngine.getInstance().getSkill(sId, 1).isDance() == dances).count();
    }
    
    @Override
    public void onBypassFeedback(final Player player, String command) {
        command = command.replace("createscheme ", "createscheme;");
        final StringTokenizer st = new StringTokenizer(command, ";");
        final String currentCommand = st.nextToken();
        if (currentCommand.startsWith("menu")) {
            final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
            html.setFile(player, this.getHtmlPath(this.getId(), 0));
            html.replace("%objectId%", this.getObjectId());
            player.sendPacket(html);
        }
        else if (currentCommand.startsWith("cleanup")) {
            player.stopAllEffects();
            final Summon summon = player.getPet();
            if (summon != null) {
                summon.stopAllEffects();
            }
            final NpcHtmlMessage html2 = new NpcHtmlMessage(this.getObjectId());
            html2.setFile(player, this.getHtmlPath(this.getId(), 0));
            html2.replace("%objectId%", this.getObjectId());
            player.sendPacket(html2);
        }
        else if (currentCommand.startsWith("heal")) {
            player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
            final Summon summon = player.getPet();
            if (summon != null) {
                summon.setCurrentHpMp(summon.getMaxHp(), summon.getMaxMp());
            }
            final NpcHtmlMessage html2 = new NpcHtmlMessage(this.getObjectId());
            html2.setFile(player, this.getHtmlPath(this.getId(), 0));
            html2.replace("%objectId%", this.getObjectId());
            player.sendPacket(html2);
        }
        else if (currentCommand.startsWith("support")) {
            this.showGiveBuffsWindow(player);
        }
        else if (currentCommand.startsWith("givebuffs")) {
            final String schemeName = st.nextToken();
            final int cost = Integer.parseInt(st.nextToken());
            Creature target = null;
            if (st.hasMoreTokens()) {
                final String targetType = st.nextToken();
                if (targetType != null && targetType.equalsIgnoreCase("pet")) {
                    target = player.getPet();
                }
            }
            else {
                target = player;
            }
            if (target == null) {
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_PET);
            }
            else if (cost == 0 || player.reduceAdena("NPC Buffer", cost, this, true)) {
                for (final int skillId : SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName)) {
                    SkillEngine.getInstance().getSkill(skillId, SkillEngine.getInstance().getMaxLevel(skillId)).applyEffects(this, target);
                }
            }
        }
        else if (currentCommand.startsWith("editschemes")) {
            this.showEditSchemeWindow(player, st.nextToken(), st.nextToken(), Integer.parseInt(st.nextToken()));
        }
        else if (currentCommand.startsWith("skill")) {
            final String groupType = st.nextToken();
            final String schemeName2 = st.nextToken();
            final int skillId2 = Integer.parseInt(st.nextToken());
            final int page = Integer.parseInt(st.nextToken());
            final List<Integer> skills = SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName2);
            if (currentCommand.startsWith("skillselect") && !schemeName2.equalsIgnoreCase("none")) {
                final Skill skill = SkillEngine.getInstance().getSkill(skillId2, SkillEngine.getInstance().getMaxLevel(skillId2));
                if (skill.isDance()) {
                    if (getCountOf(skills, true) < Config.DANCES_MAX_AMOUNT) {
                        skills.add(skillId2);
                    }
                    else {
                        player.sendMessage("This scheme has reached the maximum amount of dances/songs.");
                    }
                }
                else if (getCountOf(skills, false) < player.getStats().getMaxBuffCount()) {
                    skills.add(skillId2);
                }
                else {
                    player.sendMessage("This scheme has reached the maximum amount of buffs.");
                }
            }
            else if (currentCommand.startsWith("skillunselect")) {
                skills.remove((Object)skillId2);
            }
            this.showEditSchemeWindow(player, groupType, schemeName2, page);
        }
        else if (currentCommand.startsWith("createscheme")) {
            try {
                final String schemeName = st.nextToken().trim();
                if (schemeName.length() > 14) {
                    player.sendMessage("Scheme's name must contain up to 14 chars.");
                    return;
                }
                if (!Util.isAlphaNumeric(schemeName.replace(" ", "").replace(".", "").replace(",", "").replace("-", "").replace("+", "").replace("!", "").replace("?", ""))) {
                    player.sendMessage("Please use plain alphanumeric characters.");
                    return;
                }
                final Map<String, ArrayList<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(player.getObjectId());
                if (schemes != null) {
                    if (schemes.size() == Config.BUFFER_MAX_SCHEMES) {
                        player.sendMessage("Maximum schemes amount is already reached.");
                        return;
                    }
                    if (schemes.containsKey(schemeName)) {
                        player.sendMessage("The scheme name already exists.");
                        return;
                    }
                }
                SchemeBufferTable.getInstance().setScheme(player.getObjectId(), schemeName.trim(), new ArrayList<Integer>());
                this.showGiveBuffsWindow(player);
            }
            catch (Exception e) {
                player.sendMessage("Scheme's name must contain up to 14 chars.");
            }
        }
        else if (currentCommand.startsWith("deletescheme")) {
            try {
                final String schemeName = st.nextToken();
                final Map<String, ArrayList<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(player.getObjectId());
                if (schemes != null && schemes.containsKey(schemeName)) {
                    schemes.remove(schemeName);
                }
            }
            catch (Exception e) {
                player.sendMessage("This scheme name is invalid.");
            }
            this.showGiveBuffsWindow(player);
        }
    }
    
    @Override
    public String getHtmlPath(final int npcId, final int val) {
        String filename = "";
        if (val == 0) {
            filename = Integer.toString(npcId);
        }
        else {
            filename = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, npcId, val);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, filename);
    }
    
    private void showGiveBuffsWindow(final Player player) {
        final StringBuilder sb = new StringBuilder(200);
        final Map<String, ArrayList<Integer>> schemes = SchemeBufferTable.getInstance().getPlayerSchemes(player.getObjectId());
        if (schemes == null || schemes.isEmpty()) {
            sb.append("<font color=\"LEVEL\">You haven't defined any scheme.</font>");
        }
        else {
            for (final Map.Entry<String, ArrayList<Integer>> scheme : schemes.entrySet()) {
                final int cost = getFee(scheme.getValue());
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, (String)scheme.getKey(), scheme.getValue().size(), (cost > 0) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, NumberFormat.getInstance(Locale.ENGLISH).format(cost)) : ""));
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, (String)scheme.getKey(), cost));
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, (String)scheme.getKey(), cost));
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)scheme.getKey()));
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (String)scheme.getKey()));
            }
        }
        final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
        html.setFile(player, this.getHtmlPath(this.getId(), 1));
        html.replace("%schemes%", sb.toString());
        html.replace("%max_schemes%", Config.BUFFER_MAX_SCHEMES);
        html.replace("%objectId%", this.getObjectId());
        player.sendPacket(html);
    }
    
    private void showEditSchemeWindow(final Player player, final String groupType, final String schemeName, final int page) {
        final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
        final List<Integer> schemeSkills = SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
        html.setFile(player, this.getHtmlPath(this.getId(), 2));
        html.replace("%schemename%", schemeName);
        html.replace("%count%", invokedynamic(makeConcatWithConstants:(JIJB)Ljava/lang/String;, getCountOf(schemeSkills, false), player.getStats().getMaxBuffCount(), getCountOf(schemeSkills, true), Config.DANCES_MAX_AMOUNT));
        html.replace("%typesframe%", getTypesFrame(groupType, schemeName));
        html.replace("%skilllistframe%", this.getGroupSkillList(player, groupType, schemeName, page));
        html.replace("%objectId%", this.getObjectId());
        player.sendPacket(html);
    }
    
    private String getGroupSkillList(final Player player, final String groupType, final String schemeName, int page) {
        List<Integer> skills = SchemeBufferTable.getInstance().getSkillsIdsByType(groupType);
        if (skills.isEmpty()) {
            return "That group doesn't contain any skills.";
        }
        final int max = countPagesNumber(skills.size(), 6);
        if (page > max) {
            page = max;
        }
        skills = skills.subList((page - 1) * 6, Math.min(page * 6, skills.size()));
        final List<Integer> schemeSkills = SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
        final StringBuilder sb = new StringBuilder(skills.size() * 150);
        int row = 0;
        for (final int skillId : skills) {
            final Skill skill = SkillEngine.getInstance().getSkill(skillId, 1);
            sb.append((row++ % 2 == 0) ? "<table width=\"280\" bgcolor=\"000000\"><tr>" : "<table width=\"280\"><tr>").append("<td height=40 width=40><img src=\"").append(skill.getIcon()).append("\" width=32 height=32></td><td width=190>").append(skill.getName()).append("<br1><font color=\"B09878\">").append(SchemeBufferTable.getInstance().getAvailableBuff(skillId).getDescription()).append("</font></td><td><button value=\" \" action=\"bypass -h npc_%objectId%_").append(schemeSkills.contains(skillId) ? "skillunselect;" : "skillselect;").append(groupType).append(";").append(schemeName).append(";").append(skillId).append(";").append(page).append("\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>").append("</tr></table><img src=\"L2UI.SquareGray\" width=277 height=1>");
        }
        sb.append("<br><img src=\"L2UI.SquareGray\" width=277 height=1><table width=\"100%\" bgcolor=000000><tr>");
        if (page > 1) {
            sb.append("<td align=left width=70><a action=\"bypass -h npc_").append(this.getObjectId()).append("_editschemes;").append(groupType).append(";").append(schemeName).append(";").append(page - 1).append("\">Previous</a></td>");
        }
        else {
            sb.append("<td align=left width=70>Previous</td>");
        }
        sb.append("<td align=center width=100>Page ").append(page).append("</td>");
        if (page < max) {
            sb.append("<td align=right width=70><a action=\"bypass -h npc_").append(this.getObjectId()).append("_editschemes;").append(groupType).append(";").append(schemeName).append(";").append(page + 1).append("\">Next</a></td>");
        }
        else {
            sb.append("<td align=right width=70>Next</td>");
        }
        sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=277 height=1>");
        return sb.toString();
    }
}
