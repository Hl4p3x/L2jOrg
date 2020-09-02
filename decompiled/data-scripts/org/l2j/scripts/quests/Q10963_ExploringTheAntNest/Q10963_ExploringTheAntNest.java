// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10963_ExploringTheAntNest;

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
import org.l2j.gameserver.model.quest.Quest;

public class Q10963_ExploringTheAntNest extends Quest
{
    private static final int RAYMOND = 30289;
    private static final int ANT_LARVA = 20075;
    private static final int ANT = 20079;
    private static final int ANT_CAPTAIN = 20080;
    private static final int ANT_OVERSEER = 20081;
    private static final int ANT_RECRUIT = 20082;
    private static final int ANT_PATROL = 20084;
    private static final int ANT_GUARD = 20086;
    private static final int ANT_SOLDIER = 20087;
    private static final int ANT_WARRIOR_CAPTAIN = 20088;
    private static final int ANT_NOBLE = 20089;
    private static final int ANT_NOBLE_CAPTAIN = 20090;
    private static final String KILL_COUNT_VAR = "KillCount";
    private static final int MIN_LEVEL = 34;
    private static final int MAX_LEVEL = 37;
    
    public Q10963_ExploringTheAntNest() {
        super(10963);
        this.addStartNpc(30289);
        this.addTalkId(30289);
        this.addKillId(new int[] { 20075, 20079, 20080, 20081, 20082, 20084, 20086, 20087, 20088, 20089, 20090 });
        this.setQuestNameNpcStringId(NpcStringId.LV_34_37_EXPLORING_THE_ANT_NEST);
        this.addCondMinLevel(34, "no_lvl.html");
        this.addCondMaxLevel(37, "no_lvl.html");
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
            case "30289-01.htm": {
                htmltext = event;
                break;
            }
            case "30289-02.htm": {
                htmltext = event;
                break;
            }
            case "30289-03.htm": {
                qs.startQuest();
                htmltext = event;
                break;
            }
            case "30289-05.html": {
                if (qs.isStarted()) {
                    addExpAndSp(player, 3000000L, 75000);
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
            if (killCount < 500) {
                qs.set("KillCount", killCount);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                this.sendNpcLogList(killer);
            }
            else {
                qs.setCond(2, true);
                qs.unset("KillCount");
                killer.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(NpcStringId.MONSTERS_OF_THE_ANT_NEST_ARE_KILLED_NUSE_THE_TELEPORT_TO_GET_TO_HIGH_PRIEST_RAYMOND_IN_GLUDIO, 2, 5000, new String[0]) });
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCond(1)) {
            final Set<NpcLogListHolder> holder = new HashSet<NpcLogListHolder>();
            holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_ANT_NEST.getId(), true, qs.getInt("KillCount")));
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
            htmltext = "30289.htm";
        }
        else if (qs.isStarted()) {
            switch (npc.getId()) {
                case 30289: {
                    if (qs.isCond(2)) {
                        htmltext = "30289-04.html";
                        break;
                    }
                    break;
                }
            }
        }
        else if (qs.isCompleted() && npc.getId() == 30289) {
            htmltext = getAlreadyCompletedMsg(player);
        }
        return htmltext;
    }
}
