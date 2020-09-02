// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00356_DigUpTheSeaOfSpores;

import java.util.HashMap;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Map;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00356_DigUpTheSeaOfSpores extends Quest
{
    private static final int GAUEN = 30717;
    private static final int CARNIVORE_SPORE = 5865;
    private static final int HERBIVOROUS_SPORE = 5866;
    private static final int MIN_LEVEL = 43;
    private static final int ROTTING_TREE = 20558;
    private static final int SPORE_ZOMBIE = 20562;
    private static final Map<Integer, Double> MONSTER_DROP_CHANCES;
    
    public Q00356_DigUpTheSeaOfSpores() {
        super(356);
        this.addStartNpc(30717);
        this.addTalkId(30717);
        this.addKillId(new int[] { 20558, 20562 });
        this.registerQuestItems(new int[] { 5866, 5865 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "30717-02.htm":
            case "30717-03.htm":
            case "30717-04.htm":
            case "30717-10.html":
            case "30717-18.html": {
                htmltext = event;
                break;
            }
            case "30717-05.htm": {
                qs.startQuest();
                htmltext = event;
                break;
            }
            case "30717-09.html": {
                takeItems(player, 5865, -1L);
                takeItems(player, 5866, -1L);
                htmltext = event;
                break;
            }
            case "30717-11.html": {
                qs.exitQuest(true, true);
                htmltext = event;
                break;
            }
            case "30717-14.html": {
                qs.exitQuest(true, true);
                htmltext = event;
                break;
            }
            case "FINISH": {
                final int value = Rnd.get(100);
                int adena = 0;
                if (value < 20) {
                    adena = 3000;
                    htmltext = "30717-15.html";
                }
                else if (value < 70) {
                    adena = 1300;
                    htmltext = "30717-16.html";
                }
                else {
                    adena = 1300;
                    htmltext = "30717-17.html";
                }
                this.giveAdena(player, (long)adena, true);
                qs.exitQuest(true, true);
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState qs = this.getQuestState(killer, false);
        if (qs == null || !GameUtils.checkIfInRange(Config.ALT_PARTY_RANGE, (WorldObject)npc, (WorldObject)killer, true)) {
            return null;
        }
        final int dropItem = (npc.getId() == 20558) ? 5866 : 5865;
        final int otherItem = (dropItem == 5866) ? 5865 : 5866;
        if (giveItemRandomly(qs.getPlayer(), npc, dropItem, 1L, 100L, (double)Q00356_DigUpTheSeaOfSpores.MONSTER_DROP_CHANCES.get(npc.getId()), true)) {
            if (getQuestItemsCount(killer, otherItem) >= 100L) {
                qs.setCond(3);
            }
            else {
                qs.setCond(2);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        if (qs.isCreated()) {
            htmltext = ((player.getLevel() >= 43) ? "30717-01.htm" : "30717-06.htm");
        }
        else if (qs.isStarted()) {
            final boolean hasAllHerbSpores = getQuestItemsCount(player, 5866) >= 100L;
            final boolean hasAllCarnSpores = getQuestItemsCount(player, 5865) >= 100L;
            if (hasAllHerbSpores && hasAllCarnSpores) {
                htmltext = "30717-13.html";
            }
            else if (hasAllCarnSpores) {
                htmltext = "30717-12.html";
            }
            else if (hasAllHerbSpores) {
                htmltext = "30717-08.html";
            }
            else {
                htmltext = "30717-07.html";
            }
        }
        return htmltext;
    }
    
    static {
        (MONSTER_DROP_CHANCES = new HashMap<Integer, Double>()).put(20558, 0.73);
        Q00356_DigUpTheSeaOfSpores.MONSTER_DROP_CHANCES.put(20562, 0.94);
    }
}
