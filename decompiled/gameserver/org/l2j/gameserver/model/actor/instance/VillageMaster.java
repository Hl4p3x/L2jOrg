// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.IntFunction;
import org.slf4j.LoggerFactory;
import java.util.function.Predicate;
import java.util.Collection;
import java.util.EnumSet;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.base.ClassInfo;
import org.l2j.gameserver.enums.Race;
import java.util.Objects;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.network.serverpackets.MagicSkillLaunched;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;
import org.l2j.gameserver.model.SkillLearn;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ExAcquirableSkillListByClass;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.data.database.data.SubPledgeData;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.base.SubClass;
import java.util.Iterator;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import java.util.EnumMap;
import org.l2j.gameserver.model.base.ClassId;
import java.util.Set;
import org.slf4j.Logger;

public class VillageMaster extends Folk
{
    private static final Logger LOGGER;
    private static final Set<ClassId> mainSubclassSet;
    private static final Set<ClassId> neverSubclassed;
    private static final Set<ClassId> subclasseSet1;
    private static final Set<ClassId> subclasseSet2;
    private static final Set<ClassId> subclasseSet3;
    private static final Set<ClassId> subclasseSet4;
    private static final Set<ClassId> subclasseSet5;
    private static final EnumMap<ClassId, Set<ClassId>> subclassSetMap;
    
