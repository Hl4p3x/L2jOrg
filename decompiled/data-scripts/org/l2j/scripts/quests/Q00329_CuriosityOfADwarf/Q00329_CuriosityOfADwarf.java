// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00329_CuriosityOfADwarf;

import java.util.Arrays;
import io.github.joealisson.primitive.HashIntMap;
import java.util.Iterator;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00329_CuriosityOfADwarf extends Quest
{
    private static final int TRADER_ROLENTO = 30437;
    private static final int GOLEM_HEARTSTONE = 1346;
    private static final int BROKEN_HEARTSTONE = 1365;
    private static final int MIN_LEVEL = 33;
    private static final IntMap<List<ItemHolder>> MONSTER_DROPS;
    
    public Q00329_CuriosityOfADwarf() {
        super(329);
        this.addStartNpc(30437);
        this.addTalkId(30437);
        this.addKillId((IntCollection)Q00329_CuriosityOfADwarf.MONSTER_DROPS.keySet());
        this.registerQuestItems(new int[] { 1346, 1365 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        String htmltext = null;
        if (st == null) {
            return htmltext;
        }
        switch (event) {
            case "30437-03.htm": {
                if (st.isCreated()) {
                    st.startQuest();
                    htmltext = event;
                    break;
                }
                break;
            }
            case "30437-06.html": {
                st.exitQuest(true, true);
                htmltext = event;
                break;
            }
            case "30437-07.html": {
                htmltext = event;
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState st = this.getQuestState(killer, false);
        if (st != null && GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, (WorldObject)npc, (WorldObject)killer, true)) {
            final int rnd = getRandom(100);
            for (final ItemHolder drop : (List)Q00329_CuriosityOfADwarf.MONSTER_DROPS.get(npc.getId())) {
                if (rnd < drop.getCount()) {
                    giveItemRandomly(killer, npc, drop.getId(), 1L, 0L, 1.0, true);
                    break;
                }
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        switch (st.getState()) {
            case 0: {
                htmltext = ((player.getLevel() >= 33) ? "30437-02.htm" : "30437-01.htm");
                break;
            }
            case 1: {
                if (this.hasAtLeastOneQuestItem(player, this.getRegisteredItemIds())) {
                    final long broken = getQuestItemsCount(player, 1365);
                    final long golem = getQuestItemsCount(player, 1346);
                    this.giveAdena(player, broken * 5L + golem * 40L + ((broken + golem >= 700L) ? 700 : 1000), true);
                    takeItems(player, -1, this.getRegisteredItemIds());
                    htmltext = "30437-05.html";
                    break;
                }
                htmltext = "30437-04.html";
                break;
            }
        }
        return htmltext;
    }
    
    static {
        (MONSTER_DROPS = (IntMap)new HashIntMap()).put(20083, (Object)Arrays.asList(new ItemHolder(1346, 3L), new ItemHolder(1365, 54L)));
        Q00329_CuriosityOfADwarf.MONSTER_DROPS.put(20085, (Object)Arrays.asList(new ItemHolder(1346, 3L), new ItemHolder(1365, 58L)));
    }
}
