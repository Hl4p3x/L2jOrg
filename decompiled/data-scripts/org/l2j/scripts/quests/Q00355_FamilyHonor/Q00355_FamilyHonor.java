// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00355_FamilyHonor;

import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.quest.Quest;

public final class Q00355_FamilyHonor extends Quest
{
    private static final int GALIBREDO = 30181;
    private static final int PATRIN = 30929;
    private static final int GALFREDO_ROMERS_BUST = 4252;
    private static final int SCULPTOR_BERONA = 4350;
    private static final int ANCIENT_STATUE_PROTOTYPE = 4351;
    private static final int ANCIENT_STATUE_ORIGINAL = 4352;
    private static final int ANCIENT_STATUE_REPLICA = 4353;
    private static final int ANCIENT_STATUE_FORGERY = 4354;
    private static final int MIN_LEVEL = 36;
    private static final IntMap<DropInfo> MOBS;
    
    public Q00355_FamilyHonor() {
        super(355);
        this.addStartNpc(30181);
        this.addTalkId(new int[] { 30181, 30929 });
        this.addKillId((IntCollection)Q00355_FamilyHonor.MOBS.keySet());
        this.registerQuestItems(new int[] { 4252 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "30181-02.htm":
            case "30181-09.html":
            case "30929-01.html":
            case "30929-02.html": {
                htmltext = event;
                break;
            }
            case "30181-03.htm": {
                qs.startQuest();
                htmltext = event;
                break;
            }
            case "30181-06.html": {
                final long galfredoRomersBustCount = getQuestItemsCount(player, 4252);
                if (galfredoRomersBustCount < 1L) {
                    htmltext = event;
                    break;
                }
                if (galfredoRomersBustCount >= 100L) {
                    this.giveAdena(player, galfredoRomersBustCount * 20L, true);
                    takeItems(player, 4252, -1L);
                    htmltext = "30181-07.html";
                    break;
                }
                this.giveAdena(player, galfredoRomersBustCount * 20L, true);
                takeItems(player, 4252, -1L);
                htmltext = "30181-08.html";
                break;
            }
            case "30181-10.html": {
                final long galfredoRomersBustCount = getQuestItemsCount(player, 4252);
                if (galfredoRomersBustCount > 0L) {
                    this.giveAdena(player, galfredoRomersBustCount * 120L, true);
                }
                takeItems(player, 4252, -1L);
                qs.exitQuest(true, true);
                htmltext = event;
                break;
            }
            case "30929-03.html": {
                final int random = Rnd.get(100);
                if (hasQuestItems(player, 4350)) {
                    if (random < 2) {
                        giveItems(player, 4351, 1L);
                        htmltext = event;
                    }
                    else if (random < 32) {
                        giveItems(player, 4352, 1L);
                        htmltext = "30929-04.html";
                    }
                    else if (random < 62) {
                        giveItems(player, 4353, 1L);
                        htmltext = "30929-05.html";
                    }
                    else if (random < 77) {
                        giveItems(player, 4354, 1L);
                        htmltext = "30929-06.html";
                    }
                    else {
                        htmltext = "30929-07.html";
                    }
                    takeItems(player, 4350, 1L);
                    break;
                }
                htmltext = "30929-08.html";
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
        final DropInfo info = (DropInfo)Q00355_FamilyHonor.MOBS.get(npc.getId());
        final int random = Rnd.get(1000);
        if (random < info.getFirstChance()) {
            giveItemRandomly(killer, npc, 4252, 1L, 0L, 1.0, true);
        }
        else if (random < info.getSecondChance()) {
            giveItemRandomly(killer, npc, 4350, 1L, 0L, 1.0, true);
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        if (qs.isCreated()) {
            htmltext = ((player.getLevel() >= 36) ? "30181-01.htm" : "30181-04.html");
        }
        else if (qs.isStarted()) {
            if (npc.getId() == 30181) {
                if (hasQuestItems(player, 4350)) {
                    htmltext = "30181-11.html";
                }
                else {
                    htmltext = "30181-05.html";
                }
            }
            else {
                htmltext = "30929-01.html";
            }
        }
        return htmltext;
    }
    
    static {
        (MOBS = (IntMap)new HashIntMap()).put(20767, (Object)new DropInfo(560, 684));
        Q00355_FamilyHonor.MOBS.put(20768, (Object)new DropInfo(530, 650));
        Q00355_FamilyHonor.MOBS.put(20769, (Object)new DropInfo(420, 516));
        Q00355_FamilyHonor.MOBS.put(20770, (Object)new DropInfo(440, 560));
    }
    
    private static final class DropInfo
    {
        public final int _firstChance;
        public final int _secondChance;
        
        public DropInfo(final int firstChance, final int secondChance) {
            this._firstChance = firstChance;
            this._secondChance = secondChance;
        }
        
        public int getFirstChance() {
            return this._firstChance;
        }
        
        public int getSecondChance() {
            return this._secondChance;
        }
    }
}
