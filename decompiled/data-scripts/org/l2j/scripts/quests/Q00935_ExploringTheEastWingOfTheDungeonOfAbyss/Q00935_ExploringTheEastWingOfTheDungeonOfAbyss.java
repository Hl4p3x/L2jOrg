// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00935_ExploringTheEastWingOfTheDungeonOfAbyss;

import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.enums.QuestType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.quest.Quest;

public class Q00935_ExploringTheEastWingOfTheDungeonOfAbyss extends Quest
{
    private static final int IRIS = 31776;
    private static final int ROSAMMY = 31777;
    public final int MERTT = 21644;
    public final int DUHT = 21645;
    public final int PRIZT = 21646;
    public final int KOVART = 21647;
    public final ItemHolder OSKZLA;
    public final ItemHolder POD;
    
    public Q00935_ExploringTheEastWingOfTheDungeonOfAbyss() {
        super(935);
        this.OSKZLA = new ItemHolder(90009, 1L);
        this.POD = new ItemHolder(90136, 1L);
        this.addStartNpc(new int[] { 31776, 31777 });
        this.addTalkId(new int[] { 31776, 31777 });
        this.addKillId(new int[] { 21644, 21645, 21646, 21647 });
        this.registerQuestItems(new int[] { this.OSKZLA.getId() });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (qs == null) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "31776-01.htm":
            case "31776-02.htm":
            case "31776-03.htm":
            case "31777-01.htm":
            case "31777-02.htm":
            case "31777-03.htm": {
                htmltext = event;
                break;
            }
            case "31776-04.htm": {
                if (player.getLevel() >= 45) {
                    qs.startQuest();
                    htmltext = event;
                    break;
                }
                break;
            }
            case "31777-04.htm": {
                if (player.getLevel() >= 45) {
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
                htmltext = ((talker.getLevel() < 45) ? "nolvl.htm" : "31776-01.htm");
                break;
            }
            case 1: {
                if (npc.getId() == 31776) {
                    switch (qs.getCond()) {
                        case 0: {
                            if (qs.getPlayer().getLevel() >= 45 && qs.getPlayer().getLevel() <= 49) {
                                htmltext = "31776-01.htm";
                                break;
                            }
                            htmltext = "31776-01a.htm";
                            break;
                        }
                        case 1: {
                            htmltext = "31776-04.htm";
                            break;
                        }
                        case 2: {
                            htmltext = "31776-05.htm";
                            break;
                        }
                    }
                    break;
                }
                if (npc.getId() == 31777) {
                    switch (qs.getCond()) {
                        case 0: {
                            if (qs.getPlayer().getLevel() >= 45 && qs.getPlayer().getLevel() <= 49) {
                                htmltext = "31777-01.htm";
                                qs.startQuest();
                                break;
                            }
                            htmltext = "31777-01a.htm";
                            break;
                        }
                        case 1: {
                            htmltext = "31777-04.htm";
                            break;
                        }
                        case 2: {
                            htmltext = "31777-05.htm";
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
                if (npc.getId() == 31776 && qs.getPlayer().getLevel() < 45) {
                    htmltext = "31776-01.htm";
                    break;
                }
                if (npc.getId() == 31777 && qs.getPlayer().getLevel() < 45) {
                    htmltext = "31777-01.htm";
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
