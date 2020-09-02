// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.Arrays;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import io.github.joealisson.primitive.IntMap;
import java.util.function.Consumer;
import java.util.StringJoiner;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import java.util.Objects;
import org.l2j.gameserver.model.html.PageResult;
import java.util.List;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.GMViewItemList;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.network.serverpackets.PartySmallWindowAll;
import org.l2j.gameserver.network.serverpackets.PartySmallWindowDeleteAll;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.network.serverpackets.ExSubjobInfo;
import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminEditChar implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_current_player")) {
            this.showCharacterInfo(activeChar, activeChar);
        }
        else if (command.startsWith("admin_character_info")) {
            final String[] data = command.split(" ");
            if (data.length > 1) {
                this.showCharacterInfo(activeChar, World.getInstance().findPlayer(data[1]));
            }
            else if (GameUtils.isPlayer(activeChar.getTarget())) {
                this.showCharacterInfo(activeChar, activeChar.getTarget().getActingPlayer());
            }
            else {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            }
        }
        else if (command.startsWith("admin_character_list")) {
            this.listCharacters(activeChar, 0);
        }
        else if (command.startsWith("admin_show_characters")) {
            try {
                final String val = command.substring(22);
                final int page = Integer.parseInt(val);
                this.listCharacters(activeChar, page);
            }
            catch (StringIndexOutOfBoundsException e2) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //show_characters <page_number>");
            }
        }
        else if (command.startsWith("admin_find_character")) {
            try {
                final String val = command.substring(21);
                this.findCharacter(activeChar, val);
            }
            catch (StringIndexOutOfBoundsException e2) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //find_character <character_name>");
                this.listCharacters(activeChar, 0);
            }
        }
        else if (command.startsWith("admin_find_ip")) {
            try {
                final String val = command.substring(14);
                this.findCharactersPerIp(activeChar, val);
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //find_ip <www.xxx.yyy.zzz>");
                this.listCharacters(activeChar, 0);
            }
        }
        else if (command.startsWith("admin_find_account")) {
            try {
                final String val = command.substring(19);
                this.findCharactersPerAccount(activeChar, val);
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //find_account <player_name>");
                this.listCharacters(activeChar, 0);
            }
        }
        else if (command.startsWith("admin_edit_character")) {
            final String[] data = command.split(" ");
            if (data.length > 1) {
                this.editCharacter(activeChar, data[1]);
            }
            else if (GameUtils.isPlayer(activeChar.getTarget())) {
                this.editCharacter(activeChar, null);
            }
            else {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            }
        }
        else if (command.startsWith("admin_setreputation")) {
            try {
                final String val = command.substring(20);
                final int reputation = Integer.parseInt(val);
                this.setTargetReputation(activeChar, reputation);
            }
            catch (Exception e) {
                if (Config.DEVELOPER) {
                    AdminEditChar.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e));
                }
                BuilderUtil.sendSysMessage(activeChar, "Usage: //setreputation <new_reputation_value>");
            }
        }
        else if (command.startsWith("admin_nokarma")) {
            if (!GameUtils.isPlayer(activeChar.getTarget())) {
                BuilderUtil.sendSysMessage(activeChar, "You must target a player.");
                return false;
            }
            if (activeChar.getTarget().getActingPlayer().getReputation() < 0) {
                this.setTargetReputation(activeChar, 0);
            }
        }
        else if (command.startsWith("admin_setpk")) {
            try {
                final String val = command.substring(12);
                final int pk = Integer.parseInt(val);
                final WorldObject target = activeChar.getTarget();
                if (GameUtils.isPlayer(target)) {
                    final Player player = target.getActingPlayer();
                    player.setPkKills(pk);
                    player.broadcastUserInfo();
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new UserInfo(player) });
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, pk));
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, player.getName(), pk));
                }
                else {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                }
            }
            catch (Exception e) {
                if (Config.DEVELOPER) {
                    AdminEditChar.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e));
                }
                BuilderUtil.sendSysMessage(activeChar, "Usage: //setpk <pk_count>");
            }
        }
        else if (command.startsWith("admin_setpvp")) {
            try {
                final String val = command.substring(13);
                final int pvp = Integer.parseInt(val);
                final WorldObject target = activeChar.getTarget();
                if (target != null && GameUtils.isPlayer(target)) {
                    final Player player = (Player)target;
                    player.setPvpKills(pvp);
                    player.updatePvpTitleAndColor(false);
                    player.broadcastUserInfo();
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new UserInfo(player) });
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, pvp));
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, player.getName(), pvp));
                }
                else {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                }
            }
            catch (Exception e) {
                if (Config.DEVELOPER) {
                    AdminEditChar.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e));
                }
                BuilderUtil.sendSysMessage(activeChar, "Usage: //setpvp <pvp_count>");
            }
        }
        else if (command.startsWith("admin_setfame")) {
            try {
                final String val = command.substring(14);
                final int fame = Integer.parseInt(val);
                final WorldObject target = activeChar.getTarget();
                if (GameUtils.isPlayer(target)) {
                    final Player player = (Player)target;
                    player.setFame(fame);
                    player.broadcastUserInfo();
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new UserInfo(player) });
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, fame));
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, player.getName(), fame));
                }
                else {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                }
            }
            catch (Exception e) {
                if (Config.DEVELOPER) {
                    AdminEditChar.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e));
                }
                BuilderUtil.sendSysMessage(activeChar, "Usage: //setfame <new_fame_value>");
            }
        }
        else if (command.startsWith("admin_rec")) {
            try {
                final String val = command.substring(10);
                final int recVal = Integer.parseInt(val);
                final WorldObject target = activeChar.getTarget();
                if (GameUtils.isPlayer(target)) {
                    final Player player = (Player)target;
                    player.setRecomHave(recVal);
                    player.broadcastUserInfo();
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, recVal));
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, player.getName(), recVal));
                }
                else {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                }
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //rec number");
            }
        }
        else if (command.startsWith("admin_setclass")) {
            try {
                final String val = command.substring(15).trim();
                final int classidval = Integer.parseInt(val);
                final WorldObject target = activeChar.getTarget();
                if (target == null || !GameUtils.isPlayer(target)) {
                    return false;
                }
                final Player player = target.getActingPlayer();
                if (ClassId.getClassId(classidval) != null && player.getClassId().getId() != classidval) {
                    player.setClassId(classidval);
                    if (player.isSubClassActive()) {
                        ((SubClass)player.getSubClasses().get(player.getClassIndex())).setClassId(player.getActiveClass());
                    }
                    else {
                        player.setBaseClass(player.getActiveClass());
                    }
                    final String newclass = ClassListData.getInstance().getClass(player.getClassId()).getClassName();
                    player.store(false);
                    player.broadcastUserInfo();
                    player.sendSkillList();
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED) });
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new ExUserInfoInvenWeight(player) });
                    player.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, newclass));
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), newclass));
                }
                else {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //setclass <valid_new_classid>");
                }
            }
            catch (StringIndexOutOfBoundsException e2) {
                AdminHtml.showAdminHtml(activeChar, "setclass/human_fighter.htm");
            }
            catch (NumberFormatException e3) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //setclass <valid_new_classid>");
            }
        }
        else if (command.startsWith("admin_settitle")) {
            try {
                final String val = command.substring(15);
                final WorldObject target2 = activeChar.getTarget();
                Player player2 = null;
                if (target2 == null || !GameUtils.isPlayer(target2)) {
                    return false;
                }
                player2 = (Player)target2;
                player2.setTitle(val);
                player2.sendPacket(SystemMessageId.YOUR_TITLE_HAS_BEEN_CHANGED);
                player2.broadcastTitleInfo();
            }
            catch (StringIndexOutOfBoundsException e2) {
                BuilderUtil.sendSysMessage(activeChar, "You need to specify the new title.");
            }
        }
        else if (command.startsWith("admin_changename")) {
            try {
                final String val = command.substring(17);
                final WorldObject target2 = activeChar.getTarget();
                Player player2 = null;
                if (target2 == null || !GameUtils.isPlayer(target2)) {
                    return false;
                }
                player2 = (Player)target2;
                if (PlayerNameTable.getInstance().doesCharNameExist(val)) {
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, val));
                    return false;
                }
                player2.setName(val);
                if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).cachePlayersName()) {
                    PlayerNameTable.getInstance().addName(player2);
                }
                player2.storeMe();
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, val));
                player2.sendMessage("Your name has been changed by a GM.");
                player2.broadcastUserInfo();
                if (player2.isInParty()) {
                    player2.getParty().broadcastToPartyMembers(player2, (ServerPacket)PartySmallWindowDeleteAll.STATIC_PACKET);
                    for (final Player member : player2.getParty().getMembers()) {
                        if (member != player2) {
                            member.sendPacket(new ServerPacket[] { (ServerPacket)new PartySmallWindowAll(member, player2.getParty()) });
                        }
                    }
                }
                if (player2.getClan() != null) {
                    player2.getClan().broadcastClanStatus();
                }
            }
            catch (StringIndexOutOfBoundsException e2) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //setname new_name_for_target");
            }
        }
        else if (command.startsWith("admin_setsex")) {
            final WorldObject target3 = activeChar.getTarget();
            Player player3 = null;
            if (target3 == null || !GameUtils.isPlayer(target3)) {
                return false;
            }
            player3 = (Player)target3;
            player3.getAppearance().setFemale(!player3.getAppearance().isFemale());
            player3.sendMessage("Your gender has been changed by a GM");
            player3.broadcastUserInfo();
        }
        else if (command.startsWith("admin_setcolor")) {
            try {
                final String val = command.substring(15);
                final WorldObject target2 = activeChar.getTarget();
                Player player2 = null;
                if (target2 == null || !GameUtils.isPlayer(target2)) {
                    return false;
                }
                player2 = (Player)target2;
                player2.getAppearance().setNameColor((int)Integer.decode(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, val)));
                player2.sendMessage("Your name color has been changed by a GM");
                player2.broadcastUserInfo();
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "You need to specify a valid new color.");
            }
        }
        else if (command.startsWith("admin_settcolor")) {
            try {
                final String val = command.substring(16);
                final WorldObject target2 = activeChar.getTarget();
                Player player2 = null;
                if (target2 == null || !GameUtils.isPlayer(target2)) {
                    return false;
                }
                player2 = (Player)target2;
                player2.getAppearance().setTitleColor((int)Integer.decode(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, val)));
                player2.sendMessage("Your title color has been changed by a GM");
                player2.broadcastUserInfo();
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "You need to specify a valid new color.");
            }
        }
        else if (command.startsWith("admin_fullfood")) {
            final WorldObject target3 = activeChar.getTarget();
            if (target3 != null && GameUtils.isPet(target3)) {
                final Pet targetPet = (Pet)target3;
                targetPet.setCurrentFed(targetPet.getMaxFed());
                targetPet.broadcastStatusUpdate();
            }
            else {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            }
        }
        else if (command.startsWith("admin_remove_clan_penalty")) {
            try {
                final StringTokenizer st = new StringTokenizer(command, " ");
                if (st.countTokens() != 3) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //remove_clan_penalty join|create charname");
                    return false;
                }
                st.nextToken();
                final boolean changeCreateExpiryTime = st.nextToken().equalsIgnoreCase("create");
                final String playerName = st.nextToken();
                Player player = null;
                player = World.getInstance().findPlayer(playerName);
                if (player == null) {
                    ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).removeClanPenalty(playerName);
                }
                else if (changeCreateExpiryTime) {
                    player.setClanCreateExpiryTime(0L);
                }
                else {
                    player.setClanJoinExpiryTime(0L);
                }
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, playerName));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (command.startsWith("admin_find_dualbox")) {
            int multibox = 2;
            try {
                final String val2 = command.substring(19);
                multibox = Integer.parseInt(val2);
                if (multibox < 1) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //find_dualbox [number > 0]");
                    return false;
                }
            }
            catch (Exception ex) {}
            this.findDualbox(activeChar, multibox);
        }
        else if (command.startsWith("admin_strict_find_dualbox")) {
            int multibox = 2;
            try {
                final String val2 = command.substring(26);
                multibox = Integer.parseInt(val2);
                if (multibox < 1) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //strict_find_dualbox [number > 0]");
                    return false;
                }
            }
            catch (Exception ex2) {}
            this.findDualboxStrict(activeChar, multibox);
        }
        else if (command.startsWith("admin_tracert")) {
            final String[] data = command.split(" ");
            Player pl = null;
            if (data.length > 1) {
                pl = World.getInstance().findPlayer(data[1]);
            }
            else {
                final WorldObject target = activeChar.getTarget();
                if (target != null && GameUtils.isPlayer(target)) {
                    pl = (Player)target;
                }
            }
            if (pl == null) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                return false;
            }
            final GameClient client = pl.getClient();
            if (client == null) {
                BuilderUtil.sendSysMessage(activeChar, "Client is null.");
                return false;
            }
            final int[][] trace = client.getTrace();
            for (int i = 0; i < trace.length; ++i) {
                String ip = "";
                for (int o = 0; o < trace[0].length; ++o) {
                    ip = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, ip, trace[i][o]);
                    if (o != trace[0].length - 1) {
                        ip = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, ip);
                    }
                }
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, i, ip));
            }
        }
        else if (command.startsWith("admin_summon_info")) {
            final WorldObject target3 = activeChar.getTarget();
            if (target3 != null && GameUtils.isSummon(target3)) {
                this.gatherSummonInfo((Summon)target3, activeChar);
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, "Invalid target.");
            }
        }
        else if (command.startsWith("admin_unsummon")) {
            final WorldObject target3 = activeChar.getTarget();
            if (target3 != null && GameUtils.isSummon(target3)) {
                ((Summon)target3).unSummon(((Summon)target3).getOwner());
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, "Usable only with Pets/Summons");
            }
        }
        else if (command.startsWith("admin_summon_setlvl")) {
            final WorldObject target3 = activeChar.getTarget();
            if (target3 != null && GameUtils.isPet(target3)) {
                final Pet pet = (Pet)target3;
                try {
                    final String val3 = command.substring(20);
                    final int level = Integer.parseInt(val3);
                    final long oldexp = pet.getStats().getExp();
                    final long newexp = pet.getStats().getExpForLevel(level);
                    if (oldexp > newexp) {
                        pet.getStats().removeExp(oldexp - newexp);
                    }
                    else if (oldexp < newexp) {
                        pet.getStats().addExp(newexp - oldexp);
                    }
                }
                catch (Exception ex3) {}
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, "Usable only with Pets");
            }
        }
        else if (command.startsWith("admin_show_pet_inv")) {
            WorldObject target3;
            try {
                final String val2 = command.substring(19);
                final int objId = Integer.parseInt(val2);
                target3 = (WorldObject)World.getInstance().findPet(objId);
            }
            catch (Exception e4) {
                target3 = activeChar.getTarget();
            }
            if (target3 != null && GameUtils.isPet(target3)) {
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new GMViewItemList(1, (Pet)target3) });
            }
            else {
                BuilderUtil.sendSysMessage(activeChar, "Usable only with Pets");
            }
        }
        else if (command.startsWith("admin_partyinfo")) {
            WorldObject target3;
            try {
                final String val2 = command.substring(16);
                target3 = (WorldObject)World.getInstance().findPlayer(val2);
                if (target3 == null) {
                    target3 = activeChar.getTarget();
                }
            }
            catch (Exception e4) {
                target3 = activeChar.getTarget();
            }
            if (GameUtils.isPlayer(target3)) {
                if (((Player)target3).isInParty()) {
                    this.gatherPartyInfo((Player)target3, activeChar);
                }
                else {
                    BuilderUtil.sendSysMessage(activeChar, "Not in party.");
                }
            }
            else {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            }
        }
        else if (command.equals("admin_setnoble")) {
            Player player4;
            if (GameUtils.isPlayer(activeChar.getTarget())) {
                player4 = (Player)activeChar.getTarget();
            }
            else {
                player4 = activeChar;
            }
            player4.setNoble(!player4.isNoble());
            if (player4.getObjectId() != activeChar.getObjectId()) {
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player4.getName()));
            }
            player4.broadcastUserInfo();
            player4.sendMessage("GM changed your nobless status!");
        }
        else if (command.startsWith("admin_set_hp")) {
            final String[] data = command.split(" ");
            try {
                final WorldObject target2 = activeChar.getTarget();
                if (!GameUtils.isCreature(target2)) {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                    return false;
                }
                ((Creature)target2).setCurrentHp(Double.parseDouble(data[1]));
            }
            catch (Exception e4) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //set_hp 1000");
            }
        }
        else if (command.startsWith("admin_set_mp")) {
            final String[] data = command.split(" ");
            try {
                final WorldObject target2 = activeChar.getTarget();
                if (!GameUtils.isCreature(target2)) {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                    return false;
                }
                ((Creature)target2).setCurrentMp(Double.parseDouble(data[1]));
            }
            catch (Exception e4) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //set_mp 1000");
            }
        }
        else if (command.startsWith("admin_set_cp")) {
            final String[] data = command.split(" ");
            try {
                final WorldObject target2 = activeChar.getTarget();
                if (!GameUtils.isCreature(target2)) {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                    return false;
                }
                ((Creature)target2).setCurrentCp(Double.parseDouble(data[1]));
            }
            catch (Exception e4) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //set_cp 1000");
            }
        }
        else if (command.startsWith("admin_set_pvp_flag")) {
            try {
                final WorldObject target3 = activeChar.getTarget();
                if (!GameUtils.isPlayable(target3)) {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                    return false;
                }
                final Playable playable = (Playable)target3;
                playable.updatePvPFlag(Math.abs(playable.getPvpFlag() - 1));
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //set_pvp_flag");
            }
        }
        else if (command.startsWith("admin_setparam")) {
            final WorldObject target3 = activeChar.getTarget();
            if (!GameUtils.isCreature(target3)) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                return false;
            }
            final StringTokenizer st2 = new StringTokenizer(command, " ");
            st2.nextToken();
            if (!st2.hasMoreTokens()) {
                BuilderUtil.sendSysMessage(activeChar, "Syntax: //setparam <stat> <value>");
                return false;
            }
            final String statName = st2.nextToken();
            if (!st2.hasMoreTokens()) {
                BuilderUtil.sendSysMessage(activeChar, "Syntax: //setparam <stat> <value>");
                return false;
            }
            try {
                final Stat stat = Stat.valueOf(statName);
                final double value = Double.parseDouble(st2.nextToken());
                final Creature targetCreature = (Creature)target3;
                if (value >= 0.0) {
                    targetCreature.getStats().addFixedValue(stat, Double.valueOf(value));
                    targetCreature.getStats().recalculateStats(true);
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/stats/Stat;D)Ljava/lang/String;, stat, value));
                }
                else {
                    BuilderUtil.sendSysMessage(activeChar, "Non negative values are only allowed!");
                }
            }
            catch (Exception e5) {
                BuilderUtil.sendSysMessage(activeChar, "Couldn't find such stat!");
                BuilderUtil.sendSysMessage(activeChar, "Syntax: //setparam <stat> <value>");
                return false;
            }
        }
        else if (command.startsWith("admin_unsetparam")) {
            final WorldObject target3 = activeChar.getTarget();
            if (!GameUtils.isCreature(target3)) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                return false;
            }
            final StringTokenizer st2 = new StringTokenizer(command, " ");
            st2.nextToken();
            if (!st2.hasMoreTokens()) {
                BuilderUtil.sendSysMessage(activeChar, "Syntax: //unsetparam <stat>");
                return false;
            }
            final String statName = st2.nextToken();
            try {
                final Stat stat = Stat.valueOf(statName);
                final Creature targetCreature2 = (Creature)target3;
                targetCreature2.getStats().removeFixedValue(stat);
                targetCreature2.getStats().recalculateStats(true);
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/stats/Stat;)Ljava/lang/String;, stat));
            }
            catch (Exception e5) {
                BuilderUtil.sendSysMessage(activeChar, "Couldn't find such stat!");
                return false;
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminEditChar.ADMIN_COMMANDS;
    }
    
    private void listCharacters(final Player activeChar, final int page) {
        final List<Player> players = new ArrayList<Player>(World.getInstance().getPlayers());
        players.sort(Comparator.comparingLong(Player::getUptime));
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(activeChar, "data/html/admin/charlist.htm");
        final PageResult result = PageBuilder.newBuilder((Collection)players, 20, "bypass -h admin_show_characters").currentPage(page).bodyHandler((pages, player, sb) -> {
            sb.append("<tr>");
            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), player.getName()));
            sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, ClassListData.getInstance().getClass(player.getClassId()).getClientCode(), player.getLevel()));
            sb.append("</tr>");
        }).build();
        if (result.getPages() > 0) {
            html.replace("%pages%", invokedynamic(makeConcatWithConstants:(Ljava/lang/StringBuilder;)Ljava/lang/String;, result.getPagerTemplate()));
        }
        else {
            html.replace("%pages%", "");
        }
        html.replace("%players%", result.getBodyTemplate().toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    private void showCharacterInfo(final Player activeChar, Player player) {
        if (Objects.isNull(player)) {
            final WorldObject target = activeChar.getTarget();
            if (!GameUtils.isPlayer(target)) {
                return;
            }
            player = (Player)target;
        }
        else {
            activeChar.setTarget((WorldObject)player);
        }
        this.gatherCharacterInfo(activeChar, player, "charinfo.htm");
    }
    
    private void gatherCharacterInfo(final Player activeChar, final Player player, final String filename) {
        String ip = "N/A";
        if (Objects.isNull(player)) {
            BuilderUtil.sendSysMessage(activeChar, "Player is null.");
            return;
        }
        final GameClient client = player.getClient();
        if (Objects.isNull(client)) {
            BuilderUtil.sendSysMessage(activeChar, "Client is null.");
        }
        else {
            ip = client.getHostAddress();
        }
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        adminReply.setFile(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, filename));
        adminReply.replace("%name%", player.getName());
        adminReply.replace("%level%", String.valueOf(player.getLevel()));
        adminReply.replace("%clan%", String.valueOf((player.getClan() != null) ? invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, player.getObjectId(), player.getClan().getName()) : null));
        adminReply.replace("%xp%", String.valueOf(player.getExp()));
        adminReply.replace("%sp%", String.valueOf(player.getSp()));
        adminReply.replace("%class%", ClassListData.getInstance().getClass(player.getClassId()).getClientCode());
        adminReply.replace("%ordinal%", String.valueOf(player.getClassId().getId()));
        adminReply.replace("%classid%", String.valueOf(player.getClassId()));
        adminReply.replace("%baseclass%", ClassListData.getInstance().getClass(player.getBaseClass()).getClientCode());
        adminReply.replace("%x%", String.valueOf(player.getX()));
        adminReply.replace("%y%", String.valueOf(player.getY()));
        adminReply.replace("%z%", String.valueOf(player.getZ()));
        adminReply.replace("%heading%", String.valueOf(player.getHeading()));
        adminReply.replace("%currenthp%", String.valueOf((int)player.getCurrentHp()));
        adminReply.replace("%maxhp%", String.valueOf(player.getMaxHp()));
        adminReply.replace("%reputation%", String.valueOf(player.getReputation()));
        adminReply.replace("%currentmp%", String.valueOf((int)player.getCurrentMp()));
        adminReply.replace("%maxmp%", String.valueOf(player.getMaxMp()));
        adminReply.replace("%pvpflag%", String.valueOf(player.getPvpFlag()));
        adminReply.replace("%currentcp%", String.valueOf((int)player.getCurrentCp()));
        adminReply.replace("%maxcp%", String.valueOf(player.getMaxCp()));
        adminReply.replace("%pvpkills%", String.valueOf(player.getPvpKills()));
        adminReply.replace("%pkkills%", String.valueOf(player.getPkKills()));
        adminReply.replace("%currentload%", String.valueOf(player.getCurrentLoad()));
        adminReply.replace("%maxload%", String.valueOf(player.getMaxLoad()));
        adminReply.replace("%percent%", String.format("%.2f", player.getCurrentLoad() / (float)player.getMaxLoad() * 100.0f));
        adminReply.replace("%patk%", String.valueOf(player.getPAtk()));
        adminReply.replace("%matk%", String.valueOf(player.getMAtk()));
        adminReply.replace("%pdef%", String.valueOf(player.getPDef()));
        adminReply.replace("%mdef%", String.valueOf(player.getMDef()));
        adminReply.replace("%accuracy%", String.valueOf(player.getAccuracy()));
        adminReply.replace("%evasion%", String.valueOf(player.getEvasionRate()));
        adminReply.replace("%critical%", String.valueOf(player.getCriticalHit()));
        adminReply.replace("%runspeed%", String.valueOf(player.getRunSpeed()));
        adminReply.replace("%patkspd%", String.valueOf(player.getPAtkSpd()));
        adminReply.replace("%matkspd%", String.valueOf(player.getMAtkSpd()));
        adminReply.replace("%access%", invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, player.getAccessLevel().getLevel(), player.getAccessLevel().getName()));
        adminReply.replace("%account%", player.getAccountName());
        adminReply.replace("%ip%", ip);
        adminReply.replace("%hwid%", (player.getClient() != null && player.getClient().getHardwareInfo() != null) ? player.getClient().getHardwareInfo().getMacAddress() : "Unknown");
        adminReply.replace("%ai%", player.getAI().getIntention().name());
        adminReply.replace("%inst%", player.isInInstance() ? invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, player.getInstanceId(), player.getInstanceId()) : "");
        adminReply.replace("%noblesse%", player.isNoble() ? "Yes" : "No");
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void setTargetReputation(final Player activeChar, int newReputation) {
        final WorldObject target = activeChar.getTarget();
        Player player = null;
        if (GameUtils.isPlayer(target)) {
            player = (Player)target;
            if (newReputation > Config.MAX_REPUTATION) {
                newReputation = Config.MAX_REPUTATION;
            }
            final int oldReputation = player.getReputation();
            player.setReputation(newReputation);
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_REPUTATION_HAS_BEEN_CHANGED_TO_S1);
            sm.addInt(newReputation);
            player.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, player.getName(), oldReputation, newReputation));
        }
    }
    
    private void editCharacter(final Player activeChar, final String targetName) {
        WorldObject target = null;
        if (targetName != null) {
            target = (WorldObject)World.getInstance().findPlayer(targetName);
        }
        else {
            target = activeChar.getTarget();
        }
        if (target != null && GameUtils.isPlayer(target)) {
            final Player player = (Player)target;
            this.gatherCharacterInfo(activeChar, player, "charedit.htm");
        }
    }
    
    private void findCharacter(final Player activeChar, final String CharacterToFind) {
        int CharactersFound = 0;
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        adminReply.setFile(activeChar, "data/html/admin/charfind.htm");
        final StringBuilder replyMSG = new StringBuilder(1000);
        final List<Player> players = new ArrayList<Player>(World.getInstance().getPlayers());
        players.sort(Comparator.comparingLong(Player::getUptime));
        for (final Player player : players) {
            final String name = player.getName();
            if (name.toLowerCase().contains(CharacterToFind.toLowerCase())) {
                ++CharactersFound;
                replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_info ");
                replyMSG.append(name);
                replyMSG.append("\">");
                replyMSG.append(name);
                replyMSG.append("</a></td><td width=110>");
                replyMSG.append(ClassListData.getInstance().getClass(player.getClassId()).getClientCode());
                replyMSG.append("</td><td width=40>");
                replyMSG.append(player.getLevel());
                replyMSG.append("</td></tr>");
            }
            if (CharactersFound > 20) {
                break;
            }
        }
        adminReply.replace("%results%", replyMSG.toString());
        String replyMSG2;
        if (CharactersFound == 0) {
            replyMSG2 = "s. Please try again.";
        }
        else if (CharactersFound > 20) {
            adminReply.replace("%number%", " more than 20");
            replyMSG2 = "s.<br>Please refine your search to see all of the results.";
        }
        else if (CharactersFound == 1) {
            replyMSG2 = ".";
        }
        else {
            replyMSG2 = "s.";
        }
        adminReply.replace("%number%", String.valueOf(CharactersFound));
        adminReply.replace("%end%", replyMSG2);
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void findCharactersPerIp(final Player activeChar, final String IpAdress) throws IllegalArgumentException {
        boolean findDisconnected = false;
        if (IpAdress.equals("disconnected")) {
            findDisconnected = true;
        }
        else if (!IpAdress.matches("^(?:(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))\\.){3}(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))$")) {
            throw new IllegalArgumentException("Malformed IPv4 number");
        }
        int CharactersFound = 0;
        String ip = "0.0.0.0";
        final StringBuilder replyMSG = new StringBuilder(1000);
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        adminReply.setFile(activeChar, "data/html/admin/ipfind.htm");
        final List<Player> players = new ArrayList<Player>(World.getInstance().getPlayers());
        players.sort(Comparator.comparingLong(Player::getUptime));
        for (final Player player : players) {
            final GameClient client = player.getClient();
            if (client == null) {
                continue;
            }
            if (findDisconnected) {
                continue;
            }
            ip = client.getHostAddress();
            if (!ip.equals(IpAdress)) {
                continue;
            }
            final String name = player.getName();
            ++CharactersFound;
            replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_info ");
            replyMSG.append(name);
            replyMSG.append("\">");
            replyMSG.append(name);
            replyMSG.append("</a></td><td width=110>");
            replyMSG.append(ClassListData.getInstance().getClass(player.getClassId()).getClientCode());
            replyMSG.append("</td><td width=40>");
            replyMSG.append(player.getLevel());
            replyMSG.append("</td></tr>");
            if (CharactersFound > 20) {
                break;
            }
        }
        adminReply.replace("%results%", replyMSG.toString());
        String replyMSG2;
        if (CharactersFound == 0) {
            replyMSG2 = "s. Maybe they got d/c? :)";
        }
        else if (CharactersFound > 20) {
            adminReply.replace("%number%", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, CharactersFound));
            replyMSG2 = "s.<br>In order to avoid you a client crash I won't <br1>display results beyond the 20th character.";
        }
        else if (CharactersFound == 1) {
            replyMSG2 = ".";
        }
        else {
            replyMSG2 = "s.";
        }
        adminReply.replace("%ip%", IpAdress);
        adminReply.replace("%number%", String.valueOf(CharactersFound));
        adminReply.replace("%end%", replyMSG2);
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void findCharactersPerAccount(final Player activeChar, final String characterName) throws IllegalArgumentException {
        final Player player = World.getInstance().findPlayer(characterName);
        if (player == null) {
            throw new IllegalArgumentException("Player doesn't exist");
        }
        final IntMap<String> chars = (IntMap<String>)player.getAccountChars();
        final StringJoiner replyMSG = new StringJoiner("<br1>");
        final Collection values = chars.values();
        final StringJoiner obj = replyMSG;
        Objects.requireNonNull(obj);
        values.forEach(obj::add);
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        adminReply.setFile(activeChar, "data/html/admin/accountinfo.htm");
        adminReply.replace("%account%", player.getAccountName());
        adminReply.replace("%player%", characterName);
        adminReply.replace("%characters%", replyMSG.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void findDualbox(final Player activeChar, final int multibox) {
        final Map<String, List<Player>> ipMap = new HashMap<String, List<Player>>();
        String ip = "0.0.0.0";
        final Map<String, Integer> dualboxIPs = new HashMap<String, Integer>();
        final List<Player> players = new ArrayList<Player>(World.getInstance().getPlayers());
        players.sort(Comparator.comparingLong(Player::getUptime));
        for (final Player player : players) {
            final GameClient client = player.getClient();
            if (client == null) {
                continue;
            }
            ip = client.getHostAddress();
            if (ipMap.get(ip) == null) {
                ipMap.put(ip, new ArrayList<Player>());
            }
            ipMap.get(ip).add(player);
            if (ipMap.get(ip).size() < multibox) {
                continue;
            }
            final Integer count = dualboxIPs.get(ip);
            if (count == null) {
                dualboxIPs.put(ip, multibox);
            }
            else {
                dualboxIPs.put(ip, count + 1);
            }
        }
        final List<String> keys = new ArrayList<String>(dualboxIPs.keySet());
        keys.sort(Comparator.comparing(s -> dualboxIPs.get(s)).reversed());
        final StringBuilder results = new StringBuilder();
        for (final String dualboxIP : keys) {
            results.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;, dualboxIP, dualboxIP, dualboxIPs.get(dualboxIP)));
        }
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        adminReply.setFile(activeChar, "data/html/admin/dualbox.htm");
        adminReply.replace("%multibox%", String.valueOf(multibox));
        adminReply.replace("%results%", results.toString());
        adminReply.replace("%strict%", "");
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void findDualboxStrict(final Player activeChar, final int multibox) {
        final Map<IpPack, List<Player>> ipMap = new HashMap<IpPack, List<Player>>();
        final Map<IpPack, Integer> dualboxIPs = new HashMap<IpPack, Integer>();
        final List<Player> players = new ArrayList<Player>(World.getInstance().getPlayers());
        players.sort(Comparator.comparingLong(Player::getUptime));
        for (final Player player : players) {
            final GameClient client = player.getClient();
            if (client == null) {
                continue;
            }
            final IpPack pack = new IpPack(client.getHostAddress(), client.getTrace());
            if (ipMap.get(pack) == null) {
                ipMap.put(pack, new ArrayList<Player>());
            }
            ipMap.get(pack).add(player);
            if (ipMap.get(pack).size() < multibox) {
                continue;
            }
            final Integer count = dualboxIPs.get(pack);
            if (count == null) {
                dualboxIPs.put(pack, multibox);
            }
            else {
                dualboxIPs.put(pack, count + 1);
            }
        }
        final List<IpPack> keys = new ArrayList<IpPack>(dualboxIPs.keySet());
        keys.sort(Comparator.comparing(s -> dualboxIPs.get(s)).reversed());
        final StringBuilder results = new StringBuilder();
        for (final IpPack dualboxIP : keys) {
            results.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/String;, dualboxIP.ip, dualboxIP.ip, dualboxIPs.get(dualboxIP)));
        }
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        adminReply.setFile(activeChar, "data/html/admin/dualbox.htm");
        adminReply.replace("%multibox%", String.valueOf(multibox));
        adminReply.replace("%results%", results.toString());
        adminReply.replace("%strict%", "strict_");
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void gatherSummonInfo(final Summon target, final Player activeChar) {
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(activeChar, "data/html/admin/petinfo.htm");
        final String name = target.getName();
        html.replace("%name%", (name == null) ? "N/A" : name);
        html.replace("%level%", Integer.toString(target.getLevel()));
        html.replace("%exp%", Long.toString(target.getStats().getExp()));
        final String owner = target.getActingPlayer().getName();
        html.replace("%owner%", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, owner, owner));
        html.replace("%class%", target.getClass().getSimpleName());
        html.replace("%ai%", target.hasAI() ? target.getAI().getIntention().name() : "NULL");
        html.replace("%hp%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, (int)target.getStatus().getCurrentHp(), target.getStats().getMaxHp()));
        html.replace("%mp%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, (int)target.getStatus().getCurrentMp(), target.getStats().getMaxMp()));
        html.replace("%karma%", Integer.toString(target.getReputation()));
        html.replace("%race%", target.getTemplate().getRace().toString());
        if (GameUtils.isPet((WorldObject)target)) {
            final int objId = target.getActingPlayer().getObjectId();
            html.replace("%inv%", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, objId));
        }
        else {
            html.replace("%inv%", "none");
        }
        if (GameUtils.isPet((WorldObject)target)) {
            html.replace("%food%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, ((Pet)target).getCurrentFed(), ((Pet)target).getPetLevelData().getPetMaxFeed()));
            html.replace("%load%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, target.getInventory().getTotalWeight(), target.getMaxLoad()));
        }
        else {
            html.replace("%food%", "N/A");
            html.replace("%load%", "N/A");
        }
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    private void gatherPartyInfo(final Player target, final Player activeChar) {
        boolean color = true;
        final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
        html.setFile(activeChar, "data/html/admin/partyinfo.htm");
        final StringBuilder text = new StringBuilder(400);
        for (final Player member : target.getParty().getMembers()) {
            if (color) {
                text.append("<tr><td><table width=270 border=0 bgcolor=131210 cellpadding=2><tr><td width=30 align=right>");
            }
            else {
                text.append("<tr><td><table width=270 border=0 cellpadding=2><tr><td width=30 align=right>");
            }
            text.append(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;Ljava/lang/String;)Ljava/lang/String;, member.getLevel(), member.getName(), member.getName()));
            text.append(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/base/ClassId;)Ljava/lang/String;, member.getClassId()));
            color = !color;
        }
        html.replace("%player%", target.getName());
        html.replace("%party%", text.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminEditChar.class);
        ADMIN_COMMANDS = new String[] { "admin_edit_character", "admin_current_player", "admin_setreputation", "admin_nokarma", "admin_setfame", "admin_character_list", "admin_character_info", "admin_show_characters", "admin_find_character", "admin_find_ip", "admin_find_account", "admin_find_dualbox", "admin_strict_find_dualbox", "admin_tracert", "admin_rec", "admin_settitle", "admin_changename", "admin_setsex", "admin_setcolor", "admin_settcolor", "admin_setclass", "admin_setpk", "admin_setpvp", "admin_set_pvp_flag", "admin_fullfood", "admin_remove_clan_penalty", "admin_summon_info", "admin_unsummon", "admin_summon_setlvl", "admin_show_pet_inv", "admin_partyinfo", "admin_setnoble", "admin_set_hp", "admin_set_mp", "admin_set_cp", "admin_setparam", "admin_unsetparam" };
    }
    
    private final class IpPack
    {
        String ip;
        int[][] tracert;
        
        public IpPack(final String ip, final int[][] tracert) {
            this.ip = ip;
            this.tracert = tracert;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.ip == null) ? 0 : this.ip.hashCode());
            for (final int[] array : this.tracert) {
                result = 31 * result + Arrays.hashCode(array);
            }
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final IpPack other = (IpPack)obj;
            if (!this.getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (this.ip == null) {
                if (other.ip != null) {
                    return false;
                }
            }
            else if (!this.ip.equals(other.ip)) {
                return false;
            }
            for (int i = 0; i < this.tracert.length; ++i) {
                for (int o = 0; o < this.tracert[0].length; ++o) {
                    if (this.tracert[i][o] != other.tracert[i][o]) {
                        return false;
                    }
                }
            }
            return true;
        }
        
        private AdminEditChar getOuterType() {
            return AdminEditChar.this;
        }
    }
}
