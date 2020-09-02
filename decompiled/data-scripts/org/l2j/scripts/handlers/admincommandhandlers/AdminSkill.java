// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.Map;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.serverpackets.PledgeSkillList;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import java.util.Iterator;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import java.util.StringTokenizer;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.network.serverpackets.AcquireSkillList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminSkill implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] ADMIN_COMMANDS;
    private static Skill[] adminSkills;
    
    public boolean useAdminCommand(String command, final Player activeChar) {
        if (command.equals("admin_show_skills")) {
            this.showMainPage(activeChar);
        }
        else if (command.startsWith("admin_remove_skills")) {
            try {
                final String val = command.substring(20);
                this.removeSkillsPage(activeChar, Integer.parseInt(val));
            }
            catch (StringIndexOutOfBoundsException ex) {}
        }
        else if (command.startsWith("admin_skill_list")) {
            AdminHtml.showAdminHtml(activeChar, "skills.htm");
        }
        else if (command.startsWith("admin_skill_index")) {
            try {
                final String val = command.substring(18);
                AdminHtml.showAdminHtml(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, val));
            }
            catch (StringIndexOutOfBoundsException ex2) {}
        }
        else if (command.startsWith("admin_add_skill")) {
            try {
                final String val = command.substring(15);
                this.adminAddSkill(activeChar, val);
            }
            catch (Exception e2) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //add_skill <skill_id> <level>");
            }
        }
        else if (command.startsWith("admin_remove_skill")) {
            try {
                final String id = command.substring(19);
                final int idval = Integer.parseInt(id);
                this.adminRemoveSkill(activeChar, idval);
            }
            catch (Exception e2) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //remove_skill <skill_id>");
            }
        }
        else if (command.equals("admin_get_skills")) {
            this.adminGetSkills(activeChar);
        }
        else if (command.equals("admin_reset_skills")) {
            this.adminResetSkills(activeChar);
        }
        else if (command.equals("admin_give_all_skills")) {
            this.adminGiveAllSkills(activeChar, false);
        }
        else if (command.equals("admin_give_all_skills_fs")) {
            this.adminGiveAllSkills(activeChar, true);
        }
        else if (command.equals("admin_give_clan_skills")) {
            this.adminGiveClanSkills(activeChar, false);
        }
        else if (command.equals("admin_give_all_clan_skills")) {
            this.adminGiveClanSkills(activeChar, true);
        }
        else if (command.equals("admin_remove_all_skills")) {
            final WorldObject target = activeChar.getTarget();
            if (!GameUtils.isPlayer(target)) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                return false;
            }
            final Player player = target.getActingPlayer();
            for (final Skill skill : player.getAllSkills()) {
                player.removeSkill(skill);
            }
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            player.sendMessage("Admin removed all skills from you.");
            player.sendSkillList();
            player.broadcastUserInfo();
            player.sendPacket(new ServerPacket[] { (ServerPacket)new AcquireSkillList(player) });
        }
        else if (command.startsWith("admin_add_clan_skill")) {
            try {
                final String[] val2 = command.split(" ");
                this.adminAddClanSkill(activeChar, Integer.parseInt(val2[1]), Integer.parseInt(val2[2]));
            }
            catch (Exception e2) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //add_clan_skill <skill_id> <level>");
            }
        }
        else if (command.startsWith("admin_setskill")) {
            final String[] split = command.split(" ");
            final int id2 = Integer.parseInt(split[1]);
            final int lvl = Integer.parseInt(split[2]);
            final Skill skill = SkillEngine.getInstance().getSkill(id2, lvl);
            if (skill != null) {
                activeChar.addSkill(skill);
                activeChar.sendSkillList();
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, skill.getName(), id2, lvl));
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new AcquireSkillList(activeChar) });
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, id2, lvl));
            }
        }
        else if (command.startsWith("admin_cast")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            command = st.nextToken();
            if (!st.hasMoreTokens()) {
                BuilderUtil.sendSysMessage(activeChar, "Skill Id and level are not specified.");
                BuilderUtil.sendSysMessage(activeChar, "Usage: //cast <skillId> <skillLevel>");
                return false;
            }
            try {
                final int skillId = Integer.parseInt(st.nextToken());
                final int skillLevel = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : SkillEngine.getInstance().getMaxLevel(skillId);
                final Skill skill = SkillEngine.getInstance().getSkill(skillId, skillLevel);
                if (skill == null) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, skillId, skillLevel));
                    return false;
                }
                if (command.equalsIgnoreCase("admin_castnow")) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, skill.getName(), skillId, skillLevel));
                    final WorldObject target2 = skill.getTarget((Creature)activeChar, true, false, true);
                    if (target2 != null) {
                        final Skill skill2;
                        skill.forEachTargetAffected((Creature)activeChar, target2, o -> {
                            if (GameUtils.isCreature(o)) {
                                skill2.activateSkill((Creature)activeChar, new WorldObject[] { o });
                            }
                            return;
                        });
                    }
                }
                else {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, skill.getName(), skillId, skillLevel));
                    activeChar.doCast(skill);
                }
                return true;
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
                BuilderUtil.sendSysMessage(activeChar, "Usage: //cast <skillId> <skillLevel>");
                return false;
            }
        }
        return true;
    }
    
    private void adminGiveAllSkills(final Player activeChar, final boolean includedByFs) {
        final WorldObject target = activeChar.getTarget();
        if (!GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        final Player player = target.getActingPlayer();
        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, player.giveAvailableSkills(includedByFs, true), player.getName()));
        player.sendSkillList();
        player.sendPacket(new ServerPacket[] { (ServerPacket)new AcquireSkillList(player) });
    }
    
    private void adminGiveClanSkills(final Player activeChar, final boolean includeSquad) {
        final WorldObject target = activeChar.getTarget();
        if (target == null || !GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        final Player player = target.getActingPlayer();
        final Clan clan = player.getClan();
        if (clan == null) {
            activeChar.sendPacket(SystemMessageId.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
            return;
        }
        if (!player.isClanLeader()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER);
            sm.addString(player.getName());
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)sm });
        }
        final Map<Integer, SkillLearn> skills = (Map<Integer, SkillLearn>)SkillTreesData.getInstance().getMaxPledgeSkills(clan, includeSquad);
        for (final SkillLearn s : skills.values()) {
            clan.addNewSkill(SkillEngine.getInstance().getSkill(s.getSkillId(), s.getSkillLevel()));
        }
        clan.broadcastToOnlineMembers((ServerPacket)new PledgeSkillList(clan));
        for (final Player member : clan.getOnlineMembers(0)) {
            member.sendSkillList();
        }
        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, skills.size(), player.getName(), clan.getName()));
        player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, skills.size()));
    }
    
    private void removeSkillsPage(final Player activeChar, int page) {
        final WorldObject target = activeChar.getTarget();
        if (target == null || !GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }
        final Player player = target.getActingPlayer();
        final Skill[] skills = player.getAllSkills().toArray(new Skill[player.getAllSkills().size()]);
        final int maxSkillsPerPage = 10;
        int maxPages = skills.length / 10;
        if (skills.length > 10 * maxPages) {
            ++maxPages;
        }
        if (page > maxPages) {
            page = maxPages;
        }
        final int skillsStart = 10 * page;
        int skillsEnd = skills.length;
        if (skillsEnd - skillsStart > 10) {
            skillsEnd = skillsStart + 10;
        }
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        final StringBuilder replyMSG = new StringBuilder(500 + maxPages * 50 + (skillsEnd - skillsStart + 1) * 50);
        replyMSG.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, player.getName(), player.getLevel(), ClassListData.getInstance().getClass(player.getClassId()).getClientCode()));
        for (int x = 0; x < maxPages; ++x) {
            final int pagenr = x + 1;
            replyMSG.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, x, pagenr));
        }
        replyMSG.append("</tr></table></center><br><table width=270><tr><td width=80>Name:</td><td width=60>Level:</td><td width=40>Id:</td></tr>");
        for (int i = skillsStart; i < skillsEnd; ++i) {
            replyMSG.append(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;II)Ljava/lang/String;, skills[i].getId(), skills[i].getName(), skills[i].getLevel(), skills[i].getId()));
        }
        replyMSG.append("</table><br><center><table>Remove skill by ID :<tr><td>Id: </td><td><edit var=\"id_to_remove\" width=110></td></tr></table></center><center><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=110 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center><br><center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></body></html>");
        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void showMainPage(final Player activeChar) {
        final WorldObject target = activeChar.getTarget();
        if (target == null || !GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        final Player player = target.getActingPlayer();
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        adminReply.setFile(activeChar, "data/html/admin/charskills.htm");
        adminReply.replace("%name%", player.getName());
        adminReply.replace("%level%", String.valueOf(player.getLevel()));
        adminReply.replace("%class%", ClassListData.getInstance().getClass(player.getClassId()).getClientCode());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void adminGetSkills(final Player activeChar) {
        final WorldObject target = activeChar.getTarget();
        if (target == null || !GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        final Player player = target.getActingPlayer();
        if (player.getName().equals(activeChar.getName())) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
        }
        else {
            final Skill[] skills = player.getAllSkills().toArray(new Skill[player.getAllSkills().size()]);
            AdminSkill.adminSkills = activeChar.getAllSkills().toArray(new Skill[activeChar.getAllSkills().size()]);
            for (final Skill skill : AdminSkill.adminSkills) {
                activeChar.removeSkill(skill);
            }
            for (final Skill skill : skills) {
                activeChar.addSkill(skill, true);
            }
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            activeChar.sendSkillList();
        }
        this.showMainPage(activeChar);
    }
    
    private void adminResetSkills(final Player activeChar) {
        final WorldObject target = activeChar.getTarget();
        if (target == null || !GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        final Player player = target.getActingPlayer();
        if (AdminSkill.adminSkills == null) {
            BuilderUtil.sendSysMessage(activeChar, "You must get the skills of someone in order to do this.");
        }
        else {
            final Skill[] array;
            final Skill[] skills = array = player.getAllSkills().toArray(new Skill[player.getAllSkills().size()]);
            for (final Skill skill : array) {
                player.removeSkill(skill);
            }
            for (final Skill skill2 : activeChar.getAllSkills()) {
                player.addSkill(skill2, true);
            }
            for (final Skill skill : skills) {
                activeChar.removeSkill(skill);
            }
            for (final Skill skill : AdminSkill.adminSkills) {
                activeChar.addSkill(skill, true);
            }
            player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
            BuilderUtil.sendSysMessage(activeChar, "You now have all your skills back.");
            AdminSkill.adminSkills = null;
            activeChar.sendSkillList();
            player.sendSkillList();
        }
        this.showMainPage(activeChar);
    }
    
    private void adminAddSkill(final Player activeChar, final String val) {
        final WorldObject target = activeChar.getTarget();
        if (target == null || !GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            this.showMainPage(activeChar);
            return;
        }
        final Player player = target.getActingPlayer();
        final StringTokenizer st = new StringTokenizer(val);
        if (st.countTokens() != 1 && st.countTokens() != 2) {
            this.showMainPage(activeChar);
        }
        else {
            Skill skill = null;
            try {
                final String id = st.nextToken();
                final String level = (st.countTokens() == 1) ? st.nextToken() : null;
                final int idval = Integer.parseInt(id);
                final int levelval = (level == null) ? 1 : Integer.parseInt(level);
                skill = SkillEngine.getInstance().getSkill(idval, levelval);
            }
            catch (Exception e) {
                AdminSkill.LOGGER.warn("", (Throwable)e);
            }
            if (skill != null) {
                final String name = skill.getName();
                player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
                player.addSkill(skill, true);
                player.sendSkillList();
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, name, player.getName()));
                activeChar.sendSkillList();
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, "Error: there is no such skill.");
            }
            this.showMainPage(activeChar);
        }
    }
    
    private void adminRemoveSkill(final Player activeChar, final int idval) {
        final WorldObject target = activeChar.getTarget();
        if (target == null || !GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        final Player player = target.getActingPlayer();
        final Skill skill = SkillEngine.getInstance().getSkill(idval, player.getSkillLevel(idval));
        if (skill != null) {
            final String skillname = skill.getName();
            player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skillname));
            player.removeSkill(skill);
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, skillname, player.getName()));
            activeChar.sendSkillList();
        }
        else {
            BuilderUtil.sendSysMessage(activeChar, "Error: there is no such skill.");
        }
        this.removeSkillsPage(activeChar, 0);
    }
    
    private void adminAddClanSkill(final Player activeChar, final int id, final int level) {
        final WorldObject target = activeChar.getTarget();
        if (target == null || !GameUtils.isPlayer(target)) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            this.showMainPage(activeChar);
            return;
        }
        final Player player = target.getActingPlayer();
        if (!player.isClanLeader()) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER);
            sm.addString(player.getName());
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            this.showMainPage(activeChar);
            return;
        }
        if (id < 370 || id > 391 || level < 1 || level > 3) {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //add_clan_skill <skill_id> <level>");
            this.showMainPage(activeChar);
            return;
        }
        final Skill skill = SkillEngine.getInstance().getSkill(id, level);
        if (skill == null) {
            BuilderUtil.sendSysMessage(activeChar, "Error: there is no such skill.");
            return;
        }
        final String skillname = skill.getName();
        final SystemMessage sm2 = SystemMessage.getSystemMessage(SystemMessageId.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED);
        sm2.addSkillName(skill);
        player.sendPacket(new ServerPacket[] { (ServerPacket)sm2 });
        final Clan clan = player.getClan();
        clan.broadcastToOnlineMembers((ServerPacket)sm2);
        clan.addNewSkill(skill);
        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, skillname, clan.getName()));
        clan.broadcastToOnlineMembers((ServerPacket)new PledgeSkillList(clan));
        for (final Player member : clan.getOnlineMembers(0)) {
            member.sendSkillList();
        }
        this.showMainPage(activeChar);
    }
    
    public String[] getAdminCommandList() {
        return AdminSkill.ADMIN_COMMANDS;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminSkill.class);
        ADMIN_COMMANDS = new String[] { "admin_show_skills", "admin_remove_skills", "admin_skill_list", "admin_skill_index", "admin_add_skill", "admin_remove_skill", "admin_get_skills", "admin_reset_skills", "admin_give_all_skills", "admin_give_all_skills_fs", "admin_give_clan_skills", "admin_give_all_clan_skills", "admin_remove_all_skills", "admin_add_clan_skill", "admin_setskill", "admin_cast", "admin_castnow" };
    }
}
