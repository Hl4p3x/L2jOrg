// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00326_VanquishRemnants;

import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00326_VanquishRemnants extends Quest
{
    private static final int LEOPOLD = 30435;
    private static final int RED_CROSS_BADGE = 1359;
    private static final int BLUE_CROSS_BADGE = 1360;
    private static final int BLACK_CROSS_BADGE = 1361;
    private static final int BLACK_LION_MARK = 1369;
    private static final IntMap<int[]> MONSTERS;
    private static final int MIN_LVL = 21;
    
    public Q00326_VanquishRemnants() {
        super(326);
        this.addStartNpc(30435);
        this.addTalkId(30435);
        this.addKillId((IntCollection)Q00326_VanquishRemnants.MONSTERS.keySet());
        this.registerQuestItems(new int[] { 1359, 1360, 1361 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        String htmltext = null;
        if (st != null) {
            switch (event) {
                case "30435-03.htm": {
                    st.startQuest();
                    htmltext = event;
                    break;
                }
                case "30435-07.html": {
                    st.exitQuest(true, true);
                    htmltext = event;
                    break;
                }
                case "30435-08.html": {
                    htmltext = event;
                    break;
                }
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState st = this.getQuestState(killer, false);
        if (st != null && st.isStarted() && getRandom(100) < ((int[])Q00326_VanquishRemnants.MONSTERS.get(npc.getId()))[0]) {
            giveItems(killer, ((int[])Q00326_VanquishRemnants.MONSTERS.get(npc.getId()))[1], 1L);
            playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, true);
        String htmltext = null;
        switch (st.getState()) {
            case 0: {
                htmltext = ((player.getLevel() >= 21) ? "30435-02.htm" : "30435-01.htm");
                break;
            }
            case 1: {
                final long red_badges = getQuestItemsCount(player, 1359);
                final long blue_badges = getQuestItemsCount(player, 1360);
                final long black_badges = getQuestItemsCount(player, 1361);
                final long sum = red_badges + blue_badges + black_badges;
                if (sum > 0L) {
                    if (sum >= 100L && !hasQuestItems(player, 1369)) {
                        giveItems(player, 1369, 1L);
                    }
                    this.giveAdena(player, red_badges * 10L + blue_badges * 10L + black_badges * 12L + ((sum >= 10L) ? 1000 : 0), true);
                    takeItems(player, -1, new int[] { 1359, 1360, 1361 });
                    htmltext = ((sum >= 100L) ? (hasQuestItems(player, 1369) ? "30435-09.html" : "30435-06.html") : "30435-05.html");
                    break;
                }
                htmltext = "30435-04.html";
                break;
            }
        }
        return htmltext;
    }
    
    static {
        (MONSTERS = (IntMap)new HashIntMap()).put(20053, (Object)new int[] { 61, 1359 });
        Q00326_VanquishRemnants.MONSTERS.put(20058, (Object)new int[] { 61, 1359 });
        Q00326_VanquishRemnants.MONSTERS.put(20061, (Object)new int[] { 57, 1360 });
        Q00326_VanquishRemnants.MONSTERS.put(20063, (Object)new int[] { 63, 1360 });
        Q00326_VanquishRemnants.MONSTERS.put(20066, (Object)new int[] { 59, 1361 });
        Q00326_VanquishRemnants.MONSTERS.put(20436, (Object)new int[] { 55, 1360 });
        Q00326_VanquishRemnants.MONSTERS.put(20437, (Object)new int[] { 59, 1359 });
        Q00326_VanquishRemnants.MONSTERS.put(20438, (Object)new int[] { 60, 1361 });
        Q00326_VanquishRemnants.MONSTERS.put(20439, (Object)new int[] { 62, 1360 });
    }
}
