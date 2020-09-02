// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.communityboard;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.datatables.SchemeBufferTable;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.data.database.dao.ReportDAO;
import org.l2j.gameserver.data.database.data.ReportData;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ShowBoard;
import org.l2j.gameserver.network.serverpackets.ExBuySellList;
import org.l2j.gameserver.network.serverpackets.BuyList;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.CommunityDAO;
import java.util.function.Predicate;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.function.BiPredicate;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IParseBoardHandler;

public final class HomeBoard implements IParseBoardHandler
{
    private static final Logger LOGGER;
    private static final String NAVIGATION_PATH = "data/html/CommunityBoard/Custom/new/navigation.html";
    private static final int PAGE_LIMIT = 6;
    private static final String[] COMMANDS;
    private static final String[] CUSTOM_COMMANDS;
    private static final BiPredicate<String, Player> COMBAT_CHECK;
    private static final Predicate<Player> KARMA_CHECK;
    
    private static int getFavoriteCount(final Player player) {
        return ((CommunityDAO)DatabaseAccess.getDAO((Class)CommunityDAO.class)).getFavoritesCount(player.getObjectId());
    }
    
    private static int getRegionCount(final Player player) {
        return 0;
    }
    
    public String[] getCommunityBoardCommands() {
        final List<String> commands = new ArrayList<String>();
        commands.addAll(Arrays.asList(HomeBoard.COMMANDS));
        commands.addAll(Arrays.asList(HomeBoard.CUSTOM_COMMANDS));
        return commands.stream().filter(Objects::nonNull).toArray(String[]::new);
    }
    
