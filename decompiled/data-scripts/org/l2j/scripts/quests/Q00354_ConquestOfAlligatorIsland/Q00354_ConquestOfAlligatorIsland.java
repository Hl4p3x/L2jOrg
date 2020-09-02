// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00354_ConquestOfAlligatorIsland;

import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00354_ConquestOfAlligatorIsland extends Quest
{
    private static final int KLUCK = 30895;
    private static final int ALLIGATOR_TOOTH = 5863;
    private static final int MIN_LEVEL = 38;
    private static final IntMap<Double> MOB1;
    private static final IntIntMap MOB2;
    
    public Q00354_ConquestOfAlligatorIsland() {
        super(354);
        this.addStartNpc(30895);
        this.addTalkId(30895);
        this.addKillId((IntCollection)Q00354_ConquestOfAlligatorIsland.MOB1.keySet());
        this.addKillId((IntCollection)Q00354_ConquestOfAlligatorIsland.MOB2.keySet());
        this.registerQuestItems(new int[] { 5863 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        if (st == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "30895-04.html":
            case "30895-05.html":
            case "30895-09.html": {
                htmltext = event;
                break;
            }
            case "30895-02.html": {
                st.startQuest();
                htmltext = event;
                break;
            }
            case "ADENA": {
                final long count = getQuestItemsCount(player, 5863);
                if (count >= 400L) {
                    this.giveAdena(player, 2000L, true);
                    takeItems(player, 5863, -1L);
                    htmltext = "30895-06.html";
                    break;
                }
                htmltext = "30895-08.html";
                break;
            }
            case "30895-10.html": {
                st.exitQuest(true, true);
                htmltext = event;
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        final QuestState st = this.getRandomPartyMemberState(player, -1, 3, npc);
        if (st != null) {
            final int npcId = npc.getId();
            if (Q00354_ConquestOfAlligatorIsland.MOB1.containsKey(npcId)) {
                giveItemRandomly(st.getPlayer(), npc, 5863, 1L, 0L, (double)Q00354_ConquestOfAlligatorIsland.MOB1.get(npcId), true);
            }
            else {
                final int itemCount = (getRandom(100) < Q00354_ConquestOfAlligatorIsland.MOB2.get(npcId)) ? 2 : 1;
                giveItemRandomly(st.getPlayer(), npc, 5863, (long)itemCount, 0L, 1.0, true);
            }
        }
        return super.onKill(npc, player, isSummon);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        if (st.isCreated()) {
            htmltext = ((player.getLevel() >= 38) ? "30895-01.htm" : "30895-03.html");
        }
        else if (st.isStarted()) {
            htmltext = "30895-04.html";
        }
        return htmltext;
    }
    
    static {
        MOB1 = (IntMap)new HashIntMap();
        MOB2 = (IntIntMap)new HashIntIntMap();
        Q00354_ConquestOfAlligatorIsland.MOB1.put(20804, (Object)0.84);
        Q00354_ConquestOfAlligatorIsland.MOB1.put(20805, (Object)0.91);
        Q00354_ConquestOfAlligatorIsland.MOB1.put(20806, (Object)0.88);
        Q00354_ConquestOfAlligatorIsland.MOB1.put(20807, (Object)0.92);
        Q00354_ConquestOfAlligatorIsland.MOB2.put(20808, 14);
    }
}
