// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.events.LetterCollector;

import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.quest.LongTimeEvent;

public final class LetterCollector extends LongTimeEvent
{
    private static final int ROSALIA = 9000;
    private static final int A = 3875;
    private static final int C = 3876;
    private static final int E = 3877;
    private static final int G = 3879;
    private static final int I = 3881;
    private static final int L = 3882;
    private static final int N = 3883;
    private static final int R = 3885;
    private static final int M = 34956;
    private static final int O = 3884;
    private static final int S = 3886;
    private static final int H = 3880;
    private static final int II = 3888;
    private static final int[] LETTERS;
    private static final int REWARD = 29581;
    private static final int MEMMORIES = 29583;
    private static final int CHRONICLE = 29582;
    
    private LetterCollector() {
        this.addStartNpc(9000);
        this.addFirstTalkId(9000);
        this.addTalkId(9000);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "9000-1.htm":
            case "9000-2.htm": {
                htmltext = event;
                break;
            }
            case "reward": {
                if (getQuestItemsCount(player, 3882) >= 1L && getQuestItemsCount(player, 3881) >= 1L && getQuestItemsCount(player, 3883) >= 1L && getQuestItemsCount(player, 3877) >= 2L && getQuestItemsCount(player, 3875) >= 1L && getQuestItemsCount(player, 3879) >= 1L && getQuestItemsCount(player, 3888) >= 1L) {
                    takeItems(player, 3882, 1L);
                    takeItems(player, 3881, 1L);
                    takeItems(player, 3883, 1L);
                    takeItems(player, 3877, 2L);
                    takeItems(player, 3875, 1L);
                    takeItems(player, 3879, 1L);
                    takeItems(player, 3888, 1L);
                    giveItems(player, 29581, 1L);
                    htmltext = "9000-1.htm";
                    break;
                }
                htmltext = "noItem.htm";
                break;
            }
            case "memories": {
                if (getQuestItemsCount(player, 34956) >= 2L && getQuestItemsCount(player, 3877) >= 2L && getQuestItemsCount(player, 3884) >= 1L && getQuestItemsCount(player, 3885) >= 1L && getQuestItemsCount(player, 3881) >= 1L && getQuestItemsCount(player, 3886) >= 1L) {
                    takeItems(player, 34956, 2L);
                    takeItems(player, 3877, 2L);
                    takeItems(player, 3884, 1L);
                    takeItems(player, 3885, 1L);
                    takeItems(player, 3881, 1L);
                    takeItems(player, 3886, 1L);
                    giveItems(player, 29583, 1L);
                    htmltext = "9000-1.htm";
                    break;
                }
                htmltext = "noItem.htm";
                break;
            }
            case "chronicle": {
                if (getQuestItemsCount(player, 3876) >= 2L && getQuestItemsCount(player, 3880) >= 1L && getQuestItemsCount(player, 3885) >= 1L && getQuestItemsCount(player, 3884) >= 1L && getQuestItemsCount(player, 3883) >= 1L && getQuestItemsCount(player, 3881) >= 1L && getQuestItemsCount(player, 3882) >= 1L && getQuestItemsCount(player, 3877) >= 1L) {
                    takeItems(player, 3876, 2L);
                    takeItems(player, 3880, 1L);
                    takeItems(player, 3885, 1L);
                    takeItems(player, 3884, 1L);
                    takeItems(player, 3883, 1L);
                    takeItems(player, 3881, 1L);
                    takeItems(player, 3882, 1L);
                    takeItems(player, 3877, 1L);
                    giveItems(player, 29582, 1L);
                    htmltext = "9000-1.htm";
                    break;
                }
                htmltext = "noItem.htm";
                break;
            }
            case "exchangeA": {
                if (getQuestItemsCount(player, 3875) >= 2L) {
                    takeItems(player, 3875, 2L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeC": {
                if (getQuestItemsCount(player, 3876) >= 2L) {
                    takeItems(player, 3876, 2L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeE": {
                if (getQuestItemsCount(player, 3877) >= 2L) {
                    takeItems(player, 3877, 2L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeG": {
                if (getQuestItemsCount(player, 3879) >= 2L) {
                    takeItems(player, 3879, 2L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeI": {
                if (getQuestItemsCount(player, 3881) >= 2L) {
                    takeItems(player, 3881, 2L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeL": {
                if (getQuestItemsCount(player, 3882) >= 2L) {
                    takeItems(player, 3882, 2L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeM": {
                if (getQuestItemsCount(player, 34956) >= 2L) {
                    takeItems(player, 34956, 2L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeN": {
                if (getQuestItemsCount(player, 3883) >= 2L) {
                    takeItems(player, 3883, 2L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeO": {
                if (getQuestItemsCount(player, 3884) >= 2L) {
                    takeItems(player, 3884, 2L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeR": {
                if (getQuestItemsCount(player, 3885) >= 2L) {
                    takeItems(player, 3885, 2L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeH": {
                if (getQuestItemsCount(player, 3880) >= 1L) {
                    takeItems(player, 3880, 1L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeS": {
                if (getQuestItemsCount(player, 3886) >= 1L) {
                    takeItems(player, 3886, 1L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
            case "exchangeII": {
                if (getQuestItemsCount(player, 3888) >= 1L) {
                    takeItems(player, 3888, 1L);
                    giveItems(player, getRandomEntry(LetterCollector.LETTERS), 1L);
                    htmltext = "9000-2.htm";
                    break;
                }
                htmltext = "noItemExchange.htm";
                break;
            }
        }
        return htmltext;
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
    }
    
    public static AbstractScript provider() {
        return (AbstractScript)new LetterCollector();
    }
    
    static {
        LETTERS = new int[] { 3875, 3876, 3877, 3879, 3881, 3882, 3883, 3885, 34956, 3884, 3886, 3880, 3888 };
    }
}
