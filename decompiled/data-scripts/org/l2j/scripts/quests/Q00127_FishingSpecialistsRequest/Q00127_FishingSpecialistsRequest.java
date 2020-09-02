// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.Q00127_FishingSpecialistsRequest;

import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.interfaces.ILocational;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.quest.Quest;

public class Q00127_FishingSpecialistsRequest extends Quest
{
    private static final int PIERRE = 30013;
    private static final int FERMA = 30015;
    private static final int BAIKAL = 30016;
    private static final int PIERRE_LETTER = 49510;
    private static final int FISH_REPORT = 49504;
    private static final int SEALED_BOTTLE = 49505;
    private static final int FISHING_ROD_CHEST = 49507;
    private static final Location TELEPORT_LOC;
    private static final int MIN_LEVEL = 20;
    private static final int MAX_LEVEL = 110;
    
    public Q00127_FishingSpecialistsRequest() {
        super(127);
        this.addStartNpc(30013);
        this.addTalkId(new int[] { 30013, 30015, 30016 });
        this.addCondLevel(20, 110, "30013-00.htm");
        this.registerQuestItems(new int[] { 49510, 49504, 49505 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final QuestState qs = this.getQuestState(player, false);
        if (Objects.isNull(qs)) {
            return null;
        }
        String htmltext = null;
        switch (event) {
            case "30013-02.html": {
                qs.startQuest();
                giveItems(player, 49510, 1L);
                htmltext = event;
                break;
            }
            case "teleport_to_ferma": {
                player.teleToLocation((ILocational)Q00127_FishingSpecialistsRequest.TELEPORT_LOC);
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
            if (npc.getId() == 30013) {
                htmltext = ((player.getLevel() < 20) ? "30013-00.htm" : "30013-01.htm");
            }
        }
        else if (qs.isStarted()) {
            Label_0314: {
                switch (npc.getId()) {
                    case 30013: {
                        switch (qs.getCond()) {
                            case 1:
                            case 2: {
                                htmltext = "30013-03.html";
                                break;
                            }
                            case 3: {
                                takeItems(player, -1, 49505L);
                                giveItems(player, 49507, 1L);
                                qs.exitQuest(false, true);
                                htmltext = "30013-04.html";
                                break;
                            }
                        }
                        break;
                    }
                    case 30015: {
                        switch (qs.getCond()) {
                            case 1: {
                                takeItems(player, -1, 49510L);
                                giveItems(player, 49504, 1L);
                                qs.setCond(2, true);
                                htmltext = "30015-01.html";
                                break;
                            }
                            case 2: {
                                htmltext = "30015-02.html";
                                break;
                            }
                            case 3: {
                                htmltext = "30015-03.html";
                                break;
                            }
                        }
                        break;
                    }
                    case 30016: {
                        switch (qs.getCond()) {
                            case 2: {
                                takeItems(player, -1, 49504L);
                                giveItems(player, 49505, 1L);
                                qs.setCond(3, true);
                                htmltext = "30016-01.html";
                                break Label_0314;
                            }
                            case 3: {
                                htmltext = "30016-02.html";
                                break Label_0314;
                            }
                        }
                        break;
                    }
                }
            }
        }
        else if (qs.isCompleted()) {
            htmltext = getAlreadyCompletedMsg(player);
        }
        return htmltext;
    }
    
    static {
        TELEPORT_LOC = new Location(105276, 162500, -3600);
    }
}
