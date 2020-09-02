// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00360_PlunderTheirSupplies;

import io.github.joealisson.primitive.HashIntIntMap;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00360_PlunderTheirSupplies extends Quest
{
    private static final int COLEMAN = 30873;
    private static final int MIN_LVL = 52;
    private static final IntIntMap MONSTER_DROP_CHANCES;
    private static final int SUPPLY_ITEMS = 5872;
    
    public Q00360_PlunderTheirSupplies() {
        super(360);
        this.addStartNpc(30873);
        this.addTalkId(30873);
        this.addKillId((IntCollection)Q00360_PlunderTheirSupplies.MONSTER_DROP_CHANCES.keySet());
        this.registerQuestItems(new int[] { 5872 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        String htmltext = null;
        if (st == null) {
            return htmltext;
        }
        switch (event) {
            case "30873-03.htm":
            case "30873-09.html": {
                htmltext = event;
                break;
            }
            case "30873-04.htm": {
                st.startQuest();
                htmltext = event;
                break;
            }
            case "30873-10.html": {
                st.exitQuest(false, true);
                htmltext = event;
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isPet) {
        final QuestState st = this.getQuestState(killer, false);
        if (st == null || !GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, (WorldObject)npc, (WorldObject)killer, false)) {
            return super.onKill(npc, killer, isPet);
        }
        if (Rnd.get(100) < Q00360_PlunderTheirSupplies.MONSTER_DROP_CHANCES.get(npc.getId())) {
            giveItems(killer, 5872, 1L);
            playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
        }
        return super.onKill(npc, killer, isPet);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        switch (st.getState()) {
            case 0: {
                htmltext = ((player.getLevel() >= 52) ? "30873-02.htm" : "30873-01.html");
                break;
            }
            case 1: {
                final long supplyCount = getQuestItemsCount(player, 5872);
                if (supplyCount < 0L) {
                    htmltext = "30873-05.html";
                    break;
                }
                if (supplyCount >= 500L) {
                    this.giveAdena(player, 14000L, true);
                    takeItems(player, 5872, -1L);
                    htmltext = "30873-06.html";
                    break;
                }
                break;
            }
        }
        return htmltext;
    }
    
    static {
        (MONSTER_DROP_CHANCES = (IntIntMap)new HashIntIntMap()).put(20666, 50);
        Q00360_PlunderTheirSupplies.MONSTER_DROP_CHANCES.put(20669, 75);
    }
}
