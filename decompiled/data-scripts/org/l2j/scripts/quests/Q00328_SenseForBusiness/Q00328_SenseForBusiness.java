// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00328_SenseForBusiness;

import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.quest.Quest;

public class Q00328_SenseForBusiness extends Quest
{
    private static final int SARIEN = 30436;
    private static final IntMap<int[]> MONSTER_EYES;
    private static final IntIntMap MONSTER_BASILISKS;
    private static final int MONSTER_EYE_CARCASS = 1347;
    private static final int MONSTER_EYE_LENS = 1366;
    private static final int BASILISK_GIZZARD = 1348;
    private static final int MONSTER_EYE_CARCASS_ADENA = 2;
    private static final int MONSTER_EYE_LENS_ADENA = 10;
    private static final int BASILISK_GIZZARD_ADENA = 2;
    private static final int BONUS = 100;
    private static final int BONUS_COUNT = 10;
    private static final int MIN_LVL = 21;
    
    public Q00328_SenseForBusiness() {
        super(328);
        this.addStartNpc(30436);
        this.addTalkId(30436);
        this.addKillId((IntCollection)Q00328_SenseForBusiness.MONSTER_EYES.keySet());
        this.addKillId((IntCollection)Q00328_SenseForBusiness.MONSTER_BASILISKS.keySet());
        this.registerQuestItems(new int[] { 1347, 1366, 1348 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        String htmltext = null;
        if (st != null) {
            switch (event) {
                case "30436-03.htm": {
                    st.startQuest();
                    htmltext = event;
                    break;
                }
                case "30436-06.html": {
                    st.exitQuest(true, true);
                    htmltext = event;
                    break;
                }
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        switch (st.getState()) {
            case 0: {
                htmltext = ((player.getLevel() < 21) ? "30436-01.htm" : "30436-02.htm");
                break;
            }
            case 1: {
                final long carcass = getQuestItemsCount(player, 1347);
                final long lens = getQuestItemsCount(player, 1366);
                final long gizzards = getQuestItemsCount(player, 1348);
                if (carcass + lens + gizzards > 0L) {
                    this.giveAdena(player, carcass * 2L + lens * 10L + gizzards * 2L + ((carcass + lens + gizzards >= 10L) ? 100 : 0), true);
                    takeItems(player, -1, new int[] { 1347, 1366, 1348 });
                    htmltext = "30436-05.html";
                    break;
                }
                htmltext = "30436-04.html";
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isPet) {
        final QuestState st = this.getQuestState(player, false);
        if (st != null && st.isStarted()) {
            final int chance = getRandom(100);
            if (Q00328_SenseForBusiness.MONSTER_EYES.containsKey(npc.getId())) {
                if (chance < ((int[])Q00328_SenseForBusiness.MONSTER_EYES.get(npc.getId()))[0]) {
                    giveItems(player, 1347, 1L);
                    playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                }
                else if (chance < ((int[])Q00328_SenseForBusiness.MONSTER_EYES.get(npc.getId()))[1]) {
                    giveItems(player, 1366, 1L);
                    playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                }
            }
            else if (Q00328_SenseForBusiness.MONSTER_BASILISKS.containsKey(npc.getId()) && chance < Q00328_SenseForBusiness.MONSTER_BASILISKS.get(npc.getId())) {
                giveItems(player, 1348, 1L);
                playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
            }
        }
        return super.onKill(npc, player, isPet);
    }
    
    static {
        MONSTER_EYES = (IntMap)new HashIntMap();
        MONSTER_BASILISKS = (IntIntMap)new HashIntIntMap();
        Q00328_SenseForBusiness.MONSTER_EYES.put(20055, (Object)new int[] { 61, 62 });
        Q00328_SenseForBusiness.MONSTER_EYES.put(20059, (Object)new int[] { 61, 62 });
        Q00328_SenseForBusiness.MONSTER_EYES.put(20067, (Object)new int[] { 72, 74 });
        Q00328_SenseForBusiness.MONSTER_EYES.put(20068, (Object)new int[] { 78, 79 });
        Q00328_SenseForBusiness.MONSTER_BASILISKS.put(20070, 60);
        Q00328_SenseForBusiness.MONSTER_BASILISKS.put(20072, 63);
    }
}
