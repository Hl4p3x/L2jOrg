// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00358_IllegitimateChildOfTheGoddess;

import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00358_IllegitimateChildOfTheGoddess extends Quest
{
    private static final int OLTRAN = 30862;
    private static final int SNAKE_SCALE = 5868;
    private static final int MIN_LEVEL = 63;
    private static final int SNAKE_SCALE_COUNT = 108;
    private static final int[] REWARDS;
    private static final IntMap<Double> MOBS;
    
    public Q00358_IllegitimateChildOfTheGoddess() {
        super(358);
        this.addStartNpc(30862);
        this.addTalkId(30862);
        this.addKillId((IntCollection)Q00358_IllegitimateChildOfTheGoddess.MOBS.keySet());
        this.registerQuestItems(new int[] { 5868 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        if (st == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "30862-02.htm":
            case "30862-03.htm": {
                htmltext = event;
                break;
            }
            case "30862-04.htm": {
                st.startQuest();
                htmltext = event;
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        final QuestState st = this.getRandomPartyMemberState(player, 1, 3, npc);
        if (st != null && giveItemRandomly(player, npc, 5868, 1L, 108L, (double)Q00358_IllegitimateChildOfTheGoddess.MOBS.get(npc.getId()), true)) {
            st.setCond(2, true);
        }
        return super.onKill(npc, player, isSummon);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        if (st.isCreated()) {
            htmltext = ((player.getLevel() >= 63) ? "30862-01.htm" : "30862-05.html");
        }
        else if (st.isStarted()) {
            if (getQuestItemsCount(player, 5868) < 108L) {
                htmltext = "30862-06.html";
            }
            else {
                rewardItems(player, Q00358_IllegitimateChildOfTheGoddess.REWARDS[getRandom(Q00358_IllegitimateChildOfTheGoddess.REWARDS.length)], 1L);
                st.exitQuest(true, true);
                htmltext = "30862-07.html";
            }
        }
        return htmltext;
    }
    
    static {
        REWARDS = new int[] { 4975, 4973, 4974, 4939, 4937, 4938, 4936, 4980 };
        (MOBS = (IntMap)new HashIntMap()).put(20672, (Object)0.71);
        Q00358_IllegitimateChildOfTheGoddess.MOBS.put(20673, (Object)0.74);
    }
}
