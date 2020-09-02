// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.WorldObject;
import java.util.stream.Stream;
import org.l2j.gameserver.network.serverpackets.ExWorldChatCnt;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.settings.ChatSettings;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.Config;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.RateSettings;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.olympiad.OlympiadEngine;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminAdmin implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_admin")) {
            this.showMainPage(activeChar, command);
        }
        else if (command.equals("admin_config_server")) {
            this.showConfigPage(activeChar);
        }
        else if (command.startsWith("admin_gmliston")) {
            AdminData.getInstance().showGm(activeChar);
            BuilderUtil.sendSysMessage(activeChar, "Registered into gm list.");
            AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
        }
        else if (command.startsWith("admin_gmlistoff")) {
            AdminData.getInstance().hideGm(activeChar);
            BuilderUtil.sendSysMessage(activeChar, "Removed from gm list.");
            AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
        }
        else if (command.startsWith("admin_silence")) {
            if (activeChar.isSilenceMode()) {
                activeChar.setSilenceMode(false);
                activeChar.sendPacket(SystemMessageId.MESSAGE_ACCEPTANCE_MODE);
            }
            else {
                activeChar.setSilenceMode(true);
                activeChar.sendPacket(SystemMessageId.MESSAGE_REFUSAL_MODE);
            }
            AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
        }
        else if (command.startsWith("admin_saveolymp")) {
            OlympiadEngine.getInstance().saveOlympiadStatus();
            BuilderUtil.sendSysMessage(activeChar, "olympiad system saved.");
        }
        else if (command.startsWith("admin_sethero")) {
            if (activeChar.getTarget() == null) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                return false;
            }
            final Player target = GameUtils.isPlayer(activeChar.getTarget()) ? activeChar.getTarget().getActingPlayer() : activeChar;
            target.setHero(!target.isHero());
            target.broadcastUserInfo();
        }
        else if (command.startsWith("admin_givehero")) {
            if (activeChar.getTarget() == null) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                return false;
            }
            final Player target = GameUtils.isPlayer(activeChar.getTarget()) ? activeChar.getTarget().getActingPlayer() : activeChar;
            if (Hero.getInstance().isHero(target.getObjectId())) {
                BuilderUtil.sendSysMessage(activeChar, "This player has already claimed the hero status.");
                return false;
            }
            if (!Hero.getInstance().isUnclaimedHero(target.getObjectId())) {
                BuilderUtil.sendSysMessage(activeChar, "This player cannot claim the hero status.");
                return false;
            }
            Hero.getInstance().claimHero(target);
        }
        else if (command.startsWith("admin_diet")) {
            try {
                final StringTokenizer st = new StringTokenizer(command);
                st.nextToken();
                if (st.nextToken().equalsIgnoreCase("on")) {
                    activeChar.setDietMode(true);
                    BuilderUtil.sendSysMessage(activeChar, "Diet mode on.");
                }
                else if (st.nextToken().equalsIgnoreCase("off")) {
                    activeChar.setDietMode(false);
                    BuilderUtil.sendSysMessage(activeChar, "Diet mode off.");
                }
            }
            catch (Exception ex) {
                if (activeChar.getDietMode()) {
                    activeChar.setDietMode(false);
                    BuilderUtil.sendSysMessage(activeChar, "Diet mode off.");
                }
                else {
                    activeChar.setDietMode(true);
                    BuilderUtil.sendSysMessage(activeChar, "Diet mode on.");
                }
            }
            finally {
                activeChar.refreshOverloaded(true);
            }
            AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
        }
        else if (command.startsWith("admin_tradeoff")) {
            try {
                final String mode = command.substring(15);
                if (mode.equalsIgnoreCase("on")) {
                    activeChar.setTradeRefusing(true);
                    BuilderUtil.sendSysMessage(activeChar, "Trade refusal enabled.");
                }
                else if (mode.equalsIgnoreCase("off")) {
                    activeChar.setTradeRefusing(false);
                    BuilderUtil.sendSysMessage(activeChar, "Trade refusal disabled.");
                }
            }
            catch (Exception ex) {
                if (activeChar.isTradeRefusing()) {
                    activeChar.setTradeRefusing(false);
                    BuilderUtil.sendSysMessage(activeChar, "Trade refusal disabled.");
                }
                else {
                    activeChar.setTradeRefusing(true);
                    BuilderUtil.sendSysMessage(activeChar, "Trade refusal enabled.");
                }
            }
            AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
        }
        else if (command.startsWith("admin_setconfig")) {
            final StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
            try {
                final String pName = st.nextToken();
                final String pValue = st.nextToken();
                if (Float.valueOf(pValue) == null) {
                    BuilderUtil.sendSysMessage(activeChar, "Invalid parameter!");
                    return false;
                }
                final String s = pName;
                switch (s) {
                    case "RateXp": {
                        ((RateSettings)Configurator.getSettings((Class)RateSettings.class)).setXp(Float.parseFloat(pValue));
                        break;
                    }
                    case "RateSp": {
                        Config.RATE_SP = Float.valueOf(pValue);
                        break;
                    }
                    case "RateDropSpoil": {
                        Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER = Float.valueOf(pValue);
                        break;
                    }
                    case "EnchantChanceElementStone": {
                        Config.ENCHANT_CHANCE_ELEMENT_STONE = Float.valueOf(pValue);
                        break;
                    }
                    case "EnchantChanceElementCrystal": {
                        Config.ENCHANT_CHANCE_ELEMENT_CRYSTAL = Float.valueOf(pValue);
                        break;
                    }
                    case "EnchantChanceElementJewel": {
                        Config.ENCHANT_CHANCE_ELEMENT_JEWEL = Float.valueOf(pValue);
                        break;
                    }
                    case "EnchantChanceElementEnergy": {
                        Config.ENCHANT_CHANCE_ELEMENT_ENERGY = Float.valueOf(pValue);
                        break;
                    }
                }
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, pName, pValue));
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(activeChar, "Usage: //setconfig <parameter> <value>");
            }
            finally {
                this.showConfigPage(activeChar);
            }
        }
        else if (command.startsWith("admin_worldchat")) {
            final StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
            final String s2 = st.hasMoreTokens() ? st.nextToken() : "";
            switch (s2) {
                case "shout": {
                    final StringBuilder sb = new StringBuilder();
                    while (st.hasMoreTokens()) {
                        sb.append(st.nextToken());
                        sb.append(" ");
                    }
                    final CreatureSay cs = new CreatureSay(activeChar, ChatType.WORLD, sb.toString());
                    final Stream stream = World.getInstance().getPlayers().stream();
                    Objects.requireNonNull(activeChar);
                    final Stream filter = stream.filter(activeChar::isNotBlocked);
                    final CreatureSay obj = cs;
                    Objects.requireNonNull(obj);
                    filter.forEach(obj::sendTo);
                    break;
                }
                case "see": {
                    final WorldObject target2 = activeChar.getTarget();
                    if (!GameUtils.isPlayer(target2)) {
                        activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                        break;
                    }
                    final Player targetPlayer = target2.getActingPlayer();
                    final int worldChatMinLevel = ((ChatSettings)Configurator.getSettings((Class)ChatSettings.class)).worldChatMinLevel();
                    if (targetPlayer.getLevel() < worldChatMinLevel) {
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, worldChatMinLevel));
                        break;
                    }
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, targetPlayer.getName(), targetPlayer.getWorldChatUsed(), targetPlayer.getWorldChatPoints()));
                    break;
                }
                case "set": {
                    final WorldObject target2 = activeChar.getTarget();
                    if (!GameUtils.isPlayer(target2)) {
                        activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                        break;
                    }
                    final Player targetPlayer = target2.getActingPlayer();
                    final int worldChatMinLevel = ((ChatSettings)Configurator.getSettings((Class)ChatSettings.class)).worldChatMinLevel();
                    if (targetPlayer.getLevel() < worldChatMinLevel) {
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, worldChatMinLevel));
                        break;
                    }
                    if (!st.hasMoreTokens()) {
                        BuilderUtil.sendSysMessage(activeChar, "Incorrect syntax, use: //worldchat set <times used>");
                        break;
                    }
                    final String valueToken = st.nextToken();
                    if (!Util.isDigit(valueToken)) {
                        BuilderUtil.sendSysMessage(activeChar, "Incorrect syntax, use: //worldchat set <times used>");
                        break;
                    }
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, targetPlayer.getName(), targetPlayer.getWorldChatPoints(), valueToken));
                    targetPlayer.setWorldChatUsed(Integer.parseInt(valueToken));
                    if (((ChatSettings)Configurator.getSettings((Class)ChatSettings.class)).worldChatEnabled()) {
                        targetPlayer.sendPacket(new ServerPacket[] { (ServerPacket)new ExWorldChatCnt(targetPlayer) });
                        break;
                    }
                    break;
                }
                default: {
                    BuilderUtil.sendSysMessage(activeChar, "Possible commands:");
                    BuilderUtil.sendSysMessage(activeChar, " - Send message: //worldchat shout <text>");
                    BuilderUtil.sendSysMessage(activeChar, " - See your target's points: //worldchat see");
                    BuilderUtil.sendSysMessage(activeChar, " - Change your target's points: //worldchat set <points>");
                    break;
                }
            }
        }
        else if (command.startsWith("admin_gmon")) {}
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminAdmin.ADMIN_COMMANDS;
    }
    
    private void showMainPage(final Player activeChar, final String command) {
        int mode = 0;
        String filename = null;
        try {
            mode = Integer.parseInt(command.substring(11));
        }
        catch (Exception ex) {}
        switch (mode) {
            case 1: {
                filename = "main";
                break;
            }
            case 2: {
                filename = "game";
                break;
            }
            case 3: {
                filename = "effects";
                break;
            }
            case 4: {
                filename = "server";
                break;
            }
            case 5: {
                filename = "mods";
                break;
            }
            case 6: {
                filename = "char";
                break;
            }
            case 7: {
                filename = "gm";
                break;
            }
            default: {
                filename = "main";
                break;
            }
        }
        AdminHtml.showAdminHtml(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, filename));
    }
    
    private void showConfigPage(final Player activeChar) {
        final NpcHtmlMessage adminReply = new NpcHtmlMessage();
        final StringBuilder replyMSG = new StringBuilder("<html><title>L2J :: Config</title><body>");
        replyMSG.append("<center><table width=270><tr><td width=60><button value=\"Main\" action=\"bypass -h admin_admin\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=150>Config Server Panel</td><td width=60><button value=\"Back\" action=\"bypass -h admin_admin4\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table></center><br>");
        replyMSG.append("<center><table width=260><tr><td width=140></td><td width=40></td><td width=40></td></tr>");
        replyMSG.append("<tr><td><font color=\"00AA00\">Drop:</font></td><td></td><td></td></tr>");
        replyMSG.append("<tr><td><font color=\"LEVEL\">Rate EXP</font> = ").append(((RateSettings)Configurator.getSettings((Class)RateSettings.class)).xp()).append("</td><td><edit var=\"param1\" width=40 height=15></td><td><button value=\"Set\" action=\"bypass -h admin_setconfig RateXp $param1\" width=40 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
        replyMSG.append(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, Config.RATE_SP));
        replyMSG.append(invokedynamic(makeConcatWithConstants:(F)Ljava/lang/String;, Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER));
        replyMSG.append("<tr><td width=140></td><td width=40></td><td width=40></td></tr>");
        replyMSG.append("<tr><td><font color=\"00AA00\">Enchant:</font></td><td></td><td></td></tr>");
        replyMSG.append(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, Config.ENCHANT_CHANCE_ELEMENT_STONE));
        replyMSG.append(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, Config.ENCHANT_CHANCE_ELEMENT_CRYSTAL));
        replyMSG.append(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, Config.ENCHANT_CHANCE_ELEMENT_JEWEL));
        replyMSG.append(invokedynamic(makeConcatWithConstants:(D)Ljava/lang/String;, Config.ENCHANT_CHANCE_ELEMENT_ENERGY));
        replyMSG.append("</table></body></html>");
        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminAdmin.class);
        ADMIN_COMMANDS = new String[] { "admin_admin", "admin_admin1", "admin_admin2", "admin_admin3", "admin_admin4", "admin_admin5", "admin_admin6", "admin_admin7", "admin_gmliston", "admin_gmlistoff", "admin_silence", "admin_diet", "admin_tradeoff", "admin_set", "admin_set_mod", "admin_saveolymp", "admin_sethero", "admin_givehero", "admin_setconfig", "admin_config_server", "admin_gmon", "admin_worldchat" };
    }
}
