// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00369_CollectorOfJewels;

import io.github.joealisson.primitive.HashIntMap;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.model.holders.QuestItemHolder;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00369_CollectorOfJewels extends Quest
{
    private static final int NELL = 30376;
    private static final int FLARE_SHARD = 5882;
    private static final int FREEZING_SHARD = 5883;
    private static final int MIN_LEVEL = 25;
    private static final IntMap<QuestItemHolder> MOBS_DROP_CHANCES;
    
    public Q00369_CollectorOfJewels() {
        super(369);
        this.addStartNpc(30376);
        this.addTalkId(30376);
        this.addKillId((IntCollection)Q00369_CollectorOfJewels.MOBS_DROP_CHANCES.keySet());
        this.registerQuestItems(new int[] { 5882, 5883 });
    }
    
    public boolean checkPartyMember(final Player member, final Npc npc) {
        final QuestState st = this.getQuestState(member, false);
        return st != null && (st.isMemoState(1) || st.isMemoState(3));
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        if (st == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "30376-02.htm": {
                st.startQuest();
                st.setMemoState(1);
                htmltext = event;
                break;
            }
            case "30376-05.html": {
                htmltext = event;
                break;
            }
            case "30376-06.html": {
                if (st.isMemoState(2)) {
                    st.setMemoState(3);
                    st.setCond(3, true);
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30376-07.html": {
                st.exitQuest(true, true);
                htmltext = event;
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        final QuestItemHolder item = (QuestItemHolder)Q00369_CollectorOfJewels.MOBS_DROP_CHANCES.get(npc.getId());
        if (Rnd.get(100) < item.getChance()) {
            final Player luckyPlayer = this.getRandomPartyMember(player, npc);
            if (luckyPlayer != null) {
                final QuestState st = this.getQuestState(luckyPlayer, false);
                final int itemCount = st.isMemoState(1) ? 50 : 200;
                final int cond = st.isMemoState(1) ? 2 : 4;
                if (giveItemRandomly(luckyPlayer, npc, item.getId(), item.getCount(), (long)itemCount, 1.0, true) && this.getQuestItemsCount(luckyPlayer, new int[] { 5882, 5883 }) >= itemCount * 2) {
                    st.setCond(cond);
                }
            }
        }
        return super.onKill(npc, player, isSummon);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        if (st.isCreated()) {
            htmltext = ((player.getLevel() >= 25) ? "30376-01.htm" : "30376-03.html");
        }
        else if (st.isStarted()) {
            switch (st.getMemoState()) {
                case 1: {
                    if (this.getQuestItemsCount(player, new int[] { 5882, 5883 }) >= 100L) {
                        this.giveAdena(player, 3000L, true);
                        takeItems(player, -1, new int[] { 5882, 5883 });
                        st.setMemoState(2);
                        htmltext = "30376-04.html";
                        break;
                    }
                    htmltext = "30376-08.html";
                    break;
                }
                case 2: {
                    htmltext = "30376-09.html";
                    break;
                }
                case 3: {
                    if (this.getQuestItemsCount(player, new int[] { 5882, 5883 }) >= 400L) {
                        this.giveAdena(player, 12000L, true);
                        takeItems(player, -1, new int[] { 5882, 5883 });
                        st.exitQuest(true, true);
                        htmltext = "30376-10.html";
                        break;
                    }
                    htmltext = "30376-11.html";
                    break;
                }
            }
        }
        return htmltext;
    }
    
    static {
        (MOBS_DROP_CHANCES = (IntMap)new HashIntMap()).put(20609, (Object)new QuestItemHolder(5882, 75, 1L));
        Q00369_CollectorOfJewels.MOBS_DROP_CHANCES.put(20612, (Object)new QuestItemHolder(5882, 91, 1L));
        Q00369_CollectorOfJewels.MOBS_DROP_CHANCES.put(20749, (Object)new QuestItemHolder(5882, 100, 2L));
        Q00369_CollectorOfJewels.MOBS_DROP_CHANCES.put(20616, (Object)new QuestItemHolder(5883, 81, 1L));
        Q00369_CollectorOfJewels.MOBS_DROP_CHANCES.put(20619, (Object)new QuestItemHolder(5883, 87, 1L));
        Q00369_CollectorOfJewels.MOBS_DROP_CHANCES.put(20747, (Object)new QuestItemHolder(5883, 100, 2L));
    }
}
