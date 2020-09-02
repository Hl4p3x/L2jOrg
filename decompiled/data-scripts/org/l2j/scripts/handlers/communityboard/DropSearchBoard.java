// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.communityboard;

import java.util.StringJoiner;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.stats.Stat;
import java.text.DecimalFormat;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import java.util.Iterator;
import org.l2j.gameserver.model.holders.DropHolder;
import org.l2j.gameserver.enums.DropType;
import org.l2j.gameserver.data.xml.impl.NpcData;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Map;
import org.l2j.gameserver.handler.IParseBoardHandler;

public class DropSearchBoard implements IParseBoardHandler
{
    private static final String NAVIGATION_PATH = "data/html/CommunityBoard/Custom/navigation.html";
    private static final String[] COMMAND;
    private final Map<Integer, List<CBDropHolder>> DROP_INDEX_CACHE;
    private final Set<Integer> BLOCK_ID;
    
    public DropSearchBoard() {
        this.DROP_INDEX_CACHE = new HashMap<Integer, List<CBDropHolder>>();
        (this.BLOCK_ID = new HashSet<Integer>()).add(57);
        this.buildDropIndex();
    }
    
    private void buildDropIndex() {
        final Iterator<DropHolder> iterator;
        DropHolder dropHolder;
        NpcData.getInstance().getTemplates(npc -> npc.getDropList(DropType.DROP) != null).forEach(npcTemplate -> {
            npcTemplate.getDropList(DropType.DROP).iterator();
            while (iterator.hasNext()) {
                dropHolder = iterator.next();
                this.addToDropList(npcTemplate, dropHolder);
            }
            return;
        });
        final Iterator<DropHolder> iterator2;
        DropHolder dropHolder2;
        NpcData.getInstance().getTemplates(npc -> npc.getDropList(DropType.SPOIL) != null).forEach(npcTemplate -> {
            npcTemplate.getDropList(DropType.SPOIL).iterator();
            while (iterator2.hasNext()) {
                dropHolder2 = iterator2.next();
                this.addToDropList(npcTemplate, dropHolder2);
            }
            return;
        });
        this.DROP_INDEX_CACHE.values().stream().forEach(l -> l.sort((d1, d2) -> Byte.valueOf(d1.npcLevel).compareTo(d2.npcLevel)));
    }
    
    private void addToDropList(final NpcTemplate npcTemplate, final DropHolder dropHolder) {
        if (this.BLOCK_ID.contains(dropHolder.getItemId())) {
            return;
        }
        List<CBDropHolder> dropList = this.DROP_INDEX_CACHE.get(dropHolder.getItemId());
        if (dropList == null) {
            dropList = new ArrayList<CBDropHolder>();
            this.DROP_INDEX_CACHE.put(dropHolder.getItemId(), dropList);
        }
        dropList.add(new CBDropHolder(npcTemplate, dropHolder));
    }
    
