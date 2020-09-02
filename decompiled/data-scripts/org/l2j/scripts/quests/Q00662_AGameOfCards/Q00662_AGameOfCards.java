// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00662_AGameOfCards;

import io.github.joealisson.primitive.HashIntIntMap;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import java.util.ArrayList;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00662_AGameOfCards extends Quest
{
    private static final int KLUMP = 30845;
    private static final int RED_GEM = 8765;
    private static final int ZIGGOS_GEMSTONE = 8868;
    private static final int MIN_LEVEL = 61;
    private static final int REQUIRED_CHIP_COUNT = 50;
    private static final IntIntMap MONSTERS;
    
    public Q00662_AGameOfCards() {
        super(662);
        this.addStartNpc(30845);
        this.addTalkId(30845);
        this.addKillId((IntCollection)Q00662_AGameOfCards.MONSTERS.keySet());
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        String htmltext = null;
        if (st == null) {
            return htmltext;
        }
        switch (event) {
            case "30845-03.htm": {
                if (player.getLevel() >= 61) {
                    if (st.isCreated()) {
                        st.startQuest();
                    }
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30845-06.html":
            case "30845-08.html":
            case "30845-09.html":
            case "30845-09a.html":
            case "30845-09b.html":
            case "30845-10.html": {
                htmltext = event;
                break;
            }
            case "30845-07.html": {
                st.exitQuest(true, true);
                htmltext = event;
                break;
            }
            case "return": {
                htmltext = ((getQuestItemsCount(player, 8765) < 50L) ? "30845-04.html" : "30845-05.html");
                break;
            }
            case "30845-11.html": {
                if (getQuestItemsCount(player, 8765) >= 50L) {
                    int i1;
                    int i2;
                    int i3;
                    int i4;
                    int i5;
                    for (i1 = 0, i2 = 0, i3 = 0, i4 = 0, i5 = 0; i1 == i2 || i1 == i3 || i1 == i4 || i1 == i5 || i2 == i3 || i2 == i4 || i2 == i5 || i3 == i4 || i3 == i5 || i4 == i5; i1 = Rnd.get(70) + 1, i2 = Rnd.get(70) + 1, i3 = Rnd.get(70) + 1, i4 = Rnd.get(70) + 1, i5 = Rnd.get(70) + 1) {}
                    if (i1 >= 57) {
                        i1 -= 56;
                    }
                    else if (i1 >= 43) {
                        i1 -= 42;
                    }
                    else if (i1 >= 29) {
                        i1 -= 28;
                    }
                    else if (i1 >= 15) {
                        i1 -= 14;
                    }
                    if (i2 >= 57) {
                        i2 -= 56;
                    }
                    else if (i2 >= 43) {
                        i2 -= 42;
                    }
                    else if (i2 >= 29) {
                        i2 -= 28;
                    }
                    else if (i2 >= 15) {
                        i2 -= 14;
                    }
                    if (i3 >= 57) {
                        i3 -= 56;
                    }
                    else if (i3 >= 43) {
                        i3 -= 42;
                    }
                    else if (i3 >= 29) {
                        i3 -= 28;
                    }
                    else if (i3 >= 15) {
                        i3 -= 14;
                    }
                    if (i4 >= 57) {
                        i4 -= 56;
                    }
                    else if (i4 >= 43) {
                        i4 -= 42;
                    }
                    else if (i4 >= 29) {
                        i4 -= 28;
                    }
                    else if (i4 >= 15) {
                        i4 -= 14;
                    }
                    if (i5 >= 57) {
                        i5 -= 56;
                    }
                    else if (i5 >= 43) {
                        i5 -= 42;
                    }
                    else if (i5 >= 29) {
                        i5 -= 28;
                    }
                    else if (i5 >= 15) {
                        i5 -= 14;
                    }
                    st.set("v1", i4 * 1000000 + i3 * 10000 + i2 * 100 + i1);
                    st.set("ExMemoState", i5);
                    takeItems(player, 8765, 50L);
                    htmltext = event;
                    break;
                }
                break;
            }
            case "turncard1":
            case "turncard2":
            case "turncard3":
            case "turncard4":
            case "turncard5": {
                final int cond = st.getInt("v1");
                int i6 = st.getInt("ExMemoState");
                final int i7 = i6 % 100;
                int i8 = i6 / 100;
                i6 = cond % 100;
                final int i9 = cond % 10000 / 100;
                final int i10 = cond % 1000000 / 10000;
                final int i11 = cond % 100000000 / 1000000;
                switch (event) {
                    case "turncard1": {
                        if (i8 % 2 < 1) {
                            ++i8;
                        }
                        if (i8 % 32 < 31) {
                            st.set("ExMemoState", i8 * 100 + i7);
                            break;
                        }
                        break;
                    }
                    case "turncard2": {
                        if (i8 % 4 < 2) {
                            i8 += 2;
                        }
                        if (i8 % 32 < 31) {
                            st.set("ExMemoState", i8 * 100 + i7);
                            break;
                        }
                        break;
                    }
                    case "turncard3": {
                        if (i8 % 8 < 4) {
                            i8 += 4;
                        }
                        if (i8 % 32 < 31) {
                            st.set("ExMemoState", i8 * 100 + i7);
                            break;
                        }
                        break;
                    }
                    case "turncard4": {
                        if (i8 % 16 < 8) {
                            i8 += 8;
                        }
                        if (i8 % 32 < 31) {
                            st.set("ExMemoState", i8 * 100 + i7);
                            break;
                        }
                        break;
                    }
                    case "turncard5": {
                        if (i8 % 32 < 16) {
                            i8 += 16;
                        }
                        if (i8 % 32 < 31) {
                            st.set("ExMemoState", i8 * 100 + i7);
                            break;
                        }
                        break;
                    }
                }
                if (i8 % 32 < 31) {
                    htmltext = this.getHtml(player, "30845-12.html");
                }
                else if (i8 % 32 == 31) {
                    int i12 = 0;
                    int i13 = 0;
                    if (i6 >= 1 && i6 <= 14 && i9 >= 1 && i9 <= 14 && i10 >= 1 && i10 <= 14 && i11 >= 1 && i11 <= 14 && i7 >= 1 && i7 <= 14) {
                        if (i6 == i9) {
                            i12 += 10;
                            i13 += 8;
                        }
                        if (i6 == i10) {
                            i12 += 10;
                            i13 += 4;
                        }
                        if (i6 == i11) {
                            i12 += 10;
                            i13 += 2;
                        }
                        if (i6 == i7) {
                            i12 += 10;
                            ++i13;
                        }
                        if (i12 % 100 < 10) {
                            if (i13 % 16 < 8) {
                                if (i13 % 8 < 4 && i9 == i10) {
                                    i12 += 10;
                                    i13 += 4;
                                }
                                if (i13 % 4 < 2 && i9 == i11) {
                                    i12 += 10;
                                    i13 += 2;
                                }
                                if (i13 % 2 < 1 && i9 == i7) {
                                    i12 += 10;
                                    ++i13;
                                }
                            }
                        }
                        else if (i12 % 10 == 0 && i13 % 16 < 8) {
                            if (i13 % 8 < 4 && i9 == i10) {
                                ++i12;
                                i13 += 4;
                            }
                            if (i13 % 4 < 2 && i9 == i11) {
                                ++i12;
                                i13 += 2;
                            }
                            if (i13 % 2 < 1 && i9 == i7) {
                                ++i12;
                                ++i13;
                            }
                        }
                        if (i12 % 100 < 10) {
                            if (i13 % 8 < 4) {
                                if (i13 % 4 < 2 && i10 == i11) {
                                    i12 += 10;
                                    i13 += 2;
                                }
                                if (i13 % 2 < 1 && i10 == i7) {
                                    i12 += 10;
                                    ++i13;
                                }
                            }
                        }
                        else if (i12 % 10 == 0 && i13 % 8 < 4) {
                            if (i13 % 4 < 2 && i10 == i11) {
                                ++i12;
                                i13 += 2;
                            }
                            if (i13 % 2 < 1 && i10 == i7) {
                                ++i12;
                                ++i13;
                            }
                        }
                        if (i12 % 100 < 10) {
                            if (i13 % 4 < 2 && i13 % 2 < 1 && i11 == i7) {
                                i12 += 10;
                                ++i13;
                            }
                        }
                        else if (i12 % 10 == 0 && i13 % 4 < 2 && i13 % 2 < 1 && i11 == i7) {
                            ++i12;
                            ++i13;
                        }
                    }
                    if (i12 == 40) {
                        rewardItems(player, 8868, 43L);
                        rewardItems(player, 959, 3L);
                        rewardItems(player, 729, 1L);
                        st.set("ExMemoState", 0);
                        st.set("v1", 0);
                        htmltext = this.getHtml(player, "30845-13.html");
                    }
                    else if (i12 == 30) {
                        rewardItems(player, 959, 2L);
                        rewardItems(player, 951, 2L);
                        st.set("ExMemoState", 0);
                        st.set("v1", 0);
                        htmltext = this.getHtml(player, "30845-14.html");
                    }
                    else if (i12 == 21 || i12 == 12) {
                        rewardItems(player, 729, 1L);
                        rewardItems(player, 947, 2L);
                        rewardItems(player, 955, 1L);
                        st.set("ExMemoState", 0);
                        st.set("v1", 0);
                        htmltext = this.getHtml(player, "30845-15.html");
                    }
                    else if (i12 == 20) {
                        rewardItems(player, 951, 2L);
                        st.set("ExMemoState", 0);
                        st.set("v1", 0);
                        htmltext = this.getHtml(player, "30845-16.html");
                    }
                    else if (i12 == 11) {
                        rewardItems(player, 951, 1L);
                        st.set("ExMemoState", 0);
                        st.set("v1", 0);
                        htmltext = this.getHtml(player, "30845-17.html");
                    }
                    else if (i12 == 10) {
                        rewardItems(player, 956, 2L);
                        st.set("ExMemoState", 0);
                        st.set("v1", 0);
                        htmltext = this.getHtml(player, "30845-18.html");
                    }
                    else if (i12 == 0) {
                        st.set("ExMemoState", 0);
                        st.set("v1", 0);
                        htmltext = this.getHtml(player, "30845-19.html");
                    }
                }
                if (htmltext == null) {
                    break;
                }
                if (i8 % 2 < 1) {
                    htmltext = htmltext.replaceAll("FontColor1", "FFFF00");
                    htmltext = htmltext.replaceAll("Cell1", "?");
                }
                else {
                    htmltext = htmltext.replaceAll("FontColor1", "FF6F6F");
                    htmltext = setHtml(htmltext, i6, "Cell1");
                }
                if (i8 % 4 < 2) {
                    htmltext = htmltext.replaceAll("FontColor2", "FFFF00");
                    htmltext = htmltext.replaceAll("Cell2", "?");
                }
                else {
                    htmltext = htmltext.replaceAll("FontColor2", "FF6F6F");
                    htmltext = setHtml(htmltext, i9, "Cell2");
                }
                if (i8 % 8 < 4) {
                    htmltext = htmltext.replaceAll("FontColor3", "FFFF00");
                    htmltext = htmltext.replaceAll("Cell3", "?");
                }
                else {
                    htmltext = htmltext.replaceAll("FontColor3", "FF6F6F");
                    htmltext = setHtml(htmltext, i10, "Cell3");
                }
                if (i8 % 16 < 8) {
                    htmltext = htmltext.replaceAll("FontColor4", "FFFF00");
                    htmltext = htmltext.replaceAll("Cell4", "?");
                }
                else {
                    htmltext = htmltext.replaceAll("FontColor4", "FF6F6F");
                    htmltext = setHtml(htmltext, i11, "Cell4");
                }
                if (i8 % 32 < 16) {
                    htmltext = htmltext.replaceAll("FontColor5", "FFFF00");
                    htmltext = htmltext.replaceAll("Cell5", "?");
                    break;
                }
                htmltext = htmltext.replaceAll("FontColor5", "FF6F6F");
                htmltext = setHtml(htmltext, i7, "Cell5");
                break;
            }
            case "playagain": {
                htmltext = ((getQuestItemsCount(player, 8765) < 50L) ? "30845-21.html" : "30845-20.html");
                break;
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        switch (st.getState()) {
            case 0: {
                htmltext = ((player.getLevel() < 61) ? "30845-02.html" : "30845-01.htm");
                break;
            }
            case 1: {
                if (st.isCond(1)) {
                    htmltext = ((getQuestItemsCount(player, 8765) < 50L) ? "30845-04.html" : "30845-05.html");
                    break;
                }
                if (st.getInt("ExMemoState") != 0) {
                    final int i0 = st.getInt("v1");
                    int i2 = st.getInt("ExMemoState");
                    final int i3 = i2 % 100;
                    final int i4 = i2 / 100;
                    i2 = i0 % 100;
                    final int i5 = i0 % 10000 / 100;
                    final int i6 = i0 % 1000000 / 10000;
                    final int i7 = i0 % 100000000 / 1000000;
                    htmltext = this.getHtml(player, "30845-11a.html");
                    if (i4 % 2 < 1) {
                        htmltext = htmltext.replaceAll("FontColor1", "FFFF00");
                        htmltext = htmltext.replaceAll("Cell1", "?");
                    }
                    else {
                        htmltext = htmltext.replaceAll("FontColor1", "FF6F6F");
                        htmltext = setHtml(htmltext, i2, "Cell1");
                    }
                    if (i4 % 4 < 2) {
                        htmltext = htmltext.replaceAll("FontColor2", "FFFF00");
                        htmltext = htmltext.replaceAll("Cell2", "?");
                    }
                    else {
                        htmltext = htmltext.replaceAll("FontColor2", "FF6F6F");
                        htmltext = setHtml(htmltext, i5, "Cell2");
                    }
                    if (i4 % 8 < 4) {
                        htmltext = htmltext.replaceAll("FontColor3", "FFFF00");
                        htmltext = htmltext.replaceAll("Cell3", "?");
                    }
                    else {
                        htmltext = htmltext.replaceAll("FontColor3", "FF6F6F");
                        htmltext = setHtml(htmltext, i6, "Cell3");
                    }
                    if (i4 % 16 < 8) {
                        htmltext = htmltext.replaceAll("FontColor4", "FFFF00");
                        htmltext = htmltext.replaceAll("Cell4", "?");
                    }
                    else {
                        htmltext = htmltext.replaceAll("FontColor4", "FF6F6F");
                        htmltext = setHtml(htmltext, i7, "Cell4");
                    }
                    if (i4 % 32 < 16) {
                        htmltext = htmltext.replaceAll("FontColor5", "FFFF00");
                        htmltext = htmltext.replaceAll("Cell5", "?");
                    }
                    else {
                        htmltext = htmltext.replaceAll("FontColor5", "FF6F6F");
                        htmltext = setHtml(htmltext, i3, "Cell5");
                    }
                    break;
                }
                break;
            }
            case 2: {
                htmltext = getAlreadyCompletedMsg(player);
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final List<Player> players = new ArrayList<Player>();
        players.add(killer);
        players.add(killer);
        if (killer.isInParty()) {
            for (final Player member : killer.getParty().getMembers()) {
                if (this.getQuestState(member, false) != null) {
                    players.add(member);
                }
            }
        }
        final Player player = players.get(Rnd.get(players.size()));
        if (player != null && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, (WorldObject)npc, (WorldObject)player, false) && Q00662_AGameOfCards.MONSTERS.get(npc.getId()) < Rnd.get(1000)) {
            final QuestState st = this.getQuestState(player, false);
            if (st != null) {
                giveItemRandomly(st.getPlayer(), npc, 8765, 1L, 0L, (double)Q00662_AGameOfCards.MONSTERS.get(npc.getId()), true);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    private static String setHtml(final String htmltext, final int var, final String regex) {
        String replacement = null;
        switch (var) {
            case 1: {
                replacement = "!";
                break;
            }
            case 2: {
                replacement = "=";
                break;
            }
            case 3: {
                replacement = "T";
                break;
            }
            case 4: {
                replacement = "V";
                break;
            }
            case 5: {
                replacement = "O";
                break;
            }
            case 6: {
                replacement = "P";
                break;
            }
            case 7: {
                replacement = "S";
                break;
            }
            case 8: {
                replacement = "E";
                break;
            }
            case 9: {
                replacement = "H";
                break;
            }
            case 10: {
                replacement = "A";
                break;
            }
            case 11: {
                replacement = "R";
                break;
            }
            case 12: {
                replacement = "D";
                break;
            }
            case 13: {
                replacement = "I";
                break;
            }
            case 14: {
                replacement = "N";
                break;
            }
            default: {
                replacement = "ERROR";
                break;
            }
        }
        return htmltext.replaceAll(regex, replacement);
    }
    
    static {
        (MONSTERS = (IntIntMap)new HashIntIntMap()).put(20672, 357);
        Q00662_AGameOfCards.MONSTERS.put(20673, 357);
        Q00662_AGameOfCards.MONSTERS.put(20674, 583);
        Q00662_AGameOfCards.MONSTERS.put(20677, 435);
        Q00662_AGameOfCards.MONSTERS.put(20955, 358);
        Q00662_AGameOfCards.MONSTERS.put(20958, 283);
        Q00662_AGameOfCards.MONSTERS.put(20959, 455);
        Q00662_AGameOfCards.MONSTERS.put(20961, 365);
        Q00662_AGameOfCards.MONSTERS.put(20962, 348);
        Q00662_AGameOfCards.MONSTERS.put(20965, 457);
        Q00662_AGameOfCards.MONSTERS.put(20966, 493);
        Q00662_AGameOfCards.MONSTERS.put(20968, 418);
        Q00662_AGameOfCards.MONSTERS.put(20972, 350);
        Q00662_AGameOfCards.MONSTERS.put(20973, 453);
        Q00662_AGameOfCards.MONSTERS.put(21002, 315);
        Q00662_AGameOfCards.MONSTERS.put(21004, 320);
        Q00662_AGameOfCards.MONSTERS.put(21006, 335);
        Q00662_AGameOfCards.MONSTERS.put(21008, 462);
        Q00662_AGameOfCards.MONSTERS.put(21010, 397);
        Q00662_AGameOfCards.MONSTERS.put(21109, 507);
        Q00662_AGameOfCards.MONSTERS.put(21112, 552);
        Q00662_AGameOfCards.MONSTERS.put(21114, 587);
        Q00662_AGameOfCards.MONSTERS.put(21116, 812);
        Q00662_AGameOfCards.MONSTERS.put(20142, 232);
    }
}
