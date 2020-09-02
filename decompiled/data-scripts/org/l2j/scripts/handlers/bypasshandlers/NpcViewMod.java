// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.AggroInfo;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.ItemEngine;
import java.text.DecimalFormat;
import org.l2j.gameserver.model.holders.DropHolder;
import java.util.List;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.commons.util.CommonUtil;
import java.util.concurrent.TimeUnit;
import java.util.Objects;
import org.l2j.gameserver.util.HtmlUtil;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.enums.DropType;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.commons.util.Util;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IBypassHandler;

public class NpcViewMod implements IBypassHandler
{
    private static final Logger LOGGER;
    private static final String[] COMMANDS;
    private static final int DROP_LIST_ITEMS_PER_PAGE = 8;
    
    public boolean useBypass(final String command, final Player player, final Creature bypassOrigin) {
        final StringTokenizer st = new StringTokenizer(command);
        st.nextToken();
        if (!st.hasMoreTokens()) {
            NpcViewMod.LOGGER.warn("Bypass[NpcViewMod] used without enough parameters.");
            return false;
        }
        final String actualCommand = st.nextToken();
        final String lowerCase = actualCommand.toLowerCase();
        switch (lowerCase) {
            case "view": {
                final int objectId = Util.parseNextInt(st, -1);
                final WorldObject target = (objectId > 0) ? World.getInstance().findObject(objectId) : player.getTarget();
                GameUtils.doIfIsNpc(target, npc -> this.sendNpcView(player, npc));
                break;
            }
            case "droplist": {
                if (st.countTokens() < 2) {
                    NpcViewMod.LOGGER.warn("Bypass[NpcViewMod] used without enough parameters.");
                    return false;
                }
                final String dropListTypeString = st.nextToken();
                try {
                    final DropType dropListType = Enum.valueOf(DropType.class, dropListTypeString);
                    final WorldObject object;
                    final WorldObject target2 = object = World.getInstance().findObject(Integer.parseInt(st.nextToken()));
                    final Npc npc2;
                    if (!(object instanceof Npc) || (npc2 = (Npc)object) != (Npc)object) {
                        return false;
                    }
                    this.sendNpcDropList(player, npc2, dropListType, Util.parseNextInt(st, 0));
                    break;
                }
                catch (IllegalArgumentException e) {
                    NpcViewMod.LOGGER.warn("Bypass[NpcViewMod] unknown drop list scope: {}", (Object)dropListTypeString);
                    return false;
                }
            }
            case "skills": {
                final int objectId = Util.parseNextInt(st, -1);
                final WorldObject target = (objectId > 0) ? World.getInstance().findObject(objectId) : player.getTarget();
                GameUtils.doIfIsNpc(target, npc -> this.sendNpcSkillView(player, npc));
                break;
            }
            case "aggrolist": {
                final int objectId = Util.parseNextInt(st, -1);
                final WorldObject target = (objectId > 0) ? World.getInstance().findObject(objectId) : player.getTarget();
                GameUtils.doIfIsNpc(target, npc -> sendAggroListView(player, npc));
                break;
            }
        }
        return true;
    }
    
    public String[] getBypassList() {
        return NpcViewMod.COMMANDS;
    }
    