    public boolean parseCommunityBoardCommand(final String command, final StringTokenizer tokens, final Player player) {
        final String navigation = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/Custom/navigation.html");
        final String[] params = command.split(" ");
        String html = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/Custom/dropsearch/main.html");
        final String s = params[0];
        switch (s) {
            case "_bbs_search_item": {
                final String itemName = this.buildItemName(params);
                final String result = this.buildItemSearchResult(itemName);
                html = html.replace("%searchResult%", result);
                break;
            }
            case "_bbs_search_drop": {
                final DecimalFormat chanceFormat = new DecimalFormat("0.00##");
                final int itemId = Integer.parseInt(params[1]);
                int page = Integer.parseInt(params[2]);
                final List<CBDropHolder> list = this.DROP_INDEX_CACHE.get(itemId);
                int pages = list.size() / 14;
                if (pages == 0) {
                    ++pages;
                }
                final int start = (page - 1) * 14;
                final int end = Math.min(list.size() - 1, start + 14);
                final StringBuilder builder = new StringBuilder();
                final double dropAmountEffectBonus = player.getStats().getValue(Stat.BONUS_DROP_AMOUNT, 1.0);
                final double dropRateEffectBonus = player.getStats().getValue(Stat.BONUS_DROP_RATE, 1.0);
                final double spoilRateEffectBonus = player.getStats().getValue(Stat.BONUS_SPOIL_RATE, 1.0);
                for (int index = start; index <= end; ++index) {
                    final CBDropHolder cbDropHolder = list.get(index);
                    double rateChance = 1.0;
                    double rateAmount = 1.0;
                    if (cbDropHolder.isSpoil) {
                        rateChance = Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER;
                        rateAmount = Config.RATE_SPOIL_DROP_AMOUNT_MULTIPLIER;
                        rateChance *= spoilRateEffectBonus;
                    }
                    else {
                        final ItemTemplate item = ItemEngine.getInstance().getTemplate(cbDropHolder.itemId);
                        if (Config.RATE_DROP_CHANCE_BY_ID.get(cbDropHolder.itemId) != null) {
                            rateChance *= Config.RATE_DROP_CHANCE_BY_ID.get(cbDropHolder.itemId);
                        }
                        else if (item.hasExImmediateEffect()) {
                            rateChance *= Config.RATE_HERB_DROP_CHANCE_MULTIPLIER;
                        }
                        else if (cbDropHolder.isRaid) {
                            rateAmount *= Config.RATE_RAID_DROP_CHANCE_MULTIPLIER;
                        }
                        else {
                            rateChance *= Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER;
                        }
                        if (Config.RATE_DROP_AMOUNT_BY_ID.get(cbDropHolder.itemId) != null) {
                            rateAmount *= Config.RATE_DROP_AMOUNT_BY_ID.get(cbDropHolder.itemId);
                        }
                        else if (item.hasExImmediateEffect()) {
                            rateAmount *= Config.RATE_HERB_DROP_AMOUNT_MULTIPLIER;
                        }
                        else if (cbDropHolder.isRaid) {
                            rateAmount *= Config.RATE_RAID_DROP_AMOUNT_MULTIPLIER;
                        }
                        else {
                            rateAmount *= Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER;
                        }
                        rateAmount *= dropAmountEffectBonus;
                        rateChance *= dropRateEffectBonus;
                    }
                    builder.append("<tr>");
                    builder.append("<td width=30>").append(cbDropHolder.npcLevel).append("</td>");
                    builder.append("<td width=170>").append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, cbDropHolder.npcId)).append("&@").append(cbDropHolder.npcId).append(";").append("</a>").append("</td>");
                    builder.append("<td width=80 align=CENTER>").append(cbDropHolder.min * rateAmount).append("-").append(cbDropHolder.max * rateAmount).append("</td>");
                    builder.append("<td width=50 align=CENTER>").append(chanceFormat.format(cbDropHolder.chance * rateChance)).append("%").append("</td>");
                    builder.append("<td width=50 align=CENTER>").append(cbDropHolder.isSpoil ? "Spoil" : "Drop").append("</td>");
                    builder.append("</tr>");
                }
                html = html.replace("%searchResult%", builder.toString());
                builder.setLength(0);
                builder.append("<tr>");
                for (page = 1; page <= pages; ++page) {
                    builder.append("<td>").append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, itemId, page)).append(page).append("</a>").append("</td>");
                }
                builder.append("</tr>");
                html = html.replace("%pages%", builder.toString());
                break;
            }
            case "_bbs_npc_trace": {
                final int npcId = Integer.parseInt(params[1]);
                final List<NpcSpawnTemplate> spawnList = (List<NpcSpawnTemplate>)SpawnsData.getInstance().getNpcSpawns(npc -> npc.getId() == npcId);
                if (spawnList.isEmpty()) {
                    player.sendMessage("Cannot find any spawn. Maybe dropped by a boss or instance monster.");
                    break;
                }
                final NpcSpawnTemplate spawn = spawnList.get(Rnd.get(spawnList.size()));
                player.getRadar().addMarker(spawn.getSpawnLocation().getX(), spawn.getSpawnLocation().getY(), spawn.getSpawnLocation().getZ());
                break;
            }
        }
        if (html != null) {
            html = html.replace("%navigation%", navigation);
            CommunityBoardHandler.separateAndSend(html, player);
        }
        return false;
    }
    
    private String buildItemSearchResult(final String itemName) {
        int limit = 0;
        final Set<Integer> existInDropData = this.DROP_INDEX_CACHE.keySet();
        final List<ItemTemplate> items = new ArrayList<ItemTemplate>();
        for (final ItemTemplate item : ItemEngine.getInstance().getAllItems()) {
            if (item == null) {
                continue;
            }
            if (!existInDropData.contains(item.getId())) {
                continue;
            }
            if (item.getName().toLowerCase().contains(itemName.toLowerCase())) {
                items.add(item);
                ++limit;
            }
            if (limit == 14) {
                break;
            }
        }
        if (items.isEmpty()) {
            return "<tr><td width=100 align=CENTER>No Match</td></tr>";
        }
        int line = 0;
        final StringBuilder builder = new StringBuilder(items.size() * 28);
        int i = 0;
        for (final ItemTemplate item2 : items) {
            if (++i == 1) {
                ++line;
                builder.append("<tr>");
            }
            builder.append("<td>");
            builder.append("<button value=\".\" action=\"bypass _bbs_search_drop ").append(item2.getId()).append(" 1 $order $level\" width=32 height=32  itemtooltip=\"").append(item2.getId()).append("\">");
            builder.append("</td>");
            builder.append("<td width=200>");
            builder.append("&#").append(item2.getId()).append(";");
            builder.append("</td>");
            if (i == 2) {
                builder.append("</tr>");
                i = 0;
            }
        }
        if (i % 2 == 1) {
            builder.append("</tr>");
        }
        if (line < 7) {
            for (i = 0; i < 7 - line; ++i) {
                builder.append("<tr><td height=36></td></tr>");
            }
        }
        return builder.toString();
    }
    
    private String buildItemName(final String[] params) {
        final StringJoiner joiner = new StringJoiner(" ");
        for (int i = 1; i < params.length; ++i) {
            joiner.add(params[i]);
        }
        return joiner.toString();
    }
    
    public String[] getCommunityBoardCommands() {
        return DropSearchBoard.COMMAND;
    }
    
    static {
        COMMAND = new String[] { "_bbs_search_item", "_bbs_search_drop", "_bbs_npc_trace" };
    }
    
    private class CBDropHolder
    {
        final int itemId;
        final int npcId;
        final byte npcLevel;
        final long min;
        final long max;
        final double chance;
        final boolean isSpoil;
        final boolean isRaid;
        
        public CBDropHolder(final NpcTemplate npcTemplate, final DropHolder dropHolder) {
            this.isSpoil = (dropHolder.getDropType() == DropType.SPOIL);
            this.itemId = dropHolder.getItemId();
            this.npcId = npcTemplate.getId();
            this.npcLevel = npcTemplate.getLevel();
            this.min = dropHolder.getMin();
            this.max = dropHolder.getMax();
            this.chance = dropHolder.getChance();
            this.isRaid = (npcTemplate.getType().equals("RaidBoss") || npcTemplate.getType().equals("GrandBoss"));
        }
        
        @Override
        public String toString() {
            return invokedynamic(makeConcatWithConstants:(IIBJJDZ)Ljava/lang/String;, this.itemId, this.npcId, this.npcLevel, this.min, this.max, this.chance, this.isSpoil);
        }
    }
}
