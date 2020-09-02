// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10966_ATripBegins;

import java.util.Objects;
import java.util.HashSet;
import org.l2j.gameserver.model.holders.NpcLogListHolder;
import java.util.Set;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;

public class Q10966_ATripBegins extends Quest
{
    private static final int CAPTAIN_BATHIS = 30332;
    private static final int BELLA = 30256;
    private static final ItemHolder SOE_TO_CAPTAIN_BATHIS;
    private static final ItemHolder SOE_NOVICE;
    private static final ItemHolder TALISMAN_OF_ADEN;
    private static final ItemHolder SCROLL_OF_ENCHANT_TALISMAN_OF_ADEN;
    private static final ItemHolder ADVENTURERS_BRACELET;
    private static final int ARACHNID_PREDATOR = 20926;
    private static final int SKELETON_BOWMAN = 20051;
    private static final int RUIN_SPARTOI = 20054;
    private static final int RAGING_SPARTOI = 20060;
    private static final int TUMRAN_BUGBEAR = 20062;
    private static final int TUMRAN_BUGBEAR_WARRIOR = 20064;
    private static final int MIN_LEVEL = 20;
    private static final int MAX_LEVEL = 25;
    private static final String KILL_COUNT_VAR = "KillCount";
    
    public Q10966_ATripBegins() {
        super(10966);
        this.addStartNpc(30332);
        this.addTalkId(new int[] { 30332, 30256 });
        this.addKillId(new int[] { 20926, 20051, 20054, 20060, 20060, 20062, 20064 });
        this.addCondMinLevel(20, "no_lvl.html");
        this.addCondMaxLevel(25, "no_lvl.html");
        this.setQuestNameNpcStringId(NpcStringId.LV_20_25_A_TRIP_BEGINS);
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
                htmltext = event;
                break;
            }
            case "30332-02.htm": {
                htmltext = event;
                break;
            }
            case "30332-03.htm": {
                qs.startQuest();
                npc.broadcastPacket((ServerPacket)new NpcSay(npc, ChatType.NPC_GENERAL, NpcStringId.TALK_TO_BELLA));
                htmltext = event;
                break;
            }
            case "30256-01.html": {
                qs.setCond(2, true);
                htmltext = event;
                break;
            }
            case "30332-05.html": {
                if (qs.isStarted()) {
                    addExpAndSp(player, 500000L, 12500);
                    giveItems(player, Q10966_ATripBegins.SOE_NOVICE);
                    giveItems(player, Q10966_ATripBegins.TALISMAN_OF_ADEN);
                    giveItems(player, Q10966_ATripBegins.SCROLL_OF_ENCHANT_TALISMAN_OF_ADEN);
                    giveItems(player, Q10966_ATripBegins.ADVENTURERS_BRACELET);
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
            if (killCount < 15) {
                qs.set("KillCount", killCount);
                playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                this.sendNpcLogList(killer);
            }
            else {
                qs.setCond(3, true);
                qs.unset("KillCount");
                killer.sendPacket(new ServerPacket[] { (ServerPacket)new ExShowScreenMessage("You have taken your first step as an adventurer.#Return to Bathis and get your reward.", 5000) });
                giveItems(killer, Q10966_ATripBegins.SOE_TO_CAPTAIN_BATHIS);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    public Set<NpcLogListHolder> getNpcLogList(final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs != null && qs.isCond(2)) {
            final Set<NpcLogListHolder> holder = new HashSet<NpcLogListHolder>();
            holder.add(new NpcLogListHolder(NpcStringId.DEFEAT_THE_MONSTERS_IN_THE_RUINS_OF_AGONY.getId(), true, qs.getInt("KillCount")));
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
                        htmltext = "30332-03.htm";
                        break;
                    }
                    if (qs.isCond(3)) {
                        htmltext = "30332-04.html";
                        break;
                    }
                    break;
                }
                case 30256: {
                    if (qs.isCond(1)) {
                        htmltext = "30256.html";
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
    
    static {
        SOE_TO_CAPTAIN_BATHIS = new ItemHolder(91651, 1L);
        SOE_NOVICE = new ItemHolder(10650, 10L);
        TALISMAN_OF_ADEN = new ItemHolder(91745, 1L);
        SCROLL_OF_ENCHANT_TALISMAN_OF_ADEN = new ItemHolder(91756, 1L);
        ADVENTURERS_BRACELET = new ItemHolder(91934, 1L);
    }
}