    private String getSchemesListAsHtml(final Map<String, ArrayList<Integer>> schemes) {
        String result = "<tr><td height=4></td></tr>";
        int schemesCount = 0;
        if (schemes == null || schemes.isEmpty()) {
            result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        }
        else {
            for (final Map.Entry<String, ArrayList<Integer>> scheme : schemes.entrySet()) {
                if (schemesCount == 0 || schemesCount == 2) {
                    result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
                }
                result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, result, this.getSchemeTD(scheme));
                if (schemesCount == 1 || schemesCount == 3) {
                    result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
                }
                ++schemesCount;
            }
        }
        if (schemesCount == 1 || schemesCount == 3) {
            result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        }
        return result;
    }
    
    private String getSchemeTD(final Map.Entry<String, ArrayList<Integer>> scheme) {
        final int cost = getFee(scheme.getValue());
        String result = "";
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, result, (String)scheme.getKey());
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, result, (String)scheme.getKey());
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, result, (String)scheme.getKey());
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, result, (String)scheme.getKey(), cost);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, result, (String)scheme.getKey(), cost);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        result = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, result);
        return result;
    }
    
    public boolean parseCommunityBoardCommand(String command, final StringTokenizer tokens, final Player activeChar) {
        if (HomeBoard.COMBAT_CHECK.test(command, activeChar)) {
            activeChar.sendMessage("You can't use the Community Board right now.");
            return false;
        }
        if (HomeBoard.KARMA_CHECK.test(activeChar)) {
            activeChar.sendMessage("Players with Karma cannot use the Community Board.");
            return false;
        }
        String returnHtml = null;
        final String navigation = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/new/navigation.html");
        if (command.equals("_bbshome") || command.equals("_bbstop")) {
            final String customPath = Config.CUSTOM_CB_ENABLED ? "Custom/new/" : "";
            CommunityBoardHandler.getInstance().addBypass(activeChar, "Home", command);
            returnHtml = HtmCache.getInstance().getHtm(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, customPath));
        }
        else if (command.startsWith("_bbstop")) {
            final String customPath = Config.CUSTOM_CB_ENABLED ? "Custom/" : "";
            final String path = command.replace("_bbstop ", "");
            if (path.length() > 0 && path.endsWith(".html")) {
                returnHtml = HtmCache.getInstance().getHtm(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, customPath, path));
            }
        }
        else if (command.startsWith("_bbsmultisell")) {
            final String fullBypass = command.replace("_bbsmultisell ", "");
            final String[] buypassOptions = fullBypass.split(",");
            final int multisellId = Integer.parseInt(buypassOptions[0]);
            final String page = buypassOptions[1];
            returnHtml = HtmCache.getInstance().getHtm(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, page));
            MultisellData.getInstance().separateAndSend(multisellId, activeChar, (Npc)null, false);
        }
        else if (command.startsWith("_bbsexcmultisell")) {
            final String fullBypass = command.replace("_bbsexcmultisell ", "");
            final String[] buypassOptions = fullBypass.split(",");
            final int multisellId = Integer.parseInt(buypassOptions[0]);
            final String page = buypassOptions[1];
            returnHtml = HtmCache.getInstance().getHtm(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, page));
            MultisellData.getInstance().separateAndSend(multisellId, activeChar, (Npc)null, true);
        }
        else if (command.startsWith("_bbssell")) {
            final String page2 = command.replace("_bbssell ", "");
            returnHtml = HtmCache.getInstance().getHtm(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, page2));
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new BuyList(BuyListData.getInstance().getBuyList(423), activeChar, 0.0) });
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new ExBuySellList(activeChar, false) });
        }
        else if (command.startsWith("_bbsteleport")) {
            final String teleBuypass = command.replace("_bbsteleport ", "");
            if (activeChar.getInventory().getInventoryItemCount(Config.COMMUNITYBOARD_CURRENCY, -1) < Config.COMMUNITYBOARD_TELEPORT_PRICE) {
                activeChar.sendMessage("Not enough currency!");
            }
            else if (Config.COMMUNITY_AVAILABLE_TELEPORTS.get(teleBuypass) != null) {
                activeChar.disableAllSkills();
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new ShowBoard() });
                activeChar.destroyItemByItemId("CB_Teleport", Config.COMMUNITYBOARD_CURRENCY, (long)Config.COMMUNITYBOARD_TELEPORT_PRICE, (WorldObject)activeChar, true);
                activeChar.setInstanceById(0);
                activeChar.teleToLocation((ILocational)Config.COMMUNITY_AVAILABLE_TELEPORTS.get(teleBuypass), 0);
                Objects.requireNonNull(activeChar);
                ThreadPool.schedule(activeChar::enableAllSkills, 3000L);
            }
        }
        else if (command.startsWith("_bbsbuff")) {
            final String fullBypass = command.replace("_bbsbuff ", "");
            final String[] buypassOptions = fullBypass.split(";");
            final int buffCount = buypassOptions.length - 1;
            final String page = buypassOptions[buffCount];
            if (activeChar.getInventory().getInventoryItemCount(Config.COMMUNITYBOARD_CURRENCY, -1) < Config.COMMUNITYBOARD_BUFF_PRICE * buffCount) {
                activeChar.sendMessage("Not enough currency!");
            }
            else {
                activeChar.destroyItemByItemId("CB_Buff", Config.COMMUNITYBOARD_CURRENCY, (long)(Config.COMMUNITYBOARD_BUFF_PRICE * buffCount), (WorldObject)activeChar, true);
                final Pet pet = activeChar.getPet();
                final List<Creature> targets = new ArrayList<Creature>(4);
                targets.add((Creature)activeChar);
                if (pet != null) {
                    targets.add((Creature)pet);
                }
                targets.addAll(activeChar.getServitors().values());
                for (int i = 0; i < buffCount; ++i) {
                    final Skill skill = SkillEngine.getInstance().getSkill(Integer.parseInt(buypassOptions[i].split(",")[0]), Integer.parseInt(buypassOptions[i].split(",")[1]));
                    if (Config.COMMUNITY_AVAILABLE_BUFFS.contains(skill.getId())) {
                        final Skill skill2;
                        final Skill skill3;
                        targets.stream().filter(target -> !GameUtils.isSummon(target) || !skill2.isSharedWithSummon()).forEach(target -> {
                            skill3.applyEffects((Creature)activeChar, (Creature)target);
                            if (Config.COMMUNITYBOARD_CAST_ANIMATIONS) {
                                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new MagicSkillUse((Creature)activeChar, target, skill3.getId(), skill3.getLevel(), skill3.getHitTime(), skill3.getReuseDelay()) });
                            }
                            return;
                        });
                    }
                }
            }
            returnHtml = HtmCache.getInstance().getHtm(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, page));
        }
        else if (command.startsWith("_bbsheal")) {
            final String page2 = command.replace("_bbsheal ", "");
            if (activeChar.getInventory().getInventoryItemCount(Config.COMMUNITYBOARD_CURRENCY, -1) < Config.COMMUNITYBOARD_HEAL_PRICE) {
                activeChar.sendMessage("Not enough currency!");
            }
            else {
                activeChar.destroyItemByItemId("CB_Heal", Config.COMMUNITYBOARD_CURRENCY, (long)Config.COMMUNITYBOARD_HEAL_PRICE, (WorldObject)activeChar, true);
                activeChar.setCurrentHp((double)activeChar.getMaxHp());
                activeChar.setCurrentMp((double)activeChar.getMaxMp());
                activeChar.setCurrentCp((double)activeChar.getMaxCp());
                if (activeChar.hasPet()) {
                    activeChar.getPet().setCurrentHp((double)activeChar.getPet().getMaxHp());
                    activeChar.getPet().setCurrentMp((double)activeChar.getPet().getMaxMp());
                    activeChar.getPet().setCurrentCp((double)activeChar.getPet().getMaxCp());
                }
                for (final Summon summon : activeChar.getServitors().values()) {
                    summon.setCurrentHp((double)summon.getMaxHp());
                    summon.setCurrentMp((double)summon.getMaxMp());
                    summon.setCurrentCp((double)summon.getMaxCp());
                }
                activeChar.sendMessage("You used heal!");
            }
            returnHtml = HtmCache.getInstance().getHtm(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, page2));
        }
        else if (command.startsWith("_bbsreport")) {
            final String reportText = command.replace("_bbsreport ", "");
            if (Util.isNotEmpty(reportText)) {
                final ReportData report = new ReportData();
                report.setPlayerId(activeChar.getObjectId());
                report.setReport(reportText);
                report.setPending(true);
                ((ReportDAO)DatabaseAccess.getDAO((Class)ReportDAO.class)).save((Object)report);
                activeChar.sendMessage("Thank you For your Report!! the GM will be informed!");
                AdminData.getInstance().broadcastMessageToGMs(String.format("Player: %s (%s) has just submitted a report!", activeChar.getName(), activeChar.getObjectId()));
            }
        }
        else if (command.startsWith("_bbspremium")) {
            final String fullBypass = command.replace("_bbspremium ", "");
            final String[] buypassOptions = fullBypass.split(";");
            final long buypassL2Coins = Long.parseLong(buypassOptions[0]);
            final long buypassVIPPoints = Long.parseLong(buypassOptions[1]);
            if (activeChar.getVipTier() >= 5) {
                activeChar.sendMessage("Max VIP already reached!");
            }
            else if (activeChar.getLCoins() <= buypassL2Coins) {
                activeChar.sendMessage("Not enough currency!");
            }
            else {
                activeChar.addLCoins(-buypassL2Coins);
                activeChar.updateVipPoints(buypassVIPPoints);
            }
        }
        else if (command.startsWith("_bbscreatescheme")) {
            command = command.replace("_bbscreatescheme ", "");
            boolean canCreateScheme = true;
            try {
                final String schemeName = command.trim();
                if (schemeName.length() > 14) {
                    activeChar.sendMessage("Scheme's name must contain up to 14 chars.");
                    canCreateScheme = false;
                }
                if (!Util.isAlphaNumeric(schemeName.replace(" ", "").replace(".", "").replace(",", "").replace("-", "").replace("+", "").replace("!", "").replace("?", ""))) {
                    activeChar.sendMessage("Please use plain alphanumeric characters.");
                    canCreateScheme = false;
                }
                final Map<String, ArrayList<Integer>> schemes = (Map<String, ArrayList<Integer>>)SchemeBufferTable.getInstance().getPlayerSchemes(activeChar.getObjectId());
                if (schemes != null) {
                    if (schemes.size() == Config.BUFFER_MAX_SCHEMES) {
                        activeChar.sendMessage("Maximum schemes amount is already reached.");
                        canCreateScheme = false;
                    }
                    if (schemes.containsKey(schemeName)) {
                        activeChar.sendMessage("The scheme name already exists.");
                        canCreateScheme = false;
                    }
                }
                if (canCreateScheme) {
                    SchemeBufferTable.getInstance().setScheme(activeChar.getObjectId(), schemeName.trim(), new ArrayList());
                    returnHtml = this.showEditSchemeWindow(activeChar, "Buffs", schemeName, 1, returnHtml);
                }
                else {
                    returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/new/services-buffer.html");
                }
            }
            catch (Exception e) {
                activeChar.sendMessage(e.getMessage());
            }
        }
        else if (command.startsWith("_bbseditscheme")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            final String currentCommand = st.nextToken();
            final String groupType = st.nextToken();
            final String schemeName2 = st.nextToken();
            final int page3 = Integer.parseInt(st.nextToken());
            returnHtml = this.showEditSchemeWindow(activeChar, groupType, schemeName2, page3, returnHtml);
        }
        else if (command.startsWith("_bbsskill")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            final String currentCommand = st.nextToken();
            final String groupType = st.nextToken();
            final String schemeName2 = st.nextToken();
            final int skillId = Integer.parseInt(st.nextToken());
            final int page4 = Integer.parseInt(st.nextToken());
            final List<Integer> skills = (List<Integer>)SchemeBufferTable.getInstance().getScheme(activeChar.getObjectId(), schemeName2);
            if (currentCommand.startsWith("_bbsskillselect") && !schemeName2.equalsIgnoreCase("none")) {
                final Skill skill = SkillEngine.getInstance().getSkill(skillId, SkillEngine.getInstance().getMaxLevel(skillId));
                if (skill.isDance()) {
                    if (getCountOf(skills, true) < Config.DANCES_MAX_AMOUNT) {
                        skills.add(skillId);
                    }
                    else {
                        activeChar.sendMessage("This scheme has reached the maximum amount of dances/songs.");
                    }
                }
                else if (getCountOf(skills, false) < Config.BUFFS_MAX_AMOUNT) {
                    skills.add(skillId);
                }
                else {
                    activeChar.sendMessage("This scheme has reached the maximum amount of buffs.");
                }
            }
            else if (currentCommand.startsWith("_bbsskillunselect")) {
                skills.remove((Object)skillId);
            }
            returnHtml = this.showEditSchemeWindow(activeChar, groupType, schemeName2, page4, returnHtml);
        }
        else if (command.startsWith("_bbsgivebuffs")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            final String currentCommand = st.nextToken();
            final String schemeName3 = st.nextToken();
            final long cost = Integer.parseInt(st.nextToken());
            Creature target2 = null;
            if (st.hasMoreTokens()) {
                final String targetType = st.nextToken();
                if (targetType != null && targetType.equalsIgnoreCase("pet")) {
                    target2 = (Creature)activeChar.getPet();
                }
                else if (targetType != null && targetType.equalsIgnoreCase("summon")) {
                    for (final Summon summon2 : activeChar.getServitorsAndPets()) {
                        if (summon2.isServitor()) {
                            target2 = (Creature)summon2;
                        }
                    }
                }
            }
            else {
                target2 = (Creature)activeChar;
            }
            if (target2 == null) {
                activeChar.sendMessage("You don't have a pet.");
            }
            else if (cost == 0L || activeChar.reduceAdena("Community Board Buffer", cost, (WorldObject)target2, true)) {
                for (final int skillId2 : SchemeBufferTable.getInstance().getScheme(activeChar.getObjectId(), schemeName3)) {
                    SkillEngine.getInstance().getSkill(skillId2, SkillEngine.getInstance().getMaxLevel(skillId2)).applyEffects(target2, target2);
                }
            }
            returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/new/services-buffer.html");
        }
        else if (command.startsWith("_bbsdeletescheme")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            final String currentCommand = st.nextToken();
            final String schemeName3 = st.nextToken();
            final Map<String, ArrayList<Integer>> schemes2 = (Map<String, ArrayList<Integer>>)SchemeBufferTable.getInstance().getPlayerSchemes(activeChar.getObjectId());
            if (schemes2 != null && schemes2.containsKey(schemeName3)) {
                schemes2.remove(schemeName3);
            }
            returnHtml = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/Custom/new/services-buffer.html");
        }
        if (Objects.nonNull(returnHtml)) {
            if (Config.CUSTOM_CB_ENABLED) {
                final Map<String, ArrayList<Integer>> schemes3 = (Map<String, ArrayList<Integer>>)SchemeBufferTable.getInstance().getPlayerSchemes(activeChar.getObjectId());
                returnHtml = returnHtml.replace("%schemes%", this.getSchemesListAsHtml(schemes3));
                returnHtml = returnHtml.replace("%max_schemes%", Integer.toString(Config.BUFFER_MAX_SCHEMES));
                returnHtml = returnHtml.replace("%navigation%", navigation);
                returnHtml = returnHtml.replaceAll("%name%", activeChar.getName());
                returnHtml = returnHtml.replaceAll("%premium%", "Could not find account setup");
                returnHtml = returnHtml.replaceAll("%clan%", (activeChar.getClan() != null) ? activeChar.getClan().getName() : "No clan");
                returnHtml = returnHtml.replaceAll("%alliance%", "Could not find it");
                returnHtml = returnHtml.replaceAll("%country%", "Could not found it");
                returnHtml = returnHtml.replaceAll("%class%", activeChar.getBaseTemplate().getClassId().name().replace("_", " "));
                returnHtml = returnHtml.replaceAll("%exp%", String.valueOf(activeChar.getExp()));
                returnHtml = returnHtml.replaceAll("%adena%", String.valueOf(activeChar.getAdena()));
                returnHtml = returnHtml.replaceAll("%online%", String.valueOf(activeChar.getUptime()));
                returnHtml = returnHtml.replaceAll("%onlinePlayers%", String.valueOf(World.getInstance().getPlayers().size()));
            }
            else {
                returnHtml = returnHtml.replaceAll("%fav_count%", Integer.toString(getFavoriteCount(activeChar)));
                returnHtml = returnHtml.replaceAll("%region_count%", Integer.toString(getRegionCount(activeChar)));
                returnHtml = returnHtml.replaceAll("%clan_count%", Integer.toString(ClanTable.getInstance().getClanCount()));
            }
            CommunityBoardHandler.separateAndSend(returnHtml, activeChar);
        }
        return false;
    }
    
    private String setHtmlSchemeBuffList(final Player player, final String groupType, final String schemeName, final List<Integer> skills, final int page, String returnHtml) {
        int skillCount = 0;
        int buffCount = 1;
        int danceCount = 1;
        for (int i = 1; i <= 36; ++i) {
            Skill skill = null;
            if (skillCount < skills.size()) {
                skill = SkillEngine.getInstance().getSkill((int)skills.get(skillCount), 1);
                if (!skill.isDance()) {
                    returnHtml = this.replaceVars(buffCount, groupType, schemeName, skill.getIcon(), skills.get(skillCount), page, returnHtml);
                    ++buffCount;
                }
                else {
                    returnHtml = this.replaceVars(danceCount + 24, groupType, schemeName, skill.getIcon(), skills.get(skillCount), page, returnHtml);
                    ++danceCount;
                }
                ++skillCount;
            }
        }
        for (int i = 1; i <= 36; ++i) {
            returnHtml = this.replaceVars(i, groupType, schemeName, "L2UI_CT1.l2ui_ct1.ComboBox_DF_Dropmenu_Bg", -1, page, returnHtml);
        }
        return returnHtml;
    }
    
    private String replaceVars(final int index, final String groupType, final String schemeName, final String skillIcon, final int skillID, final int page, String returnHtml) {
        final String command = (skillID > -1) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, groupType, schemeName, skillID, page) : "";
        returnHtml = returnHtml.replace(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, index), (CharSequence)skillIcon);
        returnHtml = returnHtml.replace(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, index), (CharSequence)command);
        return returnHtml;
    }
    
    private String showEditSchemeWindow(final Player player, final String groupType, final String schemeName, final int page, String returnHtml) {
        returnHtml = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/Custom/new/services-buffer-editscheme.html");
        final List<Integer> schemeSkills = (List<Integer>)SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
        returnHtml = this.setHtmlSchemeBuffList(player, groupType, schemeName, schemeSkills, page, returnHtml);
        returnHtml = returnHtml.replace("%schemename%", schemeName);
        returnHtml = returnHtml.replace((CharSequence)"%count%", invokedynamic(makeConcatWithConstants:(JBJB)Ljava/lang/String;, getCountOf(schemeSkills, false), Config.BUFFS_MAX_AMOUNT, getCountOf(schemeSkills, true), Config.DANCES_MAX_AMOUNT));
        returnHtml = returnHtml.replace("%typesframe%", getTypesFrame(groupType, schemeName));
        returnHtml = returnHtml.replace("%skilllistframe%", this.getGroupSkillList(player, groupType, schemeName, page));
        return returnHtml;
    }
    
    private String getGroupSkillList(final Player player, final String groupType, final String schemeName, int page) {
        final List<Integer> skills = (List<Integer>)SchemeBufferTable.getInstance().getSkillsIdsByType(groupType);
        if (skills.isEmpty()) {
            return "That group doesn't contain any skills.";
        }
        final int max = countPagesNumber(skills.size(), 6);
        if (page > max) {
            page = max;
        }
        final List<Integer> schemeSkills = (List<Integer>)SchemeBufferTable.getInstance().getScheme(player.getObjectId(), schemeName);
        final StringBuilder sb = new StringBuilder(skills.size() * 150);
        int column = 0;
        final int maxColumn = (skills.size() <= 16) ? 4 : 7;
        sb.append("<table background=L2UI_CT1.Windows_DF_TooltipBG>");
        for (final int skillId : skills) {
            if (column == 0) {
                sb.append("<tr>");
                sb.append("<td height=10>");
                sb.append("</td>");
            }
            final Skill skill = SkillEngine.getInstance().getSkill(skillId, 1);
            if (schemeSkills.contains(skillId)) {
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, skill.getIcon(), groupType, schemeName, skillId, page));
            }
            else {
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, skill.getIcon(), groupType, schemeName, skillId, page));
            }
            if (++column == maxColumn) {
                sb.append("</tr>");
                column = 0;
            }
        }
        if (!sb.toString().endsWith("</tr>")) {
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
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
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, type));
            }
            else {
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, type, schemeName, type));
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
    
    private static int getFee(final List<Integer> list) {
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
        return skills.stream().filter(sId -> SkillEngine.getInstance().getSkill((int)sId, 1).isDance() == dances).count();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)HomeBoard.class);
        COMMANDS = new String[] { "_bbshome", "_bbstop", "_bbsreport" };
        CUSTOM_COMMANDS = new String[] { Config.COMMUNITYBOARD_ENABLE_MULTISELLS ? "_bbsexcmultisell" : null, Config.COMMUNITYBOARD_ENABLE_MULTISELLS ? "_bbsmultisell" : null, Config.COMMUNITYBOARD_ENABLE_MULTISELLS ? "_bbssell" : null, Config.COMMUNITYBOARD_ENABLE_TELEPORTS ? "_bbsteleport" : null, Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbsbuff" : null, Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbscreatescheme" : null, Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbseditscheme" : null, Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbsdeletescheme" : null, Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbsskillselect" : null, Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbsskillunselect" : null, Config.COMMUNITYBOARD_ENABLE_BUFFS ? "_bbsgivebuffs" : null, Config.COMMUNITYBOARD_ENABLE_HEAL ? "_bbsheal" : null, Config.COMMUNITYBOARD_ENABLE_PREMIUM ? "_bbspremium" : null, Config.COMMUNITYBOARD_ENABLE_AUTO_HP_MP_CP ? "_bbsautohpmpcp" : null };
        boolean commandCheck;
        final String[] custom_COMMANDS;
        final int length;
        int i = 0;
        String c;
        COMBAT_CHECK = ((command, activeChar) -> {
            commandCheck = false;
            custom_COMMANDS = HomeBoard.CUSTOM_COMMANDS;
            length = custom_COMMANDS.length;
            while (i < length) {
                c = custom_COMMANDS[i];
                if (c != null && command.startsWith(c)) {
                    commandCheck = true;
                    break;
                }
                else {
                    ++i;
                }
            }
            return commandCheck && (activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isInDuel() || activeChar.isInOlympiadMode() || activeChar.isInsideZone(ZoneType.SIEGE) || activeChar.isInsideZone(ZoneType.PVP));
        });
        KARMA_CHECK = (player -> Config.COMMUNITYBOARD_KARMA_DISABLED && player.getReputation() < 0);
    }
}
