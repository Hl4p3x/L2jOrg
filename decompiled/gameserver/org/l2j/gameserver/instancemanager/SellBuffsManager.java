// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Iterator;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.util.HtmlUtil;
import org.l2j.gameserver.model.holders.SellBuffHolder;
import java.util.ArrayList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreSetWholeMsg;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.actor.instance.Player;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.gameserver.Config;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class SellBuffsManager extends GameXmlReader
{
    private static final Logger LOGGER;
    private static final List<Integer> ALLOWED_BUFFS;
    private static final String htmlFolder = "data/html/mods/SellBuffs/";
    
    private SellBuffsManager() {
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/SellBuffData.xsd");
    }
    
    public void load() {
        if (Config.SELLBUFF_ENABLED) {
            SellBuffsManager.ALLOWED_BUFFS.clear();
            this.parseDatapackFile("data/SellBuffData.xml");
            SellBuffsManager.LOGGER.info("Loaded {} allowed buffs.", (Object)SellBuffsManager.ALLOWED_BUFFS.size());
        }
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final NodeList node = doc.getDocumentElement().getElementsByTagName("skill");
        for (int i = 0; i < node.getLength(); ++i) {
            final Element elem = (Element)node.item(i);
            final int skillId = Integer.parseInt(elem.getAttribute("id"));
            if (!SellBuffsManager.ALLOWED_BUFFS.contains(skillId)) {
                SellBuffsManager.ALLOWED_BUFFS.add(skillId);
            }
        }
    }
    
    public void sendSellMenu(final Player player) {
        final String html = HtmCache.getInstance().getHtm(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.isSellingBuffs() ? "BuffMenu_already.html" : "BuffMenu.html"));
        CommunityBoardHandler.separateAndSend(html, player);
    }
    
    public void sendBuffChoiceMenu(final Player player, final int index) {
        String html = HtmCache.getInstance().getHtm(player, "data/html/mods/SellBuffs/BuffChoice.html");
        html = html.replace("%list%", this.buildSkillMenu(player, index));
        CommunityBoardHandler.separateAndSend(html, player);
    }
    
    public void sendBuffEditMenu(final Player player) {
        String html = HtmCache.getInstance().getHtm(player, "data/html/mods/SellBuffs/BuffChoice.html");
        html = html.replace("%list%", this.buildEditMenu(player));
        CommunityBoardHandler.separateAndSend(html, player);
    }
    
    public void sendBuffMenu(final Player player, final Player seller, final int index) {
        if (!seller.isSellingBuffs() || seller.getSellingBuffs().isEmpty()) {
            return;
        }
        String html = HtmCache.getInstance().getHtm(player, "data/html/mods/SellBuffs/BuffBuyMenu.html");
        html = html.replace("%list%", this.buildBuffMenu(player, seller, index));
        CommunityBoardHandler.separateAndSend(html, player);
    }
    
    public void startSellBuffs(final Player player, final String title) {
        player.sitDown();
        player.setIsSellingBuffs(true);
        player.setPrivateStoreType(PrivateStoreType.PACKAGE_SELL);
        player.getSellList().setTitle(title);
        player.getSellList().setPackaged(true);
        player.broadcastUserInfo();
        player.broadcastPacket(new ExPrivateStoreSetWholeMsg(player));
        this.sendSellMenu(player);
    }
    
    public void stopSellBuffs(final Player player) {
        player.setIsSellingBuffs(false);
        player.setPrivateStoreType(PrivateStoreType.NONE);
        player.standUp();
        player.broadcastUserInfo();
        this.sendSellMenu(player);
    }
    
    private String buildBuffMenu(final Player player, final Player seller, final int index) {
        final int ceiling = 10;
        int nextIndex = -1;
        int previousIndex = -1;
        int emptyFields = 0;
        final StringBuilder sb = new StringBuilder();
        final List<SellBuffHolder> sellList = new ArrayList<SellBuffHolder>();
        int count = 0;
        for (final SellBuffHolder holder : seller.getSellingBuffs()) {
            if (++count > index && count <= 10 + index) {
                sellList.add(holder);
            }
        }
        if (count > 10 && count > index + 10) {
            nextIndex = index + 10;
        }
        if (index >= 10) {
            previousIndex = index - 10;
        }
        emptyFields = 10 - sellList.size();
        sb.append("<br>");
        sb.append(HtmlUtil.getMpGauge(250, (long)seller.getCurrentMp(), seller.getMaxMp(), false));
        sb.append("<br>");
        sb.append("<table border=0 cellpadding=0 cellspacing=0 background=\"L2UI_CH3.refinewnd_back_Pattern\">");
        sb.append("<tr><td><br><br><br></td></tr>");
        sb.append("<tr>");
        sb.append("<td fixwidth=\"10\"></td>");
        sb.append("<td> <button action=\"\" value=\"Icon\" width=75 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Name\" width=175 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Level\" width=85 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"MP Cost\" width=100 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Price\" width=200 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Action\" width=100 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td fixwidth=\"20\"></td>");
        sb.append("</tr>");
        for (final SellBuffHolder holder : sellList) {
            final Skill skill = seller.getKnownSkill(holder.getSkillId());
            if (skill == null) {
                ++emptyFields;
            }
            else {
                final ItemTemplate item = ItemEngine.getInstance().getTemplate(Config.SELLBUFF_PAYMENT_ID);
                sb.append("<tr>");
                sb.append("<td fixwidth=\"20\"></td>");
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skill.getIcon()));
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, skill.getName(), (skill.getLevel() > 100) ? invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, skill.getLevel() % 100) : "</td>"));
                sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, (skill.getLevel() > 100) ? SkillEngine.getInstance().getMaxLevel(skill.getId()) : skill.getLevel()));
                sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, skill.getMpConsume() * Config.SELLBUFF_MP_MULTIPLER));
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, GameUtils.formatAdena(holder.getPrice()), (item != null) ? item.getName() : ""));
                sb.append(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, seller.getObjectId(), skill.getId(), index));
                sb.append("</tr>");
                sb.append("<tr><td><br><br></td></tr>");
            }
        }
        for (int i = 0; i < emptyFields; ++i) {
            sb.append("<tr>");
            sb.append("<td fixwidth=\"20\" height=\"32\"></td>");
            sb.append("<td align=center></td>");
            sb.append("<td align=left></td>");
            sb.append("<td align=center></td>");
            sb.append("<td align=center></font></td>");
            sb.append("<td align=center></td>");
            sb.append("<td align=center fixwidth=\"50\"></td>");
            sb.append("</tr>");
            sb.append("<tr><td><br><br></td></tr>");
        }
        sb.append("</table>");
        sb.append("<table width=\"250\" border=\"0\">");
        sb.append("<tr>");
        if (previousIndex > -1) {
            sb.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, seller.getObjectId(), previousIndex));
        }
        if (nextIndex > -1) {
            sb.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, seller.getObjectId(), nextIndex));
        }
        sb.append("</tr>");
        sb.append("</table>");
        return sb.toString();
    }
    
    private String buildEditMenu(final Player player) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<table border=0 cellpadding=0 cellspacing=0 background=\"L2UI_CH3.refinewnd_back_Pattern\">");
        sb.append("<tr><td><br><br><br></td></tr>");
        sb.append("<tr>");
        sb.append("<td fixwidth=\"10\"></td>");
        sb.append("<td> <button action=\"\" value=\"Icon\" width=75 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Name\" width=150 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Level\" width=75 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Old Price\" width=100 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"New Price\" width=125 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Action\" width=125 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Remove\" width=85 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td fixwidth=\"20\"></td>");
        sb.append("</tr>");
        if (player.getSellingBuffs().isEmpty()) {
            sb.append("</table>");
            sb.append("<br><br><br>");
            sb.append("You don't have added any buffs yet!");
        }
        else {
            for (final SellBuffHolder holder : player.getSellingBuffs()) {
                final Skill skill = player.getKnownSkill(holder.getSkillId());
                if (skill == null) {
                    continue;
                }
                sb.append("<tr>");
                sb.append("<td fixwidth=\"20\"></td>");
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skill.getIcon()));
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, skill.getName(), (skill.getLevel() > 100) ? invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, skill.getLevel() % 100) : "</td>"));
                sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, (skill.getLevel() > 100) ? SkillEngine.getInstance().getMaxLevel(skill.getId()) : skill.getLevel()));
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, GameUtils.formatAdena(holder.getPrice())));
                sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, skill.getId()));
                sb.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, skill.getId(), skill.getId()));
                sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, skill.getId()));
                sb.append("</tr>");
                sb.append("<tr><td><br><br></td></tr>");
            }
            sb.append("</table>");
        }
        return sb.toString();
    }
    
    private String buildSkillMenu(final Player player, final int index) {
        final int ceiling = index + 10;
        int nextIndex = -1;
        int previousIndex = -1;
        final StringBuilder sb = new StringBuilder();
        final List<Skill> skillList = new ArrayList<Skill>();
        int count = 0;
        for (final Skill skill : player.getAllSkills()) {
            if (SellBuffsManager.ALLOWED_BUFFS.contains(skill.getId()) && !this.isInSellList(player, skill) && ++count > index && count <= ceiling) {
                skillList.add(skill);
            }
        }
        if (count > 10 && count > index + 10) {
            nextIndex = index + 10;
        }
        if (index >= 10) {
            previousIndex = index - 10;
        }
        sb.append("<table border=0 cellpadding=0 cellspacing=0 background=\"L2UI_CH3.refinewnd_back_Pattern\">");
        sb.append("<tr><td><br><br><br></td></tr>");
        sb.append("<tr>");
        sb.append("<td fixwidth=\"10\"></td>");
        sb.append("<td> <button action=\"\" value=\"Icon\" width=100 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Name\" width=175 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Level\" width=150 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Price\" width=150 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td> <button action=\"\" value=\"Action\" width=125 height=23 back=\"L2UI_CT1.OlympiadWnd_DF_Watch_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Watch\"> </td>");
        sb.append("<td fixwidth=\"20\"></td>");
        sb.append("</tr>");
        if (skillList.isEmpty()) {
            sb.append("</table>");
            sb.append("<br><br><br>");
            sb.append("At this moment you cant add any buffs!");
        }
        else {
            for (final Skill skill : skillList) {
                sb.append("<tr>");
                sb.append("<td fixwidth=\"20\"></td>");
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skill.getIcon()));
                sb.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, skill.getName(), (skill.getLevel() > 100) ? invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, skill.getLevel() % 100) : "</td>"));
                sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, (skill.getLevel() > 100) ? SkillEngine.getInstance().getMaxLevel(skill.getId()) : skill.getLevel()));
                sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, skill.getId()));
                sb.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, skill.getId(), skill.getId()));
                sb.append("</tr>");
                sb.append("<tr><td><br><br></td></tr>");
            }
            sb.append("</table>");
        }
        sb.append("<table width=\"250\" border=\"0\">");
        sb.append("<tr>");
        if (previousIndex > -1) {
            sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, previousIndex));
        }
        if (nextIndex > -1) {
            sb.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, nextIndex));
        }
        sb.append("</tr>");
        sb.append("</table>");
        return sb.toString();
    }
    
    public boolean isInSellList(final Player player, final Skill skill) {
        return player.getSellingBuffs().stream().filter(h -> h.getSkillId() == skill.getId()).findFirst().orElse(null) != null;
    }
    
    public boolean canStartSellBuffs(final Player player) {
        if (player.isAlikeDead()) {
            player.sendMessage("You can't sell buffs in fake death!");
            return false;
        }
        if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player)) {
            player.sendMessage("You can't sell buffs with Olympiad status!");
            return false;
        }
        if (player.isOnEvent()) {
            player.sendMessage("You can't sell buffs while registered in an event!");
            return false;
        }
        if (player.getReputation() < 0) {
            player.sendMessage("You can't sell buffs in Chaotic state!");
            return false;
        }
        if (player.isInDuel()) {
            player.sendMessage("You can't sell buffs in Duel state!");
            return false;
        }
        if (player.isFishing()) {
            player.sendMessage("You can't sell buffs while fishing.");
            return false;
        }
        if (player.isMounted() || player.isFlyingMounted() || player.isFlying()) {
            player.sendMessage("You can't sell buffs in Mount state!");
            return false;
        }
        if (player.isTransformed()) {
            player.sendMessage("You can't sell buffs in Transform state!");
            return false;
        }
        if (player.isInsideZone(ZoneType.NO_STORE) || !player.isInsideZone(ZoneType.PEACE) || player.isJailed()) {
            player.sendMessage("You can't sell buffs here!");
            return false;
        }
        return true;
    }
    
    public static SellBuffsManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SellBuffsManager.class);
        ALLOWED_BUFFS = new ArrayList<Integer>();
    }
    
    private static class Singleton
    {
        private static final SellBuffsManager INSTANCE;
        
        static {
            INSTANCE = new SellBuffsManager();
        }
    }
}