    private void sendNpcView(final Player activeChar, final Npc npc) {
        final NpcHtmlMessage html = new NpcHtmlMessage();
        html.setFile(activeChar, "data/html/mods/NpcView/Info.htm");
        html.replace("%name%", npc.getName());
        html.replace("%hpGauge%", HtmlUtil.getHpGauge(250, (long)npc.getCurrentHp(), (long)npc.getMaxHp(), false));
        html.replace("%mpGauge%", HtmlUtil.getMpGauge(250, (long)npc.getCurrentMp(), (long)npc.getMaxMp(), false));
        final Spawn npcSpawn = npc.getSpawn();
        if (Objects.isNull(npcSpawn) || npcSpawn.getRespawnMinDelay() == 0) {
            html.replace("%respawn%", "None");
        }
        else {
            TimeUnit timeUnit = TimeUnit.MILLISECONDS;
            long min = Long.MAX_VALUE;
            for (final TimeUnit tu : TimeUnit.values()) {
                final long minTimeFromMillis = tu.convert(npcSpawn.getRespawnMinDelay(), TimeUnit.MILLISECONDS);
                final long maxTimeFromMillis = tu.convert(npcSpawn.getRespawnMaxDelay(), TimeUnit.MILLISECONDS);
                if (TimeUnit.MILLISECONDS.convert(minTimeFromMillis, tu) == npcSpawn.getRespawnMinDelay() && TimeUnit.MILLISECONDS.convert(maxTimeFromMillis, tu) == npcSpawn.getRespawnMaxDelay() && min > minTimeFromMillis) {
                    min = minTimeFromMillis;
                    timeUnit = tu;
                }
            }
            final long minRespawnDelay = timeUnit.convert(npcSpawn.getRespawnMinDelay(), TimeUnit.MILLISECONDS);
            final long maxRespawnDelay = timeUnit.convert(npcSpawn.getRespawnMaxDelay(), TimeUnit.MILLISECONDS);
            final String timeUnitName = invokedynamic(makeConcatWithConstants:(CLjava/lang/String;)Ljava/lang/String;, timeUnit.name().charAt(0), timeUnit.name().toLowerCase().substring(1));
            if (npcSpawn.hasRespawnRandom()) {
                html.replace("%respawn%", invokedynamic(makeConcatWithConstants:(JJLjava/lang/String;)Ljava/lang/String;, minRespawnDelay, maxRespawnDelay, timeUnitName));
            }
            else {
                html.replace("%respawn%", invokedynamic(makeConcatWithConstants:(JLjava/lang/String;)Ljava/lang/String;, minRespawnDelay, timeUnitName));
            }
        }
        html.replace("%atktype%", CommonUtil.capitalizeFirst(npc.getAttackType().name().toLowerCase()));
        html.replace("%atkrange%", npc.getStats().getPhysicalAttackRange());
        html.replace("%patk%", npc.getPAtk());
        html.replace("%pdef%", npc.getPDef());
        html.replace("%matk%", npc.getMAtk());
        html.replace("%mdef%", npc.getMDef());
        html.replace("%atkspd%", npc.getPAtkSpd());
        html.replace("%castspd%", npc.getMAtkSpd());
        html.replace("%critrate%", npc.getStats().getCriticalHit());
        html.replace("%evasion%", npc.getEvasionRate());
        html.replace("%accuracy%", npc.getStats().getAccuracy());
        html.replace("%speed%", (int)npc.getStats().getMoveSpeed());
        html.replace("%attributeatktype%", npc.getStats().getAttackElement().name());
        html.replace("%attributeatkvalue%", npc.getStats().getAttackElementValue(npc.getStats().getAttackElement()));
        html.replace("%attributefire%", npc.getStats().getDefenseElementValue(AttributeType.FIRE));
        html.replace("%attributewater%", npc.getStats().getDefenseElementValue(AttributeType.WATER));
        html.replace("%attributewind%", npc.getStats().getDefenseElementValue(AttributeType.WIND));
        html.replace("%attributeearth%", npc.getStats().getDefenseElementValue(AttributeType.EARTH));
        html.replace("%attributedark%", npc.getStats().getDefenseElementValue(AttributeType.DARK));
        html.replace("%attributeholy%", npc.getStats().getDefenseElementValue(AttributeType.HOLY));
        html.replace("%dropListButtons%", getDropListButtons(npc));
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    private void sendNpcSkillView(final Player activeChar, final Npc npc) {
        final NpcHtmlMessage html = new NpcHtmlMessage();
        html.setFile(activeChar, "data/html/mods/NpcView/Skills.htm");
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sb2;
        npc.getSkills().values().forEach(s -> {
            sb2.append("<table width=277 height=32 cellspacing=0 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
            sb2.append("<tr><td width=32>");
            sb2.append("<img src=\"");
            sb2.append(s.getIcon());
            sb2.append("\" width=32 height=32>");
            sb2.append("</td><td width=110>");
            sb2.append(s.getName());
            sb2.append("</td>");
            sb2.append("<td width=45 align=center>");
            sb2.append(s.getId());
            sb2.append("</td>");
            sb2.append("<td width=35 align=center>");
            sb2.append(s.getLevel());
            sb2.append("</td></tr></table>");
            return;
        });
        html.replace("%skills%", sb.toString());
        html.replace("%npc_name%", npc.getName());
        html.replace("%npcId%", npc.getId());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    private static void sendAggroListView(final Player activeChar, final Npc npc) {
        final NpcHtmlMessage html = new NpcHtmlMessage();
        html.setFile(activeChar, "data/html/mods/NpcView/AggroList.htm");
        final StringBuilder sb = new StringBuilder();
        if (GameUtils.isAttackable((WorldObject)npc)) {
            final StringBuilder sb2;
            ((Attackable)npc).getAggroList().values().forEach(a -> {
                sb2.append("<table width=277 height=32 cellspacing=0 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
                sb2.append("<tr><td width=110>");
                sb2.append((a.getAttacker() != null) ? a.getAttacker().getName() : "NULL");
                sb2.append("</td>");
                sb2.append("<td width=60 align=center>");
                sb2.append(a.getHate());
                sb2.append("</td>");
                sb2.append("<td width=60 align=center>");
                sb2.append(a.getDamage());
                sb2.append("</td></tr></table>");
                return;
            });
        }
        html.replace("%aggrolist%", sb.toString());
        html.replace("%npc_name%", npc.getName());
        html.replace("%npcId%", npc.getId());
        html.replace("%objid%", npc.getObjectId());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
    }
    
    private static String getDropListButtons(final Npc npc) {
        final StringBuilder sb = new StringBuilder();
        final List<DropHolder> dropListDeath = (List<DropHolder>)npc.getTemplate().getDropList(DropType.DROP);
        final List<DropHolder> dropListSpoil = (List<DropHolder>)npc.getTemplate().getDropList(DropType.SPOIL);
        if (dropListDeath != null || dropListSpoil != null) {
            sb.append("<table width=275 cellpadding=0 cellspacing=0><tr>");
            if (dropListDeath != null) {
                sb.append("<td align=center><button value=\"Show Drop\" width=100 height=25 action=\"bypass NpcViewMod dropList DROP ").append(npc.getObjectId()).append("\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
            }
            if (dropListSpoil != null) {
                sb.append("<td align=center><button value=\"Show Spoil\" width=100 height=25 action=\"bypass NpcViewMod dropList SPOIL ").append(npc.getObjectId()).append("\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
            }
            sb.append("</tr></table>");
        }
        return sb.toString();
    }
    
    private void sendNpcDropList(final Player player, final Npc npc, final DropType dropType, int page) {
        final List<DropHolder> dropList = (List<DropHolder>)npc.getTemplate().getDropList(dropType);
        if (Objects.isNull(dropList)) {
            return;
        }
        final int pages = (int)Math.ceil(dropList.size() / 8.0);
        page = Math.max(0, Math.min(page, pages - 1));
        final StringBuilder pagesSb = new StringBuilder();
        if (pages > 1) {
            pagesSb.append("<table><tr>");
            for (int i = Math.max(0, page - 3); i <= Math.min(pages - 1, page + 3); ++i) {
                if (i == page) {
                    pagesSb.append("<td width=20 height=20 align=CENTER>").append(i + 1).append("</td>");
                }
                else {
                    pagesSb.append("<td><button value=\"").append(i + 1).append("\" width=20 height=20 action=\"bypass NpcViewMod dropList ").append(dropType).append(" ").append(npc.getObjectId()).append(" ").append(i).append("\" fore=\"L2UI_CT1.Button_DF_Calculator\"></td>");
                }
            }
            pagesSb.append("</tr></table>");
        }
        final int start = (page > 0) ? (page * 8) : 0;
        final int end = Math.min(page * 8 + 8, dropList.size());
        final DecimalFormat amountFormat = new DecimalFormat("#,###");
        final DecimalFormat chanceFormat = new DecimalFormat("0.00##");
        int leftHeight = 0;
        int rightHeight = 0;
        final StringBuilder leftSb = new StringBuilder();
        final StringBuilder rightSb = new StringBuilder();
        String limitReachedMsg = "";
        for (int j = start; j < end; ++j) {
            final StringBuilder sb = new StringBuilder();
            final int height = 64;
            final DropHolder dropItem = dropList.get(j);
            final ItemTemplate item = ItemEngine.getInstance().getTemplate(dropItem.getItemId());
            double rateChance = 1.0;
            double rateAmount = 1.0;
            if (dropType == DropType.SPOIL) {
                rateChance = Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER;
                rateAmount = Config.RATE_SPOIL_DROP_AMOUNT_MULTIPLIER;
            }
            else {
                if (Config.RATE_DROP_CHANCE_BY_ID.get(dropItem.getItemId()) != null) {
                    rateChance *= Config.RATE_DROP_CHANCE_BY_ID.get(dropItem.getItemId());
                }
                else if (item.hasExImmediateEffect()) {
                    rateChance *= Config.RATE_HERB_DROP_CHANCE_MULTIPLIER;
                }
                else if (npc.isRaid()) {
                    rateChance *= Config.RATE_RAID_DROP_CHANCE_MULTIPLIER;
                }
                else {
                    rateChance *= Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER;
                }
                if (Config.RATE_DROP_AMOUNT_BY_ID.get(dropItem.getItemId()) != null) {
                    rateAmount *= Config.RATE_DROP_AMOUNT_BY_ID.get(dropItem.getItemId());
                }
                else if (item.hasExImmediateEffect()) {
                    rateAmount *= Config.RATE_HERB_DROP_AMOUNT_MULTIPLIER;
                }
                else if (npc.isRaid()) {
                    rateAmount *= Config.RATE_RAID_DROP_AMOUNT_MULTIPLIER;
                }
                else {
                    rateAmount *= Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER;
                }
            }
            sb.append("<table width=332 cellpadding=2 cellspacing=0 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
            sb.append("<tr><td width=32 valign=top>");
            sb.append("<button width=\"32\" height=\"32\" itemtooltip=\"").append(dropItem.getItemId()).append("\"></td>");
            sb.append("<td><table width=295 cellpadding=0 cellspacing=4>");
            sb.append("<tr><td width=48 align=right valign=top><font color=\"LEVEL\">Amount: </font></td>");
            sb.append("<td>");
            final long min = (long)(dropItem.getMin() * rateAmount);
            final long max = (long)(dropItem.getMax() * rateAmount);
            if (min == max) {
                sb.append(amountFormat.format(min));
            }
            else {
                sb.append(amountFormat.format(min));
                sb.append(" - ");
                sb.append(amountFormat.format(max));
            }
            sb.append("</td></tr><tr><td width=48 align=right valign=top><font color=\"LEVEL\">Chance:</font></td>");
            sb.append("<td width=247>");
            sb.append(chanceFormat.format(Math.min(dropItem.getChance() * rateChance, 100.0)));
            sb.append("%</td></tr></table></td></tr><tr><td width=32></td><td width=300>&nbsp;</td></tr></table>");
            if (sb.length() + rightSb.length() + leftSb.length() >= 12000) {
                limitReachedMsg = "<br><center>Too many drops! Could not display them all!</center>";
                break;
            }
            if (leftHeight >= rightHeight + height) {
                rightSb.append((CharSequence)sb);
                rightHeight += height;
            }
            else {
                leftSb.append((CharSequence)sb);
                leftHeight += height;
            }
        }
        String html = HtmCache.getInstance().getHtm(player, "data/html/mods/NpcView/DropList.htm");
        if (html == null) {
            NpcViewMod.LOGGER.warn("The html file data/html/mods/NpcView/DropList.htm could not be found.");
            return;
        }
        html = html.replaceAll("%name%", npc.getName());
        html = html.replaceAll("%dropListButtons%", getDropListButtons(npc));
        html = html.replaceAll("%pages%", pagesSb.toString());
        final String bodySb = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, leftSb.toString(), rightSb.toString());
        html = html.replaceAll("%items%", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, bodySb, limitReachedMsg));
        GameUtils.sendCBHtml(player, html);
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)NpcViewMod.class);
        COMMANDS = new String[] { "NpcViewMod" };
    }
}
