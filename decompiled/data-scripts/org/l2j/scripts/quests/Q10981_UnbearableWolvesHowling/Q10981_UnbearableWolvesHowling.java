// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10981_UnbearableWolvesHowling;

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

public class Q10981_UnbearableWolvesHowling extends Quest
{
    private static final int NEWBIE_GUIDE = 30598;
    private static final int JACKSON = 30002;
    private static final ItemHolder SOE_TO_JACKSON;
    private static final ItemHolder SOE_NOVICE;
    private static final ItemHolder RING_NOVICE;
    private static final ItemHolder EARRING_NOVICE;
    private static final ItemHolder NECKLACE_NOVICE;
    private static final int BEARDED_KELTIR = 20481;
    private static final int WOLF = 20120;
    private static final int MAX_LEVEL = 20;
    private static final String KILL_COUNT_VAR = "KillCount";
    
    public Q10981_UnbearableWolvesHowling() {
        super(10981);
        this.addStartNpc(30598);
        this.addTalkId(new int[] { 30598, 30002 });
        this.addKillId(new int[] { 20481, 20120 });
        this.addCondMaxLevel(20, "no_lvl.html");
        this.setQuestNameNpcStringId(NpcStringId.LV_2_20_UNBEARABLE_WOLVES_HOWLING);
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
                player.teleToLocation(-90050, 241763, -3560);
                break;
            }
            case "30598-02.htm": {
                qs.startQuest();
                htmltext = event;
                break;
            }
            case "30002-02.htm": {
                if (qs.isStarted()) {
                    addExpAndSp(player, 260000L, 6000);
                    giveItems(player, Q10981_UnbearableWolvesHowling.SOE_NOVICE);
                    giveItems(player, Q10981_UnbearableWolvesHowling.RING_NOVICE);
                    giveItems(player, Q10981_UnbearableWolvesHowling.EARRING_NOVICE);
                    giveItems(player, Q10981_UnbearableWolvesHowling.NECKLACE_NOVICE);
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
                killer.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage("You hunted all monsters.#Use the Scroll of Escape in you inventory to return to Trader Jackson", 5000) });
                giveItems(killer, Q10981_UnbearableWolvesHowling.SOE_TO_JACKSON);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCond(1)) {
            final Set<NpcLogListHolder> holder = new HashSet<NpcLogListHolder>();
            holder.add(new NpcLogListHolder(NpcStringId.KILL_WOLVES_AND_BEARDED_KELTIRS.getId(), true, qs.getInt("KillCount")));
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
            htmltext = "30598-01.htm";
        }
        else if (qs.isStarted()) {
            switch (npc.getId()) {
                case 30598: {
                    if (qs.isCond(1)) {
                        htmltext = "30598-02.htm";
                        break;
                    }
                    break;
                }
                case 30002: {
                    if (qs.isCond(2)) {
                        htmltext = "30002.htm";
                        break;
                    }
                    break;
                }
            }
        }
        else if (qs.isCompleted() && npc.getId() == 30598) {
            htmltext = getAlreadyCompletedMsg(player);
        }
        return htmltext;
    }
    
    static {
        SOE_TO_JACKSON = new ItemHolder(91646, 1L);
        SOE_NOVICE = new ItemHolder(10650, 10L);
        RING_NOVICE = new ItemHolder(49041, 2L);
        EARRING_NOVICE = new ItemHolder(49040, 2L);
        NECKLACE_NOVICE = new ItemHolder(49039, 1L);
    }
}
