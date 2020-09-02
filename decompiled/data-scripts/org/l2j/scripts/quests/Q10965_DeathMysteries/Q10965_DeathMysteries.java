// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10965_DeathMysteries;

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
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;

public class Q10965_DeathMysteries extends Quest
{
    private static final int RAYMOND = 30289;
    private static final int MAXIMILLIAN = 30120;
    private static final int WYRM = 20176;
    private static final int GUARDIAN_BASILISK = 20550;
    private static final int ROAD_SCAVENGER = 20551;
    private static final int FETTERED_SOUL = 20552;
    private static final int WINDUS = 20553;
    private static final int GRANDIS = 20554;
    private static final ItemHolder ADVENTURERS_AGATHION_BRACELET;
    private static final ItemHolder ADVENTURERS_AGATHION_GRIFIN;
    private static final String KILL_COUNT_VAR = "KillCount";
    private static final int MAX_LEVEL = 40;
    private static final int MIN_LEVEL = 37;
    
    public Q10965_DeathMysteries() {
        super(10965);
        this.addStartNpc(30289);
        this.addTalkId(new int[] { 30289, 30120 });
        this.addKillId(new int[] { 20176, 20550, 20551, 20552, 20553, 20554 });
        this.setQuestNameNpcStringId(NpcStringId.LV_37_40_DEATH_MYSTERIES);
        this.addCondMinLevel(37, "no_lvl.html");
        this.addCondMaxLevel(40, "no_lvl.html");
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
            case "TELEPORT_TO_MAXIMILLIAN": {
                player.teleToLocation(86845, 148626, -3402);
                break;
            }
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
            case "30120-01.html": {
                htmltext = event;
                break;
            }
            case "30120-02.html": {
                htmltext = event;
                break;
            }
            case "30120-03.html": {
                qs.setCond(2, true);
                htmltext = event;
                break;
            }
            case "30120-05.html": {
                if (qs.isStarted()) {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(NpcStringId.YOU_VE_GOT_ADVENTURER_S_AGATHION_BRACELET_AND_ADVENTURER_S_AGATHION_GRIFFIN_NCOMPLETE_THE_TUTORIAL_AND_TRY_TO_USE_THE_AGATHION, 2, 5000, new String[0]) });
                    addExpAndSp(player, 3000000L, 75000);
                    giveItems(player, Q10965_DeathMysteries.ADVENTURERS_AGATHION_BRACELET);
                    giveItems(player, Q10965_DeathMysteries.ADVENTURERS_AGATHION_GRIFIN);
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
                killer.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(NpcStringId.MONSTERS_OF_THE_DEATH_PASS_ARE_KILLED_NUSE_THE_TELEPORT_OR_THE_SCROLL_OF_ESCAPE_TO_GET_TO_HIGH_PRIEST_MAXIMILIAN_IN_GIRAN, 2, 5000, new String[0]) });
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCond(2)) {
            final Set<NpcLogListHolder> holder = new HashSet<NpcLogListHolder>();
            holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_DEATH_PASS.getId(), true, qs.getInt("KillCount")));
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
                    if (qs.isCond(1)) {
                        htmltext = "30289-01.htm";
                        break;
                    }
                    break;
                }
                case 30120: {
                    if (qs.isCond(1)) {
                        htmltext = "30120.html";
                        break;
                    }
                    if (qs.isCond(3)) {
                        htmltext = "30120-04.html";
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
    
    static {
        ADVENTURERS_AGATHION_BRACELET = new ItemHolder(91933, 1L);
        ADVENTURERS_AGATHION_GRIFIN = new ItemHolder(91935, 1L);
    }
}
