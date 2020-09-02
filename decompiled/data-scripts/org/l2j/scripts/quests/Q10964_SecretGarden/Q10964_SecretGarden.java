// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10964_SecretGarden;

import java.util.Objects;
import java.util.HashSet;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import java.util.Set;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.quest.Quest;

public class Q10964_SecretGarden extends Quest
{
    private static final int CAPTAIN_BATHIS = 30332;
    private static final int RAYMOND = 30289;
    private static final int HARPY = 20145;
    private static final int MEDUSA = 20158;
    private static final int WYRM = 20176;
    private static final int TURAK_BUGBEAR = 20248;
    private static final int TURAK_BUGBEAR_WARRIOR = 20249;
    private static final String KILL_COUNT_VAR = "KillCount";
    private static final int MAX_LEVEL = 34;
    private static final int MIN_LEVEL = 30;
    
    public Q10964_SecretGarden() {
        super(10964);
        this.addStartNpc(30332);
        this.addTalkId(new int[] { 30332, 30289 });
        this.addKillId(new int[] { 20145, 20158, 20176, 20248, 20249 });
        this.setQuestNameNpcStringId(NpcStringId.LV_30_34_SECRET_GARDEN);
        this.addCondMinLevel(30, "no_lvl.html");
        this.addCondMaxLevel(34, "no_lvl.html");
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
            case "30332-01.htm": {
                qs.startQuest();
                htmltext = event;
                break;
            }
            case "30289-01.html": {
                qs.setCond(2, true);
                htmltext = event;
                break;
            }
            case "30289-02.html": {
                htmltext = event;
                break;
            }
            case "30289-03.html": {
                if (qs.isStarted()) {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(NpcStringId.THE_MISSION_ADVENTURER_S_JOURNEY_II_IS_NOW_AVAILABLE_NCLICK_THE_YELLOW_QUESTION_MARK_IN_THE_RIGHT_BOTTOM_CORNER_OF_YOUR_SCREEN_TO_SEE_THE_QUEST_S_INFO, 2, 5000, new String[0]) });
                    addExpAndSp(player, 2500000L, 75000);
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
        if (qs != null && qs.isCond(2)) {
            final int killCount = qs.getInt("KillCount") + 1;
            if (killCount < 500) {
                qs.set("KillCount", killCount);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                this.sendNpcLogList(killer);
            }
            else {
                qs.setCond(3, true);
                qs.unset("KillCount");
                killer.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(NpcStringId.MONSTERS_OF_THE_GORGON_FLOWER_GARDEN_ARE_KILLED_NUSE_THE_TELEPORT_TO_GET_TO_HIGH_PRIEST_RAYMOND_IN_GLUDIO, 2, 5000, new String[0]) });
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCond(2)) {
            final Set<NpcLogListHolder> holder = new HashSet<NpcLogListHolder>();
            holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_GORGON_FLOWER_GARDEN.getId(), true, qs.getInt("KillCount")));
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
            htmltext = "30332.htm";
        }
        else if (qs.isStarted()) {
            switch (npc.getId()) {
                case 30332: {
                    if (qs.isCond(1)) {
                        htmltext = "30332-01.htm";
                        break;
                    }
                    break;
                }
                case 30289: {
                    if (qs.isCond(1)) {
                        htmltext = "30289.html";
                        break;
                    }
                    if (qs.isCond(3)) {
                        htmltext = "30289-02.html";
                        break;
                    }
                    break;
                }
            }
        }
        else if (qs.isCompleted() && npc.getId() == 30332) {
            htmltext = getAlreadyCompletedMsg(player);
        }
        return htmltext;
    }
}
