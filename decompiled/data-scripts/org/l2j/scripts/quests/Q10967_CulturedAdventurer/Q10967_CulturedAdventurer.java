// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10967_CulturedAdventurer;

import java.util.HashSet;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import java.util.Set;
import org.l2j.gameserver.enums.QuestSound;
import java.util.Objects;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;

public class Q10967_CulturedAdventurer extends Quest
{
    private static final int CAPTAIN_BATHIS = 30332;
    private static final int OL_MAHUM_SHOOTER = 20063;
    private static final int OL_MAHUM_SERGEANT = 20439;
    private static final int OL_MAHUM_OFFICER = 20066;
    private static final int OL_MAHUM_GENERAL = 20438;
    private static final int OL_MAHUM_COMMANDER = 20076;
    private static final ItemHolder ADVENTURERS_BROOCH;
    private static final ItemHolder ADVENTURERS_BROOCH_GEMS;
    private static final String KILL_COUNT_VAR = "KillCount";
    private static final int MAX_LEVEL = 30;
    private static final int MIN_LEVEL = 25;
    
    public Q10967_CulturedAdventurer() {
        super(10967);
        this.addStartNpc(30332);
        this.addTalkId(30332);
        this.addKillId(new int[] { 20063, 20439, 20066, 20438, 20076 });
        this.setQuestNameNpcStringId(NpcStringId.LV_25_30_MORE_EXPERIENCE);
        this.addCondMinLevel(25, "no_lvl.html");
        this.addCondMaxLevel(30, "no_lvl.html");
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "30332-01.htm": {
                htmltext = event;
                break;
            }
            case "30332-02.htm": {
                htmltext = event;
                break;
            }
            case "30332-03.htm": {
                qs.startQuest();
                htmltext = event;
                break;
            }
            case "30332-05.html": {
                if (qs.isStarted()) {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage("You've obtained Adventurer's Brooch and Adventurer's Gem Fragment.#Check the tutorial to equip the gems.", 5000) });
                    addExpAndSp(player, 2000000L, 50000);
                    giveItems(player, Q10967_CulturedAdventurer.ADVENTURERS_BROOCH);
                    giveItems(player, Q10967_CulturedAdventurer.ADVENTURERS_BROOCH_GEMS);
                    qs.exitQuest(false, true);
                    htmltext = event;
                    break;
                }
                break;
            }
        }
        return htmltext;
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
                    if (qs.isCond(2)) {
                        htmltext = "30332-04.html";
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
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState qs = this.getQuestState(killer, false);
        if (qs != null && qs.isCond(1)) {
            final int killCount = qs.getInt("KillCount") + 1;
            if (killer.isGM()) {
                qs.setCond(2, true);
                qs.unset("KillCount");
                killer.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(NpcStringId.MONSTERS_OF_THE_ABANDONED_CAMP_ARE_KILLED_NUSE_THE_TELEPORT_TO_GET_TO_BATHIS_IN_GLUDIO, 2, 5000, new String[0]) });
            }
            else if (killCount < 300) {
                qs.set("KillCount", killCount);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                this.sendNpcLogList(killer);
            }
            else {
                qs.setCond(2, true);
                qs.unset("KillCount");
                killer.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage(NpcStringId.MONSTERS_OF_THE_ABANDONED_CAMP_ARE_KILLED_NUSE_THE_TELEPORT_TO_GET_TO_BATHIS_IN_GLUDIO, 2, 5000, new String[0]) });
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCond(1)) {
            final Set<NpcLogListHolder> holder = new HashSet<NpcLogListHolder>();
            holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_ABANDONED_CAMP.getId(), true, qs.getInt("KillCount")));
            return holder;
        }
        return (Set<NpcLogListHolder>)super.getNpcLogList(player);
    }
    
    public boolean checkPartyMember(final Player member, final Npc npc) {
        final QuestState qs = this.getQuestState(member, false);
        return qs != null && qs.isStarted();
    }
    
    static {
        ADVENTURERS_BROOCH = new ItemHolder(91932, 1L);
        ADVENTURERS_BROOCH_GEMS = new ItemHolder(91936, 1L);
    }
}
