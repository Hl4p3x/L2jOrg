// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00933_ExploringTheWestWingOfTheDungeonOfAbyss;

import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;

public class Q00933_ExploringTheWestWingOfTheDungeonOfAbyss extends Quest
{
    private static final int MAGRIT = 31774;
    private static final int INGRIT = 31775;
    public final int MERTT = 21638;
    public final int DUHT = 21639;
    public final int PRIZT = 21640;
    public final int KOVART = 21641;
    public final ItemHolder OSKZLA;
    public final ItemHolder POD;
    
    public Q00933_ExploringTheWestWingOfTheDungeonOfAbyss() {
        super(933);
        this.OSKZLA = new ItemHolder(90008, 1L);
        this.POD = new ItemHolder(90136, 1L);
        this.addStartNpc(new int[] { 31774, 31775 });
        this.addTalkId(new int[] { 31774, 31775 });
        this.addKillId(new int[] { 21638, 21639, 21640, 21641 });
        this.registerQuestItems(new int[] { this.OSKZLA.getId() });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "31774-01.htm":
            case "31774-02.htm":
            case "31774-03.htm":
            case "31775-01.htm":
            case "31775-02.htm":
            case "31775-03.htm": {
                htmltext = event;
                break;
            }
            case "31774-04.htm": {
                if (player.getLevel() >= 40) {
                    qs.startQuest();
                    htmltext = event;
                    break;
                }
                break;
            }
            case "31775-04.htm": {
                if (player.getLevel() >= 40) {
                    qs.startQuest();
                    htmltext = event;
                    break;
                }
                break;
            }
            case "end.htm": {
                player.addExpAndSp(250000.0, 7700.0);
                rewardItems(player, this.POD);
                qs.exitQuest(QuestType.DAILY, true);
                break;
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player talker) {
        final QuestState qs = this.getQuestState(talker, true);
        String htmltext = getNoQuestMsg(talker);
        switch (qs.getState()) {
            case 0: {
                htmltext = ((talker.getLevel() < 40) ? "nolvl.htm" : "31774-01.htm");
                break;
            }
            case 1: {
                if (npc.getId() == 31774) {
                    switch (qs.getCond()) {
                        case 0: {
                            if (qs.getPlayer().getLevel() >= 40 && qs.getPlayer().getLevel() <= 46) {
                                htmltext = "31774-01.htm";
                                break;
                            }
                            htmltext = "31774-01a.htm";
                            break;
                        }
                        case 1: {
                            htmltext = "31774-04.htm";
                            break;
                        }
                        case 2: {
                            htmltext = "31774-05.htm";
                            break;
                        }
                    }
                    break;
                }
                if (npc.getId() == 31775) {
                    switch (qs.getCond()) {
                        case 0: {
                            if (qs.getPlayer().getLevel() >= 40 && qs.getPlayer().getLevel() <= 46) {
                                htmltext = "31775-01.htm";
                                qs.startQuest();
                                break;
                            }
                            htmltext = "31775-01a.htm";
                            break;
                        }
                        case 1: {
                            htmltext = "31775-04.htm";
                            break;
                        }
                        case 2: {
                            htmltext = "31775-05.htm";
                            break;
                        }
                    }
                    break;
                }
                break;
            }
            case 2: {
                if (!qs.isNowAvailable()) {
                    htmltext = getAlreadyCompletedMsg(talker);
                    break;
                }
                qs.setState((byte)0);
                if (npc.getId() == 31774 && qs.getPlayer().getLevel() < 40) {
                    htmltext = "31774-01.htm";
                    break;
                }
                if (npc.getId() == 31775 && qs.getPlayer().getLevel() < 40) {
                    htmltext = "31775-01.htm";
                    break;
                }
                htmltext = "nolvl.htm";
                break;
            }
        }
        return htmltext;
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        final QuestState qs = this.getQuestState(killer, false);
        if (qs.getCond() == 1) {
            if (getQuestItemsCount(killer, this.OSKZLA.getId()) < 50L) {
                giveItems(killer, this.OSKZLA);
            }
            if (getQuestItemsCount(killer, this.OSKZLA.getId()) >= 50L) {
                qs.setCond(2);
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
}
