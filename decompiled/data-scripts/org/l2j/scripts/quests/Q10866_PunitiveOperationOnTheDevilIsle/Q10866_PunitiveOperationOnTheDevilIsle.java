// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q10866_PunitiveOperationOnTheDevilIsle;

import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.quest.Quest;

public final class Q10866_PunitiveOperationOnTheDevilIsle extends Quest
{
    private static final int RODEMAI = 30756;
    private static final int EIN = 34017;
    private static final int FETHIN = 34019;
    private static final int NIKIA = 34020;
    private static final int MIN_LEVEL = 70;
    
    public Q10866_PunitiveOperationOnTheDevilIsle() {
        super(10866);
        this.addStartNpc(30756);
        this.addTalkId(new int[] { 30756, 34017, 34019, 34020 });
        this.setQuestNameNpcStringId(NpcStringId.LV_70_PUNITIVE_OPERATION_ON_THE_DEVIL_S_ISLE);
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
            case "30756-02.htm": {
                qs.startQuest();
                htmltext = event;
                break;
            }
            case "34017-02.html": {
                qs.setCond(2, true);
                htmltext = event;
                break;
            }
            case "34019-02.html": {
                qs.setCond(3, true);
                htmltext = event;
                break;
            }
            case "34020-02.html": {
                if (qs.isStarted()) {
                    addExpAndSp(player, 150000L, 4500);
                    this.giveAdena(player, 13136L, true);
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
        if (qs.isCreated()) {
            htmltext = ((player.getLevel() >= 70) ? "30756-01.htm" : "no_lvl.html");
        }
        else if (qs.isStarted()) {
            switch (npc.getId()) {
                case 30756: {
                    if (qs.isCond(1)) {
                        htmltext = "30756-02.html";
                        break;
                    }
                    break;
                }
                case 34017: {
                    if (qs.isCond(1)) {
                        htmltext = "34017-01.html";
                        break;
                    }
                    if (qs.isCond(2)) {
                        htmltext = "34017-02.html";
                        break;
                    }
                    break;
                }
                case 34019: {
                    if (qs.isCond(2)) {
                        htmltext = "34019-01.html";
                        break;
                    }
                    if (qs.isCond(3)) {
                        htmltext = "34019-02.html";
                        break;
                    }
                    break;
                }
                case 34020: {
                    if (qs.isCond(3)) {
                        htmltext = "34020-01.html";
                        break;
                    }
                    break;
                }
            }
        }
        else if (qs.isCompleted() && npc.getId() == 30756) {
            htmltext = getAlreadyCompletedMsg(player);
        }
        return htmltext;
    }
}
