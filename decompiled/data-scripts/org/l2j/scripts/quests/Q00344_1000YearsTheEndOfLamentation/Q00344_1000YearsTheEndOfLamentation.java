// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00344_1000YearsTheEndOfLamentation;

import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00344_1000YearsTheEndOfLamentation extends Quest
{
    private static final int KAIEN = 30623;
    private static final int GARVARENTZ = 30704;
    private static final int GILMORE = 30754;
    private static final int RODEMAI = 30756;
    private static final int ORVEN = 30857;
    private static final int ARTICLES = 4269;
    private static final ItemHolder OLD_KEY;
    private static final ItemHolder OLD_HILT;
    private static final ItemHolder TOTEM_NECKLACE;
    private static final ItemHolder CRUCIFIX;
    private static final IntMap<Double> MONSTER_CHANCES;
    private static final ItemHolder ORIHARUKON_ORE;
    private static final ItemHolder VARNISH_OF_PURITY;
    private static final ItemHolder SCROLL_EWC;
    private static final ItemHolder RAID_SWORD;
    private static final ItemHolder COKES;
    private static final ItemHolder RING_OF_AGES;
    private static final ItemHolder LEATHER;
    private static final ItemHolder COARSE_BONE_POWDER;
    private static final ItemHolder HEAVY_DOOM_HAMMER;
    private static final ItemHolder STONE_OF_PURITY;
    private static final ItemHolder SCROLL_EAC;
    private static final ItemHolder DRAKE_LEATHER_BOOTS;
    private static final int MIN_LVL = 48;
    
    public Q00344_1000YearsTheEndOfLamentation() {
        super(344);
        Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.put(20236, (Object)0.58);
        Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.put(20238, (Object)0.75);
        Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.put(20237, (Object)0.78);
        Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.put(20239, (Object)0.79);
        Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.put(20240, (Object)0.85);
        Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.put(20272, (Object)0.58);
        Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.put(20273, (Object)0.78);
        Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.put(20274, (Object)0.75);
        Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.put(20275, (Object)0.79);
        Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.put(20276, (Object)0.85);
        this.addStartNpc(30754);
        this.addTalkId(new int[] { 30623, 30704, 30754, 30756, 30857 });
        this.addKillId((IntCollection)Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.keySet());
        this.registerQuestItems(new int[] { 4269, Q00344_1000YearsTheEndOfLamentation.OLD_KEY.getId(), Q00344_1000YearsTheEndOfLamentation.OLD_HILT.getId(), Q00344_1000YearsTheEndOfLamentation.TOTEM_NECKLACE.getId(), Q00344_1000YearsTheEndOfLamentation.CRUCIFIX.getId() });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        String htmltext = null;
        if (qs == null) {
            return htmltext;
        }
        int n = -1;
        switch (event.hashCode()) {
            case 1547374890: {
                if (event.equals("30754-03.htm")) {
                    n = 0;
                    break;
                }
                break;
            }
            case 1697372576: {
                if (event.equals("30754-16.html")) {
                    n = 1;
                    break;
                }
                break;
            }
            case 1548298411: {
                if (event.equals("30754-04.htm")) {
                    n = 2;
                    break;
                }
                break;
            }
            case 867127197: {
                if (event.equals("30754-08.html")) {
                    n = 3;
                    break;
                }
                break;
            }
            case 1726001727: {
                if (event.equals("30754-17.html")) {
                    n = 4;
                    break;
                }
                break;
            }
            case 2071808506: {
                if (event.equals("relic_info")) {
                    n = 5;
                    break;
                }
                break;
            }
        }
        Label_0521: {
            switch (n) {
                case 0:
                case 1: {
                    htmltext = event;
                    break;
                }
                case 2: {
                    if (qs.isCreated()) {
                        qs.startQuest();
                        htmltext = event;
                        break;
                    }
                    break;
                }
                case 3: {
                    if (qs.isCond(1)) {
                        final long count = getQuestItemsCount(player, 4269);
                        if (count < 1L) {
                            htmltext = "30754-07.html";
                        }
                        else {
                            takeItems(player, 4269, -1L);
                            if (getRandom(1000) >= count) {
                                this.giveAdena(player, count * 60L, true);
                                htmltext = event;
                            }
                            else {
                                qs.setCond(2, true);
                                switch (getRandom(4)) {
                                    case 0: {
                                        qs.setMemoState(1);
                                        giveItems(player, Q00344_1000YearsTheEndOfLamentation.OLD_HILT);
                                        break;
                                    }
                                    case 1: {
                                        qs.setMemoState(2);
                                        giveItems(player, Q00344_1000YearsTheEndOfLamentation.OLD_KEY);
                                        break;
                                    }
                                    case 2: {
                                        qs.setMemoState(3);
                                        giveItems(player, Q00344_1000YearsTheEndOfLamentation.TOTEM_NECKLACE);
                                        break;
                                    }
                                    case 3: {
                                        qs.setMemoState(4);
                                        giveItems(player, Q00344_1000YearsTheEndOfLamentation.CRUCIFIX);
                                        break;
                                    }
                                }
                                htmltext = "30754-09.html";
                            }
                        }
                        break;
                    }
                    break;
                }
                case 4: {
                    if (qs.isCond(1)) {
                        htmltext = event;
                        qs.exitQuest(true, true);
                        break;
                    }
                    break;
                }
                case 5: {
                    switch (qs.getMemoState()) {
                        case 1: {
                            htmltext = "30754-10.html";
                            break Label_0521;
                        }
                        case 2: {
                            htmltext = "30754-11.html";
                            break Label_0521;
                        }
                        case 3: {
                            htmltext = "30754-12.html";
                            break Label_0521;
                        }
                        case 4: {
                            htmltext = "30754-13.html";
                            break Label_0521;
                        }
                    }
                    break;
                }
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player talker) {
        final QuestState qs = this.getQuestState(talker, true);
        String htmltext = getNoQuestMsg(talker);
        switch (npc.getId()) {
            case 30754: {
                if (qs.isCreated()) {
                    htmltext = ((talker.getLevel() >= 48) ? "30754-02.htm" : "30754-01.htm");
                    break;
                }
                if (!qs.isStarted()) {
                    htmltext = getAlreadyCompletedMsg(talker);
                    break;
                }
                if (qs.isCond(1)) {
                    htmltext = (hasQuestItems(talker, 4269) ? "30754-06.html" : "30754-05.html");
                    break;
                }
                if (hasItem(talker, Q00344_1000YearsTheEndOfLamentation.OLD_KEY) || hasItem(talker, Q00344_1000YearsTheEndOfLamentation.OLD_HILT) || hasItem(talker, Q00344_1000YearsTheEndOfLamentation.TOTEM_NECKLACE) || hasItem(talker, Q00344_1000YearsTheEndOfLamentation.CRUCIFIX)) {
                    htmltext = "30754-14.html";
                    break;
                }
                qs.setCond(1);
                htmltext = "30754-15.html";
                break;
            }
            case 30623: {
                if (qs.getMemoState() != 1) {
                    break;
                }
                if (hasItem(talker, Q00344_1000YearsTheEndOfLamentation.OLD_HILT)) {
                    takeItems(talker, Q00344_1000YearsTheEndOfLamentation.OLD_HILT.getId(), -1L);
                    final int rand = getRandom(100);
                    if (rand <= 52) {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.ORIHARUKON_ORE);
                    }
                    else if (rand <= 76) {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.VARNISH_OF_PURITY);
                    }
                    else if (rand <= 98) {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.SCROLL_EWC);
                    }
                    else {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.RAID_SWORD);
                    }
                    qs.setCond(1);
                    htmltext = "30623-01.html";
                    break;
                }
                htmltext = "30623-02.html";
                break;
            }
            case 30756: {
                if (qs.getMemoState() != 2) {
                    break;
                }
                if (hasItem(talker, Q00344_1000YearsTheEndOfLamentation.OLD_KEY)) {
                    takeItems(talker, Q00344_1000YearsTheEndOfLamentation.OLD_KEY.getId(), -1L);
                    final int rand = getRandom(100);
                    if (rand <= 39) {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.COKES);
                    }
                    else if (rand <= 89) {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.SCROLL_EWC);
                    }
                    else {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.RING_OF_AGES);
                    }
                    qs.setCond(1);
                    htmltext = "30756-01.html";
                    break;
                }
                htmltext = "30756-02.html";
                break;
            }
            case 30704: {
                if (qs.getMemoState() != 3) {
                    break;
                }
                if (hasItem(talker, Q00344_1000YearsTheEndOfLamentation.TOTEM_NECKLACE)) {
                    takeItems(talker, Q00344_1000YearsTheEndOfLamentation.TOTEM_NECKLACE.getId(), -1L);
                    final int rand = getRandom(100);
                    if (rand <= 47) {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.LEATHER);
                    }
                    else if (rand <= 97) {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.COARSE_BONE_POWDER);
                    }
                    else {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.HEAVY_DOOM_HAMMER);
                    }
                    qs.setCond(1);
                    htmltext = "30704-01.html";
                    break;
                }
                htmltext = "30704-02.html";
                break;
            }
            case 30857: {
                if (qs.getMemoState() != 4) {
                    break;
                }
                if (hasItem(talker, Q00344_1000YearsTheEndOfLamentation.CRUCIFIX)) {
                    takeItems(talker, Q00344_1000YearsTheEndOfLamentation.CRUCIFIX.getId(), -1L);
                    final int rand = getRandom(100);
                    if (rand <= 49) {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.STONE_OF_PURITY);
                    }
                    else if (rand <= 69) {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.SCROLL_EAC);
                    }
                    else {
                        rewardItems(talker, Q00344_1000YearsTheEndOfLamentation.DRAKE_LEATHER_BOOTS);
                    }
                    qs.setCond(1);
                    htmltext = "30857-01.html";
                    break;
                }
                htmltext = "30857-02.html";
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState qs = this.getRandomPartyMemberState(killer, 1, 3, npc);
        if (qs != null) {
            giveItemRandomly(qs.getPlayer(), npc, 4269, 1L, 0L, (double)Q00344_1000YearsTheEndOfLamentation.MONSTER_CHANCES.get(npc.getId()), true);
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    static {
        OLD_KEY = new ItemHolder(4270, 1L);
        OLD_HILT = new ItemHolder(4271, 1L);
        TOTEM_NECKLACE = new ItemHolder(4272, 1L);
        CRUCIFIX = new ItemHolder(4273, 1L);
        MONSTER_CHANCES = (IntMap)new HashIntMap();
        ORIHARUKON_ORE = new ItemHolder(1874, 25L);
        VARNISH_OF_PURITY = new ItemHolder(1887, 10L);
        SCROLL_EWC = new ItemHolder(951, 1L);
        RAID_SWORD = new ItemHolder(133, 1L);
        COKES = new ItemHolder(1879, 55L);
        RING_OF_AGES = new ItemHolder(885, 1L);
        LEATHER = new ItemHolder(1882, 70L);
        COARSE_BONE_POWDER = new ItemHolder(1881, 50L);
        HEAVY_DOOM_HAMMER = new ItemHolder(191, 1L);
        STONE_OF_PURITY = new ItemHolder(1875, 19L);
        SCROLL_EAC = new ItemHolder(952, 5L);
        DRAKE_LEATHER_BOOTS = new ItemHolder(2437, 1L);
    }
}
