// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00370_AnElderSowsSeeds;

import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.HashIntIntMap;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00370_AnElderSowsSeeds extends Quest
{
    private static final int CASIAN = 30612;
    private static final int SPELLBOOK_PAGE = 5916;
    private static final int CHAPTER_OF_FIRE = 5917;
    private static final int CHAPTER_OF_WATER = 5918;
    private static final int CHAPTER_OF_WIND = 5919;
    private static final int CHAPTER_OF_EARTH = 5920;
    private static final int MIN_LEVEL = 28;
    private static final IntIntMap MOBS1;
    private static final IntMap<Double> MOBS2;
    
    public Q00370_AnElderSowsSeeds() {
        super(370);
        this.addStartNpc(30612);
        this.addTalkId(30612);
        this.addKillId((IntCollection)Q00370_AnElderSowsSeeds.MOBS1.keySet());
        this.addKillId((IntCollection)Q00370_AnElderSowsSeeds.MOBS2.keySet());
    }
    
    public boolean checkPartyMember(final Player member, final Npc npc) {
        final QuestState st = this.getQuestState(member, false);
        return st != null && st.isStarted();
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        if (st == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "30612-02.htm":
            case "30612-03.htm":
            case "30612-06.html":
            case "30612-07.html":
            case "30612-09.html": {
                htmltext = event;
                break;
            }
            case "30612-04.htm": {
                st.startQuest();
                htmltext = event;
                break;
            }
            case "REWARD": {
                if (!st.isStarted()) {
                    break;
                }
                if (this.exchangeChapters(player, false)) {
                    htmltext = "30612-08.html";
                    break;
                }
                htmltext = "30612-11.html";
                break;
            }
            case "30612-10.html": {
                if (st.isStarted()) {
                    this.exchangeChapters(player, true);
                    st.exitQuest(true, true);
                    htmltext = event;
                    break;
                }
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        final int npcId = npc.getId();
        if (Q00370_AnElderSowsSeeds.MOBS1.containsKey(npcId)) {
            if (Rnd.get(100) < Q00370_AnElderSowsSeeds.MOBS1.get(npcId)) {
                final Player luckyPlayer = this.getRandomPartyMember(player, npc);
                if (luckyPlayer != null) {
                    giveItemRandomly(luckyPlayer, npc, 5916, 1L, 0L, 1.0, true);
                }
            }
        }
        else {
            final QuestState st = this.getRandomPartyMemberState(player, -1, 3, npc);
            if (st != null) {
                giveItemRandomly(st.getPlayer(), npc, 5916, 1L, 0L, (double)Q00370_AnElderSowsSeeds.MOBS2.get(npcId), true);
            }
        }
        return super.onKill(npc, player, isSummon);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        if (st.isCreated()) {
            htmltext = ((player.getLevel() >= 28) ? "30612-01.htm" : "30612-05.html");
        }
        else if (st.isStarted()) {
            htmltext = "30612-06.html";
        }
        return htmltext;
    }
    
    private final boolean exchangeChapters(final Player player, final boolean takeAllItems) {
        final long waterChapters = getQuestItemsCount(player, 5918);
        final long earthChapters = getQuestItemsCount(player, 5920);
        final long windChapters = getQuestItemsCount(player, 5919);
        final long fireChapters = getQuestItemsCount(player, 5917);
        long minCount = waterChapters;
        if (earthChapters < minCount) {
            minCount = earthChapters;
        }
        if (windChapters < minCount) {
            minCount = windChapters;
        }
        if (fireChapters < minCount) {
            minCount = fireChapters;
        }
        if (minCount > 0L) {
            this.giveAdena(player, minCount * 3600L, true);
        }
        final long countToTake = takeAllItems ? -1L : minCount;
        takeItems(player, (int)countToTake, new int[] { 5918, 5920, 5919, 5917 });
        return minCount > 0L;
    }
    
    static {
        MOBS1 = (IntIntMap)new HashIntIntMap();
        MOBS2 = (IntMap)new HashIntMap();
        Q00370_AnElderSowsSeeds.MOBS1.put(20082, 9);
        Q00370_AnElderSowsSeeds.MOBS1.put(20086, 9);
        Q00370_AnElderSowsSeeds.MOBS1.put(20090, 22);
        Q00370_AnElderSowsSeeds.MOBS2.put(20084, (Object)0.101);
        Q00370_AnElderSowsSeeds.MOBS2.put(20089, (Object)0.1);
    }
}
