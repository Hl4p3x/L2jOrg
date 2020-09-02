// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10961_EffectiveTraining;

import java.util.HashSet;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import java.util.Set;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.QuestSound;
import java.util.Objects;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;

public final class Q10961_EffectiveTraining extends Quest
{
    private static final int NEWBIE_GUIDE = 34110;
    private static final int REAHEN = 34111;
    private static final int GREY_KELTIR = 21981;
    private static final int ELDER_GREY_KELTIR = 21982;
    private static final int BLACK_WOLF = 21983;
    private static final int ELDER_BLACK_WOLF = 21984;
    private static final ItemHolder SOE_TO_REAHEN;
    private static final ItemHolder SOE_NOVICE;
    private static final ItemHolder RING_NOVICE;
    private static final ItemHolder EARRING_NOVICE;
    private static final ItemHolder NECKLACE_NOVICE;
    private static final int MAX_LEVEL = 20;
    private static final String KILL_COUNT_VAR = "KillCount";
    
    public Q10961_EffectiveTraining() {
        super(10961);
        this.addStartNpc(34110);
        this.addTalkId(new int[] { 34110, 34111 });
        this.addKillId(new int[] { 21981, 21982, 21983, 21984 });
        this.addCondMaxLevel(20, "no_lvl.html");
        this.setQuestNameNpcStringId(NpcStringId.LV_2_20_EFFECTIVE_TRAINING);
    }
    
    public boolean checkPartyMember(final Player member, final Npc npc) {
        final QuestState qs = this.getQuestState(member, false);
        return qs != null && qs.isStarted();
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (Objects.isNull(qs)) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "TELEPORT_TO_HUNTING_GROUND": {
                player.teleToLocation(-120020, 55668, -1560);
                break;
            }
            case "34110-02.htm": {
                qs.startQuest();
                htmltext = event;
                break;
            }
            case "34111-02.htm": {
                if (qs.isStarted()) {
                    addExpAndSp(player, 260000L, 6000);
                    giveItems(player, Q10961_EffectiveTraining.SOE_NOVICE);
                    giveItems(player, Q10961_EffectiveTraining.RING_NOVICE);
                    giveItems(player, Q10961_EffectiveTraining.EARRING_NOVICE);
                    giveItems(player, Q10961_EffectiveTraining.NECKLACE_NOVICE);
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
                killer.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage("You hunted all monsters.#Use the Scroll of Escape in you inventory to return to Trader Reahen", 5000) });
                giveItems(killer, Q10961_EffectiveTraining.SOE_TO_REAHEN);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCond(1)) {
            final Set<NpcLogListHolder> holder = new HashSet<NpcLogListHolder>();
            holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_MONSTERS_ON_THE_HILL_OF_HOPE.getId(), true, qs.getInt("KillCount")));
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
            htmltext = "34110-01.htm";
        }
        else if (qs.isStarted()) {
            switch (npc.getId()) {
                case 34110: {
                    if (qs.isCond(1)) {
                        htmltext = "34110-02.htm";
                        break;
                    }
                    break;
                }
                case 34111: {
                    if (qs.isCond(2)) {
                        htmltext = "34111.htm";
                        break;
                    }
                    break;
                }
            }
        }
        else if (qs.isCompleted() && npc.getId() == 34110) {
            htmltext = getAlreadyCompletedMsg(player);
        }
        return htmltext;
    }
    
    static {
        SOE_TO_REAHEN = new ItemHolder(91917, 1L);
        SOE_NOVICE = new ItemHolder(10650, 10L);
        RING_NOVICE = new ItemHolder(49041, 2L);
        EARRING_NOVICE = new ItemHolder(49040, 2L);
        NECKLACE_NOVICE = new ItemHolder(49039, 1L);
    }
}
