// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00620_FourGoblets;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.quest.Quest;

public class Q00620_FourGoblets extends Quest
{
    private static final int NAMELESS_SPIRIT = 31453;
    private static final int GHOST_OF_WIGOTH_1 = 31452;
    private static final int GHOST_OF_WIGOTH_2 = 31454;
    private static final int CONQ_SM = 31921;
    private static final int EMPER_SM = 31922;
    private static final int SAGES_SM = 31923;
    private static final int JUDGE_SM = 31924;
    private static final int GHOST_CHAMBERLAIN_1 = 31919;
    private static final int GHOST_CHAMBERLAIN_2 = 31920;
    private static final int[] NPCS;
    private static final int ANTIQUE_BROOCH = 7262;
    private static final int ENTRANCE_PASS = 7075;
    private static final int GRAVE_PASS = 7261;
    private static final int[] GOBLETS;
    private static final int BOSS_1 = 25339;
    private static final int BOSS_2 = 25342;
    private static final int BOSS_3 = 25346;
    private static final int BOSS_4 = 25349;
    private static final int RELIC = 7254;
    private static final int SEALED_BOX = 7255;
    private static final int[] QI;
    private static final int MIN_LEVEL = 74;
    private static final int MAX_LEVEL = 80;
    
    public Q00620_FourGoblets() {
        super(620);
        this.addStartNpc(31453);
        for (final int i : Q00620_FourGoblets.NPCS) {
            this.addTalkId(i);
        }
        for (final int j : Q00620_FourGoblets.QI) {
            this.registerQuestItems(new int[] { j });
        }
        for (int id = 18120; id <= 18256; ++id) {
            this.addKillId(new int[] { id, 25339, 25342, 25346, 25349 });
        }
        this.addCondLevel(74, 80, "31453-12.htm");
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = event;
        final QuestState qs = player.getQuestState(this.getName());
        if (qs == null) {
            return htmltext;
        }
        switch (event) {
            case "accept": {
                if (qs.getPlayer().getLevel() >= 74 && qs.getPlayer().getLevel() <= 80) {
                    qs.startQuest();
                    htmltext = "31453-13.htm";
                    giveItems(player, 7075, 1L);
                    break;
                }
                htmltext = "31453-12.htm";
                break;
            }
            case "11": {
                if (getQuestItemsCount(player, 7255) >= 1L) {
                    htmltext = "31454-13.htm";
                    takeItems(player, 7255, 1L);
                    int reward = 0;
                    final int rnd = Rnd.get(5);
                    if (rnd == 0) {
                        giveItems(player, 57, 10000L);
                        reward = 1;
                    }
                    else if (rnd == 1) {
                        if (Rnd.get(1000) < 848) {
                            reward = 1;
                            final int i = Rnd.get(1000);
                            if (i < 43) {
                                giveItems(player, 1884, 42L);
                            }
                            else if (i < 66) {
                                giveItems(player, 1895, 36L);
                            }
                            else if (i < 184) {
                                giveItems(player, 1876, 4L);
                            }
                            else if (i < 250) {
                                giveItems(player, 1881, 6L);
                            }
                            else if (i < 287) {
                                giveItems(player, 5549, 8L);
                            }
                            else if (i < 484) {
                                giveItems(player, 1874, 1L);
                            }
                            else if (i < 681) {
                                giveItems(player, 1889, 1L);
                            }
                            else if (i < 799) {
                                giveItems(player, 1877, 1L);
                            }
                            else if (i < 902) {
                                giveItems(player, 1894, 1L);
                            }
                            else {
                                giveItems(player, 4043, 1L);
                            }
                        }
                        else if (Rnd.get(1000) < 323) {
                            reward = 1;
                            final int i = Rnd.get(1000);
                            if (i < 335) {
                                giveItems(player, 1888, 1L);
                            }
                            else if (i < 556) {
                                giveItems(player, 4040, 1L);
                            }
                            else if (i < 725) {
                                giveItems(player, 1890, 1L);
                            }
                            else if (i < 872) {
                                giveItems(player, 5550, 1L);
                            }
                            else if (i < 962) {
                                giveItems(player, 1893, 1L);
                            }
                            else if (i < 986) {
                                giveItems(player, 4046, 1L);
                            }
                            else {
                                giveItems(player, 4048, 1L);
                            }
                        }
                    }
                    else if (rnd == 2) {
                        if (Rnd.get(1000) < 847) {
                            reward = 1;
                            final int i = Rnd.get(1000);
                            if (i < 148) {
                                giveItems(player, 1878, 8L);
                            }
                            else if (i < 175) {
                                giveItems(player, 1882, 24L);
                            }
                            else if (i < 273) {
                                giveItems(player, 1879, 4L);
                            }
                            else if (i < 322) {
                                giveItems(player, 1880, 6L);
                            }
                            else if (i < 357) {
                                giveItems(player, 1885, 6L);
                            }
                            else if (i < 554) {
                                giveItems(player, 1875, 1L);
                            }
                            else if (i < 685) {
                                giveItems(player, 1883, 1L);
                            }
                            else if (i < 803) {
                                giveItems(player, 5220, 1L);
                            }
                            else if (i < 901) {
                                giveItems(player, 4039, 1L);
                            }
                            else {
                                giveItems(player, 4044, 1L);
                            }
                        }
                        else if (Rnd.get(1000) < 251) {
                            reward = 1;
                            final int i = Rnd.get(1000);
                            if (i < 350) {
                                giveItems(player, 1887, 1L);
                            }
                            else if (i < 587) {
                                giveItems(player, 4042, 1L);
                            }
                            else if (i < 798) {
                                giveItems(player, 1886, 1L);
                            }
                            else if (i < 922) {
                                giveItems(player, 4041, 1L);
                            }
                            else if (i < 966) {
                                giveItems(player, 1892, 1L);
                            }
                            else if (i < 996) {
                                giveItems(player, 1891, 1L);
                            }
                            else {
                                giveItems(player, 4047, 1L);
                            }
                        }
                    }
                    else if (rnd == 3) {
                        if (Rnd.get(1000) < 31) {
                            reward = 1;
                            final int i = Rnd.get(1000);
                            if (i < 223) {
                                giveItems(player, 730, 1L);
                            }
                            else if (i < 893) {
                                giveItems(player, 948, 1L);
                            }
                            else {
                                giveItems(player, 960, 1L);
                            }
                        }
                        else if (Rnd.get(1000) < 50) {
                            reward = 1;
                            final int i = Rnd.get(1000);
                            if (i < 202) {
                                giveItems(player, 729, 1L);
                            }
                            else if (i < 928) {
                                giveItems(player, 947, 1L);
                            }
                            else {
                                giveItems(player, 959, 1L);
                            }
                        }
                    }
                    else if (rnd == 4) {
                        if (Rnd.get(1000) < 329) {
                            reward = 1;
                            final int i = Rnd.get(1000);
                            if (i < 88) {
                                giveItems(player, 6698, 1L);
                            }
                            else if (i < 185) {
                                giveItems(player, 6699, 1L);
                            }
                            else if (i < 238) {
                                giveItems(player, 6700, 1L);
                            }
                            else if (i < 262) {
                                giveItems(player, 6701, 1L);
                            }
                            else if (i < 292) {
                                giveItems(player, 6702, 1L);
                            }
                            else if (i < 356) {
                                giveItems(player, 6703, 1L);
                            }
                            else if (i < 420) {
                                giveItems(player, 6704, 1L);
                            }
                            else if (i < 482) {
                                giveItems(player, 6705, 1L);
                            }
                            else if (i < 554) {
                                giveItems(player, 6706, 1L);
                            }
                            else if (i < 576) {
                                giveItems(player, 6707, 1L);
                            }
                            else if (i < 640) {
                                giveItems(player, 6708, 1L);
                            }
                            else if (i < 704) {
                                giveItems(player, 6709, 1L);
                            }
                            else if (i < 777) {
                                giveItems(player, 6710, 1L);
                            }
                            else if (i < 799) {
                                giveItems(player, 6711, 1L);
                            }
                            else if (i < 863) {
                                giveItems(player, 6712, 1L);
                            }
                            else if (i < 927) {
                                giveItems(player, 6713, 1L);
                            }
                            else {
                                giveItems(player, 6714, 1L);
                            }
                        }
                        else if (Rnd.get(1000) < 54) {
                            reward = 1;
                            final int i = Rnd.get(1000);
                            if (i < 100) {
                                giveItems(player, 6688, 1L);
                            }
                            else if (i < 198) {
                                giveItems(player, 6689, 1L);
                            }
                            else if (i < 298) {
                                giveItems(player, 6690, 1L);
                            }
                            else if (i < 398) {
                                giveItems(player, 6691, 1L);
                            }
                            else if (i < 499) {
                                giveItems(player, 7579, 1L);
                            }
                            else if (i < 601) {
                                giveItems(player, 6693, 1L);
                            }
                            else if (i < 703) {
                                giveItems(player, 6694, 1L);
                            }
                            else if (i < 801) {
                                giveItems(player, 6695, 1L);
                            }
                            else if (i < 902) {
                                giveItems(player, 6696, 1L);
                            }
                            else {
                                giveItems(player, 6697, 1L);
                            }
                        }
                    }
                    else if (reward == 0) {
                        if (Rnd.nextBoolean()) {
                            htmltext = "31454-14.htm";
                        }
                        else {
                            htmltext = "31454-15.htm";
                        }
                    }
                    break;
                }
                break;
            }
            case "12": {
                if (getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[0]) >= 1L && getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[1]) >= 1L && getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[2]) >= 1L && getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[3]) >= 1L) {
                    takeItems(player, Q00620_FourGoblets.GOBLETS[0], -1L);
                    takeItems(player, Q00620_FourGoblets.GOBLETS[1], -1L);
                    takeItems(player, Q00620_FourGoblets.GOBLETS[2], -1L);
                    takeItems(player, Q00620_FourGoblets.GOBLETS[3], -1L);
                    if (getQuestItemsCount(player, 7262) < 1L) {
                        giveItems(player, 7262, 1L);
                    }
                    qs.setCond(2, true);
                    htmltext = "31453-16.htm";
                    break;
                }
                htmltext = "31453-14.htm";
                break;
            }
            case "13": {
                qs.exitQuest(true, true);
                htmltext = "31453-18.htm";
                break;
            }
            case "14": {
                htmltext = "31453-13.htm";
                if (qs.getCond() == 2) {
                    htmltext = "31453-19.htm";
                    break;
                }
                break;
            }
            case "15": {
                if (getQuestItemsCount(player, 7262) >= 1L) {
                    qs.getPlayer().teleToLocation(178298, -84574, -7216);
                    htmltext = null;
                    break;
                }
                if (getQuestItemsCount(player, 7261) >= 1L) {
                    takeItems(player, 7261, 1L);
                    qs.getPlayer().teleToLocation(178298, -84574, -7216);
                    htmltext = null;
                    break;
                }
                htmltext = "31919-0.htm";
                break;
            }
            case "16": {
                if (getQuestItemsCount(player, 7262) >= 1L) {
                    qs.getPlayer().teleToLocation(186942, -75602, -2834);
                    htmltext = null;
                    break;
                }
                if (getQuestItemsCount(player, 7261) >= 1L) {
                    takeItems(player, 7261, 1L);
                    qs.getPlayer().teleToLocation(186942, -75602, -2834);
                    htmltext = null;
                    break;
                }
                htmltext = "31920-0.htm";
                break;
            }
            case "17": {
                if (getQuestItemsCount(player, 7262) >= 1L) {
                    qs.getPlayer().teleToLocation(169590, -90218, -2914);
                    break;
                }
                takeItems(player, 7261, 1L);
                qs.getPlayer().teleToLocation(169590, -90218, -2914);
                htmltext = "31452-6.htm";
                break;
            }
            case "18": {
                if (getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[0]) + getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[1]) + getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[2]) + getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[3]) < 3L) {
                    htmltext = "31452-3.htm";
                    break;
                }
                if (getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[0]) + getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[1]) + getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[2]) + getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[3]) == 3L) {
                    htmltext = "31452-4.htm";
                    break;
                }
                if (getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[0]) + getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[1]) + getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[2]) + getQuestItemsCount(player, Q00620_FourGoblets.GOBLETS[3]) >= 4L) {
                    htmltext = "31452-5.htm";
                    break;
                }
                break;
            }
            case "19": {
                htmltext = "31919-3.htm";
                takeItems(player, 7255, 1L);
                int reward = 0;
                final int rnd = Rnd.get(5);
                if (rnd == 0) {
                    giveItems(player, 57, 10000L);
                    reward = 1;
                }
                else if (rnd == 1) {
                    if (Rnd.get(1000) < 848) {
                        reward = 1;
                        final int i = Rnd.get(1000);
                        if (i < 43) {
                            giveItems(player, 1884, 42L);
                        }
                        else if (i < 66) {
                            giveItems(player, 1895, 36L);
                        }
                        else if (i < 184) {
                            giveItems(player, 1876, 4L);
                        }
                        else if (i < 250) {
                            giveItems(player, 1881, 6L);
                        }
                        else if (i < 287) {
                            giveItems(player, 5549, 8L);
                        }
                        else if (i < 484) {
                            giveItems(player, 1874, 1L);
                        }
                        else if (i < 681) {
                            giveItems(player, 1889, 1L);
                        }
                        else if (i < 799) {
                            giveItems(player, 1877, 1L);
                        }
                        else if (i < 902) {
                            giveItems(player, 1894, 1L);
                        }
                        else {
                            giveItems(player, 4043, 1L);
                        }
                    }
                    else if (Rnd.get(1000) < 323) {
                        reward = 1;
                        final int i = Rnd.get(1000);
                        if (i < 335) {
                            giveItems(player, 1888, 1L);
                        }
                        else if (i < 556) {
                            giveItems(player, 4040, 1L);
                        }
                        else if (i < 725) {
                            giveItems(player, 1890, 1L);
                        }
                        else if (i < 872) {
                            giveItems(player, 5550, 1L);
                        }
                        else if (i < 962) {
                            giveItems(player, 1893, 1L);
                        }
                        else if (i < 986) {
                            giveItems(player, 4046, 1L);
                        }
                        else {
                            giveItems(player, 4048, 1L);
                        }
                    }
                }
                else if (rnd == 2) {
                    if (Rnd.get(1000) < 847) {
                        reward = 1;
                        final int i = Rnd.get(1000);
                        if (i < 148) {
                            giveItems(player, 1878, 8L);
                        }
                        else if (i < 175) {
                            giveItems(player, 1882, 24L);
                        }
                        else if (i < 273) {
                            giveItems(player, 1879, 4L);
                        }
                        else if (i < 322) {
                            giveItems(player, 1880, 6L);
                        }
                        else if (i < 357) {
                            giveItems(player, 1885, 6L);
                        }
                        else if (i < 554) {
                            giveItems(player, 1875, 1L);
                        }
                        else if (i < 685) {
                            giveItems(player, 1883, 1L);
                        }
                        else if (i < 803) {
                            giveItems(player, 5220, 1L);
                        }
                        else if (i < 901) {
                            giveItems(player, 4039, 1L);
                        }
                        else {
                            giveItems(player, 4044, 1L);
                        }
                    }
                    else if (Rnd.get(1000) < 251) {
                        reward = 1;
                        final int i = Rnd.get(1000);
                        if (i < 350) {
                            giveItems(player, 1887, 1L);
                        }
                        else if (i < 587) {
                            giveItems(player, 4042, 1L);
                        }
                        else if (i < 798) {
                            giveItems(player, 1886, 1L);
                        }
                        else if (i < 922) {
                            giveItems(player, 4041, 1L);
                        }
                        else if (i < 966) {
                            giveItems(player, 1892, 1L);
                        }
                        else if (i < 996) {
                            giveItems(player, 1891, 1L);
                        }
                        else {
                            giveItems(player, 4047, 1L);
                        }
                    }
                }
                else if (rnd == 3) {
                    if (Rnd.get(1000) < 31) {
                        reward = 1;
                        final int i = Rnd.get(1000);
                        if (i < 223) {
                            giveItems(player, 730, 1L);
                        }
                        else if (i < 893) {
                            giveItems(player, 948, 1L);
                        }
                        else {
                            giveItems(player, 960, 1L);
                        }
                    }
                    else if (Rnd.get(1000) < 5) {
                        reward = 1;
                    }
                    final int i = Rnd.get(1000);
                    if (i < 202) {
                        giveItems(player, 729, 1L);
                    }
                    else if (i < 928) {
                        giveItems(player, 947, 1L);
                    }
                    else {
                        giveItems(player, 959, 1L);
                    }
                }
                else if (rnd == 4) {
                    if (Rnd.get(1000) < 329) {
                        reward = 1;
                        final int i = Rnd.get(1000);
                        if (i < 88) {
                            giveItems(player, 6698, 1L);
                        }
                        else if (i < 185) {
                            giveItems(player, 6699, 1L);
                        }
                        else if (i < 238) {
                            giveItems(player, 6700, 1L);
                        }
                        else if (i < 262) {
                            giveItems(player, 6701, 1L);
                        }
                        else if (i < 292) {
                            giveItems(player, 6702, 1L);
                        }
                        else if (i < 356) {
                            giveItems(player, 6703, 1L);
                        }
                        else if (i < 420) {
                            giveItems(player, 6704, 1L);
                        }
                        else if (i < 482) {
                            giveItems(player, 6705, 1L);
                        }
                        else if (i < 554) {
                            giveItems(player, 6706, 1L);
                        }
                        else if (i < 576) {
                            giveItems(player, 6707, 1L);
                        }
                        else if (i < 640) {
                            giveItems(player, 6708, 1L);
                        }
                        else if (i < 704) {
                            giveItems(player, 6709, 1L);
                        }
                        else if (i < 777) {
                            giveItems(player, 6710, 1L);
                        }
                        else if (i < 799) {
                            giveItems(player, 6711, 1L);
                        }
                        else if (i < 863) {
                            giveItems(player, 6712, 1L);
                        }
                        else if (i < 927) {
                            giveItems(player, 6713, 1L);
                        }
                        else {
                            giveItems(player, 6714, 1L);
                        }
                    }
                    else if (Rnd.get(1000) < 54) {
                        reward = 1;
                        final int i = Rnd.get(1000);
                        if (i < 100) {
                            giveItems(player, 6688, 1L);
                        }
                        else if (i < 198) {
                            giveItems(player, 6689, 1L);
                        }
                        else if (i < 298) {
                            giveItems(player, 6690, 1L);
                        }
                        else if (i < 398) {
                            giveItems(player, 6691, 1L);
                        }
                        else if (i < 499) {
                            giveItems(player, 7579, 1L);
                        }
                        else if (i < 601) {
                            giveItems(player, 6693, 1L);
                        }
                        else if (i < 703) {
                            giveItems(player, 6694, 1L);
                        }
                        else if (i < 801) {
                            giveItems(player, 6695, 1L);
                        }
                        else if (i < 902) {
                            giveItems(player, 6696, 1L);
                        }
                        else {
                            giveItems(player, 6697, 1L);
                        }
                    }
                }
                if (reward != 0) {
                    break;
                }
                if (Rnd.nextBoolean()) {
                    htmltext = "31919-4.htm";
                    break;
                }
                htmltext = "31919-5.htm";
                break;
            }
            case "6881": {
                takeItems(player, 7254, 1000L);
                giveItems(player, qs.getInt(event), 1L);
                htmltext = "31454-17.htm";
                break;
            }
            case "6883": {
                takeItems(player, 7254, 1000L);
                giveItems(player, qs.getInt(event), 1L);
                htmltext = "31454-17.htm";
                break;
            }
            case "6885": {
                takeItems(player, 7254, 1000L);
                giveItems(player, qs.getInt(event), 1L);
                htmltext = "31454-17.htm";
                break;
            }
            case "6887": {
                takeItems(player, 7254, 1000L);
                giveItems(player, qs.getInt(event), 1L);
                htmltext = "31454-17.htm";
                break;
            }
            case "7580": {
                takeItems(player, 7254, 1000L);
                giveItems(player, qs.getInt(event), 1L);
                htmltext = "31454-17.htm";
                break;
            }
            case "6891": {
                takeItems(player, 7254, 1000L);
                giveItems(player, qs.getInt(event), 1L);
                htmltext = "31454-17.htm";
                break;
            }
            case "6893": {
                takeItems(player, 7254, 1000L);
                giveItems(player, qs.getInt(event), 1L);
                htmltext = "31454-17.htm";
                break;
            }
            case "6895": {
                takeItems(player, 7254, 1000L);
                giveItems(player, qs.getInt(event), 1L);
                htmltext = "31454-17.htm";
                break;
            }
            case "6897": {
                takeItems(player, 7254, 1000L);
                giveItems(player, qs.getInt(event), 1L);
                htmltext = "31454-17.htm";
                break;
            }
            case "6899": {
                takeItems(player, 7254, 1000L);
                giveItems(player, qs.getInt(event), 1L);
                htmltext = "31454-17.htm";
                break;
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player talker) {
        final QuestState qs = this.getQuestState(talker, true);
        String htmltext = getNoQuestMsg(talker);
        switch (npc.getId()) {
            case 31453: {
                if (qs.isCreated()) {
                    if (qs.getPlayer().getLevel() >= 74 && qs.getPlayer().getLevel() <= 80) {
                        htmltext = "31453-1.htm";
                        break;
                    }
                    htmltext = "31453-12.htm";
                    break;
                }
                else if (qs.getCond() == 1) {
                    if (getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[0]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[1]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[2]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[3]) >= 1L) {
                        htmltext = "31453-15.htm";
                        break;
                    }
                    htmltext = "31453-14.htm";
                    break;
                }
                else {
                    if (qs.getCond() == 2) {
                        htmltext = "31453-17.htm";
                        break;
                    }
                    break;
                }
                break;
            }
            case 31452: {
                if (qs.getCond() == 1) {
                    if (getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[0]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[1]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[2]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[3]) == 1L) {
                        htmltext = "31452-01.html";
                        break;
                    }
                    if (getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[0]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[1]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[2]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[3]) > 1L) {
                        htmltext = "31452-02.html";
                        break;
                    }
                    break;
                }
                else {
                    if (qs.getCond() == 2) {
                        htmltext = "31452-02.html";
                        break;
                    }
                    break;
                }
                break;
            }
            case 31454: {
                if (getQuestItemsCount(talker, 7254) >= 1000L) {
                    if (getQuestItemsCount(talker, 7255) >= 1L) {
                        if (getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[0]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[1]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[2]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[3]) >= 1L) {
                            htmltext = "31454-4.htm";
                            break;
                        }
                        if (getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[0]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[1]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[2]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[3]) > 1L) {
                            htmltext = "31454-8.htm";
                            break;
                        }
                        htmltext = "31454-12.htm";
                        break;
                    }
                    else {
                        if (getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[0]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[1]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[2]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[3]) >= 1L) {
                            htmltext = "31454-3.htm";
                            break;
                        }
                        if (getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[0]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[1]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[2]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[3]) > 1L) {
                            htmltext = "31454-7.htm";
                            break;
                        }
                        htmltext = "31454-11.htm";
                        break;
                    }
                }
                else if (getQuestItemsCount(talker, 7255) >= 1L) {
                    if (getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[0]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[1]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[2]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[3]) >= 1L) {
                        htmltext = "31454-2.htm";
                        break;
                    }
                    if (getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[0]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[1]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[2]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[3]) > 1L) {
                        htmltext = "31454-6.htm";
                        break;
                    }
                    htmltext = "31454-10.htm";
                    break;
                }
                else {
                    if (getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[0]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[1]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[2]) >= 1L && getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[3]) >= 1L) {
                        htmltext = "31454-1.htm";
                        break;
                    }
                    if (getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[0]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[1]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[2]) + getQuestItemsCount(talker, Q00620_FourGoblets.GOBLETS[3]) > 1L) {
                        htmltext = "31454-5.htm";
                        break;
                    }
                    htmltext = "31454-9.htm";
                    break;
                }
                break;
            }
            case 31921: {
                htmltext = "31921-E.htm";
                break;
            }
            case 31922: {
                htmltext = "31922-E.htm";
                break;
            }
            case 31923: {
                htmltext = "31923-E.htm";
                break;
            }
            case 31924: {
                htmltext = "31924-E.htm";
                break;
            }
            case 31919: {
                htmltext = "31919-1.htm";
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState qs = killer.getQuestState(this.getName());
        final Player partyMember = this.getRandomPartyMember(killer, 3);
        final int npcId = npc.getId();
        if (qs != null && qs.getCond() > 0 && npcId >= 18120 && npcId <= 18256) {
            if (Rnd.get(100) < 15) {
                giveItems(killer, 7255, 1L);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
            }
            if (getQuestItemsCount(killer, 7261) < 1L) {
                giveItems(killer, 7261, 1L);
            }
            if (getQuestItemsCount(killer, 7254) < 1000L) {
                giveItems(killer, 7254, 1L);
            }
        }
        switch (npc.getId()) {
            case 25339: {
                if (partyMember == null) {
                    return null;
                }
                if (!MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)partyMember, 1000) && getQuestItemsCount(partyMember, Q00620_FourGoblets.GOBLETS[0]) < 1L) {
                    giveItems(partyMember, Q00620_FourGoblets.GOBLETS[0], 1L);
                    break;
                }
                break;
            }
            case 25342: {
                if (partyMember == null) {
                    return null;
                }
                if (!MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)partyMember, 1000) && getQuestItemsCount(partyMember, Q00620_FourGoblets.GOBLETS[1]) < 1L) {
                    giveItems(partyMember, Q00620_FourGoblets.GOBLETS[1], 1L);
                    break;
                }
                break;
            }
            case 25346: {
                if (partyMember == null) {
                    return null;
                }
                if (!MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)partyMember, 1000) && getQuestItemsCount(partyMember, Q00620_FourGoblets.GOBLETS[2]) < 1L) {
                    giveItems(partyMember, Q00620_FourGoblets.GOBLETS[2], 1L);
                    break;
                }
                break;
            }
            case 25349: {
                if (partyMember == null) {
                    return null;
                }
                if (!MathUtil.isInsideRadius3D((ILocational)npc, (ILocational)partyMember, 1000) && getQuestItemsCount(partyMember, Q00620_FourGoblets.GOBLETS[3]) < 1L) {
                    giveItems(partyMember, Q00620_FourGoblets.GOBLETS[3], 1L);
                    break;
                }
                break;
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    static {
        NPCS = new int[] { 31453, 31452, 31454, 31921, 31922, 31923, 31924, 31919, 31920 };
        GOBLETS = new int[] { 7256, 7257, 7258, 7259 };
        QI = new int[] { 7256, 7257, 7258, 7259 };
    }
}