    public VillageMaster(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2VillageMasterInstance);
    }
    
    private static Iterator<SubClass> iterSubClasses(final Player player) {
        return player.getSubClasses().values().iterator();
    }
    
    private static void dissolveClan(final Player player, final int clanId) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        final Clan clan = player.getClan();
        if (clan.getAllyId() != 0) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE);
            return;
        }
        if (clan.isAtWar()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR);
            return;
        }
        if (clan.getCastleId() != 0 || clan.getHideoutId() != 0) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_OWNING_A_CLAN_HALL_OR_CASTLE);
            return;
        }
        for (final Castle castle : CastleManager.getInstance().getCastles()) {
            if (SiegeManager.getInstance().checkIsRegistered(clan, castle.getId())) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_DISSOLVE_A_CLAN_DURING_A_SIEGE_OR_WHILE_PROTECTING_A_CASTLE);
                return;
            }
        }
        if (player.isInsideZone(ZoneType.SIEGE)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_DISSOLVE_A_CLAN_DURING_A_SIEGE_OR_WHILE_PROTECTING_A_CASTLE);
            return;
        }
        if (clan.getDissolvingExpiryTime() > System.currentTimeMillis()) {
            player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN);
            return;
        }
        clan.setDissolvingExpiryTime(System.currentTimeMillis() + Config.ALT_CLAN_DISSOLVE_DAYS * 86400000);
        clan.updateClanInDB();
        player.calculateDeathExpPenalty(null);
        ClanTable.getInstance().scheduleRemoveClan(clan);
    }
    
    private static void recoverClan(final Player player, final int clanId) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        final Clan clan = player.getClan();
        clan.setDissolvingExpiryTime(0L);
        clan.updateClanInDB();
    }
    
    private static void createSubPledge(final Player player, final String clanName, final String leaderName, final int pledgeType, final int minClanLvl) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        final Clan clan = player.getClan();
        if (clan.getLevel() < minClanLvl) {
            if (pledgeType == -1) {
                player.sendPacket(SystemMessageId.TO_ESTABLISH_A_CLAN_ACADEMY_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER);
            }
            else {
                player.sendPacket(SystemMessageId.THE_CONDITIONS_NECESSARY_TO_CREATE_A_MILITARY_UNIT_HAVE_NOT_BEEN_MET);
            }
            return;
        }
        if (!Util.isAlphaNumeric(clanName) || !isValidName(clanName) || 2 > clanName.length()) {
            player.sendPacket(SystemMessageId.CLAN_NAME_IS_INVALID);
            return;
        }
        if (clanName.length() > 16) {
            player.sendPacket(SystemMessageId.CLAN_NAME_S_LENGTH_IS_INCORRECT);
            return;
        }
        for (final Clan tempClan : ClanTable.getInstance().getClans()) {
            if (tempClan.getSubPledge(clanName) != null) {
                if (pledgeType == -1) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_EXISTS);
                    sm.addString(clanName);
                    player.sendPacket(sm);
                }
                else {
                    player.sendPacket(SystemMessageId.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME);
                }
                return;
            }
        }
        if (pledgeType != -1 && (clan.getClanMember(leaderName) == null || clan.getClanMember(leaderName).getPledgeType() != 0)) {
            if (pledgeType >= 1001) {
                player.sendPacket(SystemMessageId.THE_CAPTAIN_OF_THE_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED);
            }
            else if (pledgeType >= 100) {
                player.sendPacket(SystemMessageId.THE_ROYAL_GUARD_CAPTAIN_CANNOT_BE_APPOINTED);
            }
            return;
        }
        final int leaderId = (pledgeType != -1) ? clan.getClanMember(leaderName).getObjectId() : 0;
        if (clan.createSubPledge(player, pledgeType, leaderId, clanName) == null) {
            return;
        }
        SystemMessage sm2;
        if (pledgeType == -1) {
            sm2 = SystemMessage.getSystemMessage(SystemMessageId.CONGRATULATIONS_THE_S1_S_CLAN_ACADEMY_HAS_BEEN_CREATED);
            sm2.addString(player.getClan().getName());
        }
        else if (pledgeType >= 1001) {
            sm2 = SystemMessage.getSystemMessage(SystemMessageId.THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED);
            sm2.addString(player.getClan().getName());
        }
        else if (pledgeType >= 100) {
            sm2 = SystemMessage.getSystemMessage(SystemMessageId.THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED);
            sm2.addString(player.getClan().getName());
        }
        else {
            sm2 = SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_BEEN_CREATED);
        }
        player.sendPacket(sm2);
        if (pledgeType != -1) {
            final ClanMember leaderSubPledge = clan.getClanMember(leaderName);
            final Player leaderPlayer = leaderSubPledge.getPlayerInstance();
            if (leaderPlayer != null) {
                leaderPlayer.setPledgeClass(ClanMember.calculatePledgeClass(leaderPlayer));
                leaderPlayer.sendPacket(new UserInfo(leaderPlayer));
            }
        }
    }
    
    private static void renameSubPledge(final Player player, final int pledgeType, final String pledgeName) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        final Clan clan = player.getClan();
        final SubPledgeData subPledge = player.getClan().getSubPledge(pledgeType);
        if (subPledge == null) {
            player.sendMessage("Pledge don't exists.");
            return;
        }
        if (!Util.isAlphaNumeric(pledgeName) || !isValidName(pledgeName) || 2 > pledgeName.length()) {
            player.sendPacket(SystemMessageId.CLAN_NAME_IS_INVALID);
            return;
        }
        if (pledgeName.length() > 16) {
            player.sendPacket(SystemMessageId.CLAN_NAME_S_LENGTH_IS_INCORRECT);
            return;
        }
        subPledge.setName(pledgeName);
        clan.updateSubPledgeInDB(subPledge.getId());
        clan.broadcastClanStatus();
        player.sendMessage("Pledge name changed.");
    }
    
    private static void assignSubPledgeLeader(final Player player, final String clanName, final String leaderName) {
        if (!player.isClanLeader()) {
            player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        if (leaderName.length() > 16) {
            player.sendPacket(SystemMessageId.YOUR_TITLE_CANNOT_EXCEED_16_CHARACTERS_IN_LENGTH_PLEASE_TRY_AGAIN);
            return;
        }
        if (player.getName().equals(leaderName)) {
            player.sendPacket(SystemMessageId.THE_ROYAL_GUARD_CAPTAIN_CANNOT_BE_APPOINTED);
            return;
        }
        final Clan clan = player.getClan();
        final SubPledgeData subPledge = player.getClan().getSubPledge(clanName);
        if (null == subPledge || subPledge.getId() == -1) {
            player.sendPacket(SystemMessageId.CLAN_NAME_IS_INVALID);
            return;
        }
        if (clan.getClanMember(leaderName) == null || clan.getClanMember(leaderName).getPledgeType() != 0) {
            if (subPledge.getId() >= 1001) {
                player.sendPacket(SystemMessageId.THE_CAPTAIN_OF_THE_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED);
            }
            else if (subPledge.getId() >= 100) {
                player.sendPacket(SystemMessageId.THE_ROYAL_GUARD_CAPTAIN_CANNOT_BE_APPOINTED);
            }
            return;
        }
        subPledge.setLeaderId(clan.getClanMember(leaderName).getObjectId());
        clan.updateSubPledgeInDB(subPledge.getId());
        final ClanMember leaderSubPledge = clan.getClanMember(leaderName);
        final Player leaderPlayer = leaderSubPledge.getPlayerInstance();
        if (leaderPlayer != null) {
            leaderPlayer.setPledgeClass(ClanMember.calculatePledgeClass(leaderPlayer));
            leaderPlayer.sendPacket(new UserInfo(leaderPlayer));
        }
        clan.broadcastClanStatus();
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BEEN_SELECTED_AS_THE_CAPTAIN_OF_S2);
        sm.addString(leaderName);
        sm.addString(clanName);
        clan.broadcastToOnlineMembers(sm);
    }
    
    public static void showPledgeSkillList(final Player player) {
        if (!player.isClanLeader()) {
            final NpcHtmlMessage html = new NpcHtmlMessage();
            html.setFile(player, "data/html/villagemaster/NotClanLeader.htm");
            player.sendPacket(html);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailablePledgeSkills(player.getClan());
        if (skills.isEmpty()) {
            if (player.getClan().getLevel() < 8) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN_COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1);
                if (player.getClan().getLevel() < 5) {
                    sm.addInt(5);
                }
                else {
                    sm.addInt(player.getClan().getLevel() + 1);
                }
                player.sendPacket(sm);
            }
            else {
                final NpcHtmlMessage html2 = new NpcHtmlMessage();
                html2.setFile(player, "data/html/villagemaster/NoMoreSkills.htm");
                player.sendPacket(html2);
            }
        }
        else {
            player.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.PLEDGE));
        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    private static boolean isValidName(final String name) {
        Pattern pattern;
        try {
            pattern = Pattern.compile(Config.CLAN_NAME_TEMPLATE);
        }
        catch (PatternSyntaxException e) {
            VillageMaster.LOGGER.warn("ERROR: Wrong pattern for clan name!");
            pattern = Pattern.compile(".*");
        }
        return pattern.matcher(name).matches();
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return GameUtils.isMonster(attacker) || super.isAutoAttackable(attacker);
    }
    
    @Override
    public String getHtmlPath(final int npcId, final int val) {
        String pom = "";
        if (val == 0) {
            pom = Integer.toString(npcId);
        }
        else {
            pom = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, npcId, val);
        }
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pom);
    }
    
    @Override
    public void onBypassFeedback(final Player player, final String command) {
        final String[] commandStr = command.split(" ");
        final String actualCommand = commandStr[0];
        String cmdParams = "";
        String cmdParams2 = "";
        if (commandStr.length >= 2) {
            cmdParams = commandStr[1];
        }
        if (commandStr.length >= 3) {
            cmdParams2 = commandStr[2];
        }
        if (actualCommand.equalsIgnoreCase("create_clan")) {
            if (cmdParams.isEmpty()) {
                return;
            }
            if (!cmdParams2.isEmpty() || !isValidName(cmdParams)) {
                player.sendPacket(SystemMessageId.CLAN_NAME_IS_INVALID);
                return;
            }
            ClanTable.getInstance().createClan(player, cmdParams);
        }
        else if (actualCommand.equalsIgnoreCase("create_academy")) {
            if (cmdParams.isEmpty()) {
                return;
            }
            createSubPledge(player, cmdParams, null, -1, 5);
        }
        else if (actualCommand.equalsIgnoreCase("rename_pledge")) {
            if (cmdParams.isEmpty() || cmdParams2.isEmpty()) {
                return;
            }
            renameSubPledge(player, Integer.parseInt(cmdParams), cmdParams2);
        }
        else if (actualCommand.equalsIgnoreCase("create_royal")) {
            if (cmdParams.isEmpty()) {
                return;
            }
            createSubPledge(player, cmdParams, cmdParams2, 100, 6);
        }
        else if (actualCommand.equalsIgnoreCase("create_knight")) {
            if (cmdParams.isEmpty()) {
                return;
            }
            createSubPledge(player, cmdParams, cmdParams2, 1001, 7);
        }
        else if (actualCommand.equalsIgnoreCase("assign_subpl_leader")) {
            if (cmdParams.isEmpty()) {
                return;
            }
            assignSubPledgeLeader(player, cmdParams, cmdParams2);
        }
        else if (actualCommand.equalsIgnoreCase("create_ally")) {
            if (cmdParams.isEmpty()) {
                return;
            }
            if (player.getClan() == null) {
                player.sendPacket(SystemMessageId.ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES);
            }
            else {
                player.getClan().createAlly(player, cmdParams);
            }
        }
        else if (actualCommand.equalsIgnoreCase("dissolve_ally")) {
            player.getClan().dissolveAlly(player);
        }
        else if (actualCommand.equalsIgnoreCase("dissolve_clan")) {
            dissolveClan(player, player.getClanId());
        }
        else if (actualCommand.equalsIgnoreCase("change_clan_leader")) {
            if (cmdParams.isEmpty()) {
                return;
            }
            if (!player.isClanLeader()) {
                player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            if (player.getName().equalsIgnoreCase(cmdParams)) {
                return;
            }
            final Clan clan = player.getClan();
            final ClanMember member = clan.getClanMember(cmdParams);
            if (member == null) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DOES_NOT_EXIST);
                sm.addString(cmdParams);
                player.sendPacket(sm);
                return;
            }
            if (!member.isOnline()) {
                player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE);
                return;
            }
            if (member.getPlayerInstance().isAcademyMember()) {
                player.sendPacket(SystemMessageId.THAT_PRIVILEGE_CANNOT_BE_GRANTED_TO_A_CLAN_ACADEMY_MEMBER);
                return;
            }
            if (Config.ALT_CLAN_LEADER_INSTANT_ACTIVATION) {
                clan.setNewLeader(member);
            }
            else {
                final NpcHtmlMessage msg = new NpcHtmlMessage(this.getObjectId());
                if (clan.getNewLeaderId() == 0) {
                    clan.setNewLeaderId(member.getObjectId(), true);
                    msg.setFile(player, "data/scripts/village_master/Clan/9000-07-success.htm");
                }
                else {
                    msg.setFile(player, "data/scripts/village_master/Clan/9000-07-in-progress.htm");
                }
                player.sendPacket(msg);
            }
        }
        else if (actualCommand.equalsIgnoreCase("cancel_clan_leader_change")) {
            if (!player.isClanLeader()) {
                player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            final Clan clan = player.getClan();
            final NpcHtmlMessage msg2 = new NpcHtmlMessage(this.getObjectId());
            if (clan.getNewLeaderId() != 0) {
                clan.setNewLeaderId(0, true);
                msg2.setFile(player, "data/scripts/village_master/Clan/9000-07-canceled.htm");
            }
            else {
                msg2.setHtml("<html><body>You don't have clan leader delegation applications submitted yet!</body></html>");
            }
            player.sendPacket(msg2);
        }
        else if (actualCommand.equalsIgnoreCase("recover_clan")) {
            recoverClan(player, player.getClanId());
        }
        else if (actualCommand.equalsIgnoreCase("increase_clan_level")) {
            if (player.getClan().levelUpClan(player)) {
                player.broadcastPacket(new MagicSkillUse(player, 5103, 1, 0, 0));
                player.broadcastPacket(new MagicSkillLaunched(player, 5103, 1));
            }
        }
        else if (actualCommand.equalsIgnoreCase("learn_clan_skills")) {
            showPledgeSkillList(player);
        }
        else if (command.startsWith("Subclass")) {
            if (player.isCastingNow() || player.isAllSkillsDisabled()) {
                player.sendPacket(SystemMessageId.SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE);
                return;
            }
            final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
            if (player.getTransformation() != null) {
                html.setFile(player, "data/html/villagemaster/SubClass_NoTransformed.htm");
                player.sendPacket(html);
                return;
            }
            if (player.hasSummon()) {
                html.setFile(player, "data/html/villagemaster/SubClass_NoSummon.htm");
                player.sendPacket(html);
                return;
            }
            if (!player.isInventoryUnder90(true)) {
                player.sendPacket(SystemMessageId.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT);
                return;
            }
            if (player.getWeightPenalty() >= 2) {
                player.sendPacket(SystemMessageId.A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT);
                return;
            }
            int cmdChoice = 0;
            int paramOne = 0;
            int paramTwo = 0;
            try {
                cmdChoice = Integer.parseInt(command.substring(9, 10).trim());
                int endIndex = command.indexOf(32, 11);
                if (endIndex == -1) {
                    endIndex = command.length();
                }
                if (command.length() > 11) {
                    paramOne = Integer.parseInt(command.substring(11, endIndex).trim());
                    if (command.length() > endIndex) {
                        paramTwo = Integer.parseInt(command.substring(endIndex).trim());
                    }
                }
            }
            catch (Exception NumberFormatException) {
                VillageMaster.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, VillageMaster.class.getName(), command));
            }
            Set<ClassId> subsAvailable = null;
            switch (cmdChoice) {
                case 0: {
                    html.setFile(player, this.getSubClassMenu(player.getRace()));
                    break;
                }
                case 1: {
                    if (player.getTotalSubClasses() >= Config.MAX_SUBCLASS) {
                        html.setFile(player, this.getSubClassFail());
                        break;
                    }
                    subsAvailable = this.getAvailableSubClasses(player);
                    if (subsAvailable != null && !subsAvailable.isEmpty()) {
                        html.setFile(player, "data/html/villagemaster/SubClass_Add.htm");
                        final StringBuilder content1 = new StringBuilder(200);
                        for (final ClassId subClass : subsAvailable) {
                            final ClassInfo playerClass = ClassListData.getInstance().getClass(subClass.getId());
                            if (Objects.nonNull(playerClass)) {
                                content1.append("<a action=\"bypass -h npc_%objectId%_Subclass 4 ").append(subClass.getId()).append("\" msg=\"1268;").append(playerClass.getClassName()).append("\">").append(playerClass.getClientCode()).append("</a><br>");
                            }
                        }
                        html.replace("%list%", content1.toString());
                        break;
                    }
                    if (player.getRace() == Race.ELF || player.getRace() == Race.DARK_ELF) {
                        html.setFile(player, "data/html/villagemaster/SubClass_Fail_Elves.htm");
                        player.sendPacket(html);
                    }
                    else {
                        player.sendMessage("There are no sub classes available at this time.");
                    }
                    return;
                }
                case 2: {
                    if (player.getSubClasses().isEmpty()) {
                        html.setFile(player, "data/html/villagemaster/SubClass_ChangeNo.htm");
                        break;
                    }
                    final StringBuilder content2 = new StringBuilder(200);
                    if (this.checkVillageMaster(player.getBaseClass())) {
                        content2.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ClassListData.getInstance().getClass(player.getBaseClass()).getClientCode()));
                    }
                    final Iterator<SubClass> subList = iterSubClasses(player);
                    while (subList.hasNext()) {
                        final SubClass subClass2 = subList.next();
                        if (this.checkVillageMaster(subClass2.getClassDefinition())) {
                            content2.append(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, subClass2.getClassIndex(), ClassListData.getInstance().getClass(subClass2.getClassId()).getClientCode()));
                        }
                    }
                    if (content2.length() > 0) {
                        html.setFile(player, "data/html/villagemaster/SubClass_Change.htm");
                        html.replace("%list%", content2.toString());
                    }
                    else {
                        html.setFile(player, "data/html/villagemaster/SubClass_ChangeNotFound.htm");
                    }
                    break;
                }
                case 3: {
                    if (player.getSubClasses() == null || player.getSubClasses().isEmpty()) {
                        html.setFile(player, "data/html/villagemaster/SubClass_ModifyEmpty.htm");
                        break;
                    }
                    if (player.getTotalSubClasses() > 3) {
                        html.setFile(player, "data/html/villagemaster/SubClass_ModifyCustom.htm");
                        final StringBuilder content3 = new StringBuilder(200);
                        int classIndex = 1;
                        final Iterator<SubClass> subList2 = iterSubClasses(player);
                        while (subList2.hasNext()) {
                            final SubClass subClass3 = subList2.next();
                            content3.append(invokedynamic(makeConcatWithConstants:(IILjava/lang/String;)Ljava/lang/String;, classIndex++, subClass3.getClassIndex(), ClassListData.getInstance().getClass(subClass3.getClassId()).getClientCode()));
                        }
                        html.replace("%list%", content3.toString());
                        break;
                    }
                    html.setFile(player, "data/html/villagemaster/SubClass_Modify.htm");
                    if (player.getSubClasses().containsKey(1)) {
                        html.replace("%sub1%", ClassListData.getInstance().getClass(((SubClass)player.getSubClasses().get(1)).getClassId()).getClientCode());
                    }
                    else {
                        html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 1\">%sub1%</a><br>", "");
                    }
                    if (player.getSubClasses().containsKey(2)) {
                        html.replace("%sub2%", ClassListData.getInstance().getClass(((SubClass)player.getSubClasses().get(2)).getClassId()).getClientCode());
                    }
                    else {
                        html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 2\">%sub2%</a><br>", "");
                    }
                    if (player.getSubClasses().containsKey(3)) {
                        html.replace("%sub3%", ClassListData.getInstance().getClass(((SubClass)player.getSubClasses().get(3)).getClassId()).getClientCode());
                        break;
                    }
                    html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 3\">%sub3%</a><br>", "");
                    break;
                }
                case 4: {
                    if (!player.getFloodProtectors().getSubclass().tryPerformAction("add subclass")) {
                        VillageMaster.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, VillageMaster.class.getName(), player.getName()));
                        return;
                    }
                    boolean allowAddition = true;
                    if (player.getTotalSubClasses() >= Config.MAX_SUBCLASS) {
                        allowAddition = false;
                    }
                    if (player.getLevel() < 75) {
                        allowAddition = false;
                    }
                    if (allowAddition && !player.getSubClasses().isEmpty()) {
                        final Iterator<SubClass> subList = iterSubClasses(player);
                        while (subList.hasNext()) {
                            final SubClass subClass2 = subList.next();
                            if (subClass2.getLevel() < 75) {
                                allowAddition = false;
                                break;
                            }
                        }
                    }
                    if (allowAddition && !Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS) {
                        allowAddition = this.checkQuests(player);
                    }
                    if (!allowAddition || !this.isValidNewSubClass(player, paramOne)) {
                        html.setFile(player, this.getSubClassFail());
                        break;
                    }
                    if (!player.addSubClass(paramOne, player.getTotalSubClasses() + 1, false)) {
                        return;
                    }
                    player.setActiveClass(player.getTotalSubClasses());
                    html.setFile(player, "data/html/villagemaster/SubClass_AddOk.htm");
                    player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
                    break;
                }
                case 5: {
                    if (!player.getFloodProtectors().getSubclass().tryPerformAction("change class")) {
                        VillageMaster.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, VillageMaster.class.getName(), player.getName()));
                        return;
                    }
                    if (player.getClassIndex() == paramOne) {
                        html.setFile(player, "data/html/villagemaster/SubClass_Current.htm");
                        break;
                    }
                    if (paramOne == 0) {
                        if (!this.checkVillageMaster(player.getBaseClass())) {
                            return;
                        }
                    }
                    else {
                        try {
                            if (!this.checkVillageMaster(((SubClass)player.getSubClasses().get(paramOne)).getClassDefinition())) {
                                return;
                            }
                        }
                        catch (NullPointerException e) {
                            return;
                        }
                    }
                    player.setActiveClass(paramOne);
                    player.sendMessage("You have successfully switched to your subclass.");
                    return;
                }
                case 6: {
                    if (paramOne < 1 || paramOne > Config.MAX_SUBCLASS) {
                        return;
                    }
                    subsAvailable = this.getAvailableSubClasses(player);
                    if (subsAvailable == null || subsAvailable.isEmpty()) {
                        player.sendMessage("There are no sub classes available at this time.");
                        return;
                    }
                    final StringBuilder content4 = new StringBuilder(200);
                    for (final ClassId subClass4 : subsAvailable) {
                        content4.append("<a action=\"bypass -h npc_%objectId%_Subclass 7 ").append(paramOne).append(" ").append(subClass4.getId()).append("\" msg=\"1445;\">").append(ClassListData.getInstance().getClass(subClass4.ordinal()).getClientCode()).append("</a><br>");
                    }
                    switch (paramOne) {
                        case 1: {
                            html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice1.htm");
                            break;
                        }
                        case 2: {
                            html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice2.htm");
                            break;
                        }
                        case 3: {
                            html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice3.htm");
                            break;
                        }
                        default: {
                            html.setFile(player, "data/html/villagemaster/SubClass_ModifyChoice.htm");
                            break;
                        }
                    }
                    html.replace("%list%", content4.toString());
                    break;
                }
                case 7: {
                    if (!player.getFloodProtectors().getSubclass().tryPerformAction("change class")) {
                        VillageMaster.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, VillageMaster.class.getName(), player.getName()));
                        return;
                    }
                    if (!this.isValidNewSubClass(player, paramTwo)) {
                        return;
                    }
                    if (player.modifySubClass(paramOne, paramTwo, false)) {
                        player.abortCast();
                        player.stopAllEffectsExceptThoseThatLastThroughDeath();
                        player.stopAllEffects();
                        player.stopCubics();
                        player.setActiveClass(paramOne);
                        html.setFile(player, "data/html/villagemaster/SubClass_ModifyOk.htm");
                        html.replace("%name%", ClassListData.getInstance().getClass(paramTwo).getClientCode());
                        player.sendPacket(SystemMessageId.THE_NEW_SUBCLASS_HAS_BEEN_ADDED);
                        break;
                    }
                    player.setActiveClass(0);
                    player.sendMessage("The sub class could not be added, you have been reverted to your base class.");
                    return;
                }
            }
            html.replace("%objectId%", String.valueOf(this.getObjectId()));
            player.sendPacket(html);
        }
        else {
            super.onBypassFeedback(player, command);
        }
    }
    
    protected String getSubClassMenu(final Race race) {
        return "data/html/villagemaster/SubClass.htm";
    }
    
    protected String getSubClassFail() {
        return "data/html/villagemaster/SubClass_Fail.htm";
    }
    
    protected boolean checkQuests(final Player player) {
        if (player.isNoble()) {
            return true;
        }
        QuestState qs = player.getQuestState("Q00234_FatesWhisper");
        if (qs == null || !qs.isCompleted()) {
            return false;
        }
        qs = player.getQuestState("Q00235_MimirsElixir");
        return qs != null && qs.isCompleted();
    }
    
    private Set<ClassId> getAvailableSubClasses(final Player player) {
        final int currentBaseId = player.getBaseClass();
        final ClassId baseCID = ClassId.getClassId(currentBaseId);
        int baseClassId;
        if (baseCID.level() > 2) {
            baseClassId = baseCID.getParent().getId();
        }
        else {
            baseClassId = currentBaseId;
        }
        final Set<ClassId> availSubs = this.getSubclasses(player, baseClassId);
        if (availSubs != null && !availSubs.isEmpty()) {
            final Iterator<ClassId> availSub = availSubs.iterator();
            while (availSub.hasNext()) {
                final ClassId pclass = availSub.next();
                if (!this.checkVillageMaster(pclass)) {
                    availSub.remove();
                }
                else {
                    final int availClassId = pclass.getId();
                    final ClassId cid = ClassId.getClassId(availClassId);
                    final Iterator<SubClass> subList = iterSubClasses(player);
                    while (subList.hasNext()) {
                        final SubClass prevSubClass = subList.next();
                        final ClassId subClassId = ClassId.getClassId(prevSubClass.getClassId());
                        if (subClassId.equalsOrChildOf(cid)) {
                            availSub.remove();
                            break;
                        }
                    }
                }
            }
        }
        return availSubs;
    }
    
    public final Set<ClassId> getSubclasses(final Player player, final int classId) {
        Set<ClassId> subclasses = null;
        final ClassId pClass = ClassId.getClassId(classId);
        if (CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, classId) || CategoryManager.getInstance().isInCategory(CategoryType.FOURTH_CLASS_GROUP, classId)) {
            subclasses = EnumSet.copyOf(VillageMaster.mainSubclassSet);
            subclasses.remove(pClass);
            switch (player.getRace()) {
                case JIN_KAMAEL: {
                    subclasses.removeIf(sub -> sub.getRace() != Race.JIN_KAMAEL);
                    break;
                }
                case ELF: {
                    subclasses.removeIf(sub -> sub.getRace() == Race.DARK_ELF || sub.getRace() == Race.JIN_KAMAEL);
                    break;
                }
                case DARK_ELF: {
                    subclasses.removeIf(sub -> sub.getRace() == Race.ELF || sub.getRace() == Race.JIN_KAMAEL);
                    break;
                }
                default: {
                    subclasses.removeIf(sub -> sub.getRace() == Race.JIN_KAMAEL);
                    break;
                }
            }
            final Set<ClassId> unavailableClasses = VillageMaster.subclassSetMap.get(pClass);
            if (unavailableClasses != null) {
                subclasses.removeAll(unavailableClasses);
            }
        }
        if (subclasses != null) {
            final ClassId currClassId = player.getClassId();
            final Set<ClassId> set = subclasses;
            final ClassId obj = currClassId;
            Objects.requireNonNull(obj);
            set.removeIf(obj::equalsOrChildOf);
        }
        return subclasses;
    }
    
    private boolean isValidNewSubClass(final Player player, final int classId) {
        if (!this.checkVillageMaster(classId)) {
            return false;
        }
        final ClassId cid = ClassId.getClassId(classId);
        final Iterator<SubClass> subList = iterSubClasses(player);
        while (subList.hasNext()) {
            final SubClass sub = subList.next();
            final ClassId subClassId = ClassId.getClassId(sub.getClassId());
            if (subClassId.equalsOrChildOf(cid)) {
                return false;
            }
        }
        final int currentBaseId = player.getBaseClass();
        final ClassId baseCID = ClassId.getClassId(currentBaseId);
        int baseClassId;
        if (baseCID.level() > 2) {
            baseClassId = baseCID.getParent().getId();
        }
        else {
            baseClassId = currentBaseId;
        }
        final Set<ClassId> availSubs = this.getSubclasses(player, baseClassId);
        if (availSubs == null || availSubs.isEmpty()) {
            return false;
        }
        boolean found = false;
        for (final ClassId pclass : availSubs) {
            if (pclass.getId() == classId) {
                found = true;
                break;
            }
        }
        return found;
    }
    
    protected boolean checkVillageMasterRace(final ClassId pclass) {
        return true;
    }
    
    protected boolean checkVillageMasterTeachType(final ClassId pclass) {
        return true;
    }
    
    public final boolean checkVillageMaster(final int classId) {
        return this.checkVillageMaster(ClassId.getClassId(classId));
    }
    
    public final boolean checkVillageMaster(final ClassId pclass) {
        return Config.ALT_GAME_SUBCLASS_EVERYWHERE || (this.checkVillageMasterRace(pclass) && this.checkVillageMasterTeachType(pclass));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)VillageMaster.class);
        neverSubclassed = EnumSet.of(ClassId.OVERLORD, ClassId.WARSMITH);
        subclasseSet1 = EnumSet.of(ClassId.DARK_AVENGER, ClassId.PALADIN, ClassId.TEMPLE_KNIGHT, ClassId.SHILLIEN_KNIGHT);
        subclasseSet2 = EnumSet.of(ClassId.TREASURE_HUNTER, ClassId.ABYSS_WALKER, ClassId.PLAINS_WALKER);
        subclasseSet3 = EnumSet.of(ClassId.HAWKEYE, ClassId.SILVER_RANGER, ClassId.PHANTOM_RANGER);
        subclasseSet4 = EnumSet.of(ClassId.WARLOCK, ClassId.ELEMENTAL_SUMMONER, ClassId.PHANTOM_SUMMONER);
        subclasseSet5 = EnumSet.of(ClassId.SORCERER, ClassId.SPELLSINGER, ClassId.SPELLHOWLER);
        subclassSetMap = new EnumMap<ClassId, Set<ClassId>>(ClassId.class);
        final Set<ClassId> subclasses = CategoryManager.getInstance().getCategoryByType(CategoryType.THIRD_CLASS_GROUP).stream().mapToObj((IntFunction<?>)ClassId::getClassId).collect((Collector<? super Object, ?, Set<ClassId>>)Collectors.toSet());
        subclasses.removeAll(VillageMaster.neverSubclassed);
        mainSubclassSet = subclasses;
        VillageMaster.subclassSetMap.put(ClassId.DARK_AVENGER, VillageMaster.subclasseSet1);
        VillageMaster.subclassSetMap.put(ClassId.PALADIN, VillageMaster.subclasseSet1);
        VillageMaster.subclassSetMap.put(ClassId.TEMPLE_KNIGHT, VillageMaster.subclasseSet1);
        VillageMaster.subclassSetMap.put(ClassId.SHILLIEN_KNIGHT, VillageMaster.subclasseSet1);
        VillageMaster.subclassSetMap.put(ClassId.TREASURE_HUNTER, VillageMaster.subclasseSet2);
        VillageMaster.subclassSetMap.put(ClassId.ABYSS_WALKER, VillageMaster.subclasseSet2);
        VillageMaster.subclassSetMap.put(ClassId.PLAINS_WALKER, VillageMaster.subclasseSet2);
        VillageMaster.subclassSetMap.put(ClassId.HAWKEYE, VillageMaster.subclasseSet3);
        VillageMaster.subclassSetMap.put(ClassId.SILVER_RANGER, VillageMaster.subclasseSet3);
        VillageMaster.subclassSetMap.put(ClassId.PHANTOM_RANGER, VillageMaster.subclasseSet3);
        VillageMaster.subclassSetMap.put(ClassId.WARLOCK, VillageMaster.subclasseSet4);
        VillageMaster.subclassSetMap.put(ClassId.ELEMENTAL_SUMMONER, VillageMaster.subclasseSet4);
        VillageMaster.subclassSetMap.put(ClassId.PHANTOM_SUMMONER, VillageMaster.subclasseSet4);
        VillageMaster.subclassSetMap.put(ClassId.SORCERER, VillageMaster.subclasseSet5);
        VillageMaster.subclassSetMap.put(ClassId.SPELLSINGER, VillageMaster.subclasseSet5);
        VillageMaster.subclassSetMap.put(ClassId.SPELLHOWLER, VillageMaster.subclasseSet5);
    }
}
