// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00331_ArrowOfVengeance;

import io.github.joealisson.primitive.HashIntIntMap;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.model.quest.Quest;

public class Q00331_ArrowOfVengeance extends Quest
{
    private static final int BELTON = 30125;
    private static final int HARPY_FEATHER = 1452;
    private static final int MEDUSA_VENOM = 1453;
    private static final int WYRMS_TOOTH = 1454;
    private static final IntIntMap MONSTERS;
    private static final int MIN_LVL = 32;
    private static final int HARPY_FEATHER_ADENA = 6;
    private static final int MEDUSA_VENOM_ADENA = 7;
    private static final int WYRMS_TOOTH_ADENA = 9;
    private static final int BONUS = 1000;
    private static final int BONUS_COUNT = 10;
    
    public Q00331_ArrowOfVengeance() {
        super(331);
        this.addStartNpc(30125);
        this.addTalkId(30125);
        this.addKillId((IntCollection)Q00331_ArrowOfVengeance.MONSTERS.keySet());
        this.registerQuestItems(new int[] { 1452, 1453, 1454 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState st = this.getQuestState(player, false);
        String htmltext = null;
        if (st != null) {
            switch (event) {
                case "30125-03.htm": {
                    st.startQuest();
                    htmltext = event;
                    break;
                }
                case "30125-06.html": {
                    st.exitQuest(true, true);
                    htmltext = event;
                    break;
                }
                case "30125-07.html": {
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
                htmltext = ((player.getLevel() < 32) ? "30125-01.htm" : "30125-02.htm");
                break;
            }
            case 1: {
                final long harpyFeathers = getQuestItemsCount(player, 1452);
                final long medusaVenoms = getQuestItemsCount(player, 1453);
                final long wyrmsTeeth = getQuestItemsCount(player, 1454);
                if (harpyFeathers + medusaVenoms + wyrmsTeeth > 0L) {
                    this.giveAdena(player, harpyFeathers * 6L + medusaVenoms * 7L + wyrmsTeeth * 9L + ((harpyFeathers + medusaVenoms + wyrmsTeeth >= 10L) ? 1000 : 0), true);
                    takeItems(player, -1, new int[] { 1452, 1453, 1454 });
                    htmltext = "30125-05.html";
                    break;
                }
                htmltext = "30125-04.html";
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isPet) {
        final QuestState st = this.getQuestState(player, false);
        if (st != null && getRandom(100) < Q00331_ArrowOfVengeance.MONSTERS.get(npc.getId())) {
            switch (npc.getId()) {
                case 20145: {
                    giveItems(player, 1452, 1L);
                    break;
                }
                case 20158: {
                    giveItems(player, 1453, 1L);
                    break;
                }
                case 20176: {
                    giveItems(player, 1454, 1L);
                    break;
                }
            }
            playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
        }
        return super.onKill(npc, player, isPet);
    }
    
    static {
        (MONSTERS = (IntIntMap)new HashIntIntMap()).put(20145, 59);
        Q00331_ArrowOfVengeance.MONSTERS.put(20158, 61);
        Q00331_ArrowOfVengeance.MONSTERS.put(20176, 60);
    }
}
