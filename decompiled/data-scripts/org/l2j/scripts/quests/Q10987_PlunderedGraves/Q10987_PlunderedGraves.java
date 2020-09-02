// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10987_PlunderedGraves;

import java.util.Objects;
import java.util.HashSet;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import java.util.Set;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;

public class Q10987_PlunderedGraves extends Quest
{
    private static final int NEWBIE_GUIDE = 30602;
    private static final int USKA = 30560;
    private static final int KASHA_WOLF = 20475;
    private static final int KASHA_TIMBER_WOLF = 20477;
    private static final int GOBLIN_TOMB_RAIDER = 20319;
    private static final int RAKECLAW_IMP_HUNTER = 20312;
    private static final ItemHolder SOE_TO_USKA;
    private static final ItemHolder SOE_NOVICE;
    private static final ItemHolder RING_NOVICE;
    private static final ItemHolder EARRING_NOVICE;
    private static final ItemHolder NECKLACE_NOVICE;
    private static final int MAX_LEVEL = 20;
    private static final String KILL_COUNT_VAR = "KillCount";
    
    public Q10987_PlunderedGraves() {
        super(10987);
        this.addStartNpc(30602);
        this.addTalkId(new int[] { 30602, 30560 });
        this.addKillId(new int[] { 20475, 20477, 20319, 20312 });
        this.addCondMaxLevel(20, "no_lvl.html");
        this.setQuestNameNpcStringId(NpcStringId.LV_2_20_PLUNDERED_GRAVES);
    }
    
    public boolean checkPartyMember(final Player member, final Npc npc) {
        final QuestState qs = this.getQuestState(member, false);
        return qs != null && qs.isStarted();
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "TELEPORT_TO_HUNTING_GROUND": {
                player.teleToLocation(-39527, -117654, -1840);
                break;
            }
            case "30602-02.htm": {
                qs.startQuest();
                htmltext = event;
                break;
            }
            case "30560-02.htm": {
                if (qs.isStarted()) {
                    addExpAndSp(player, 260000L, 6000);
                    giveItems(player, Q10987_PlunderedGraves.SOE_NOVICE);
                    giveItems(player, Q10987_PlunderedGraves.RING_NOVICE);
                    giveItems(player, Q10987_PlunderedGraves.EARRING_NOVICE);
                    giveItems(player, Q10987_PlunderedGraves.NECKLACE_NOVICE);
                    qs.exitQuest(false, true);
                    htmltext = event;
                    break;
                }
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState qs = this.getQuestState(killer, false);
        if (qs != null && qs.isCond(1)) {
            final int killCount = qs.getInt("KillCount") + 1;
            if (killCount < 20) {
                qs.set("KillCount", killCount);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                this.sendNpcLogList(killer);
            }
            else {
                qs.setCond(2, true);
                qs.unset("KillCount");
                killer.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage("You hunted all monsters.#Use the Scroll of Escape in you inventory.", 5000) });
                giveItems(killer, Q10987_PlunderedGraves.SOE_TO_USKA);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCond(1)) {
            final Set<NpcLogListHolder> holder = new HashSet<NpcLogListHolder>();
            holder.add(new NpcLogListHolder(NpcStringId.TRACK_DOWN_GRAVE_ROBBERS.getId(), true, qs.getInt("KillCount")));
            return holder;
        }
        return (Set<NpcLogListHolder>)super.getNpcLogList(player);
    }
    
    public String onTalk(final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, true);
        String htmltext = getNoQuestMsg(player);
        if (Objects.isNull(qs)) {
            return htmltext;
        }
        if (qs.isCreated()) {
            htmltext = "30602-01.htm";
        }
        else if (qs.isStarted()) {
            switch (npc.getId()) {
                case 30602: {
                    if (qs.isCond(1)) {
                        htmltext = "30602-02.htm";
                        break;
                    }
                    break;
                }
                case 30560: {
                    if (qs.isCond(2)) {
                        htmltext = "30560.htm";
                        break;
                    }
                    break;
                }
            }
        }
        else if (qs.isCompleted() && npc.getId() == 30602) {
            htmltext = getAlreadyCompletedMsg(player);
        }
        return htmltext;
    }
    
    static {
        SOE_TO_USKA = new ItemHolder(91649, 1L);
        SOE_NOVICE = new ItemHolder(10650, 10L);
        RING_NOVICE = new ItemHolder(49041, 2L);
        EARRING_NOVICE = new ItemHolder(49040, 2L);
        NECKLACE_NOVICE = new ItemHolder(49039, 1L);
    }
}
