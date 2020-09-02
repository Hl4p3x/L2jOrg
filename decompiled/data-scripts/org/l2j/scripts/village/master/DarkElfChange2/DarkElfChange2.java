// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.DarkElfChange2;

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class DarkElfChange2 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static int MARK_OF_CHALLENGER;
    private static int MARK_OF_DUTY;
    private static int MARK_OF_SEEKER;
    private static int MARK_OF_SCHOLAR;
    private static int MARK_OF_PILGRIM;
    private static int MARK_OF_DUELIST;
    private static int MARK_OF_SEARCHER;
    private static int MARK_OF_REFORMER;
    private static int MARK_OF_MAGUS;
    private static int MARK_OF_FATE;
    private static int MARK_OF_SAGITTARIUS;
    private static int MARK_OF_WITCHCRAFT;
    private static int MARK_OF_SUMMONER;
    private static int[][] CLASSES;
    
    private DarkElfChange2() {
        this.addStartNpc(DarkElfChange2.NPCS);
        this.addTalkId(DarkElfChange2.NPCS);
    }
    
    public String onAdvEvent(String event, final Npc npc, final Player player) {
        if (Util.isInteger(event)) {
            final int i = Integer.valueOf(event);
            final ClassId cid = player.getClassId();
            if (cid.getRace() == Race.DARK_ELF && cid.getId() == DarkElfChange2.CLASSES[i][1]) {
                final boolean item1 = hasQuestItems(player, DarkElfChange2.CLASSES[i][6]);
                final boolean item2 = hasQuestItems(player, DarkElfChange2.CLASSES[i][7]);
                final boolean item3 = hasQuestItems(player, DarkElfChange2.CLASSES[i][8]);
                int suffix;
                if (player.getLevel() < 40) {
                    suffix = ((!item1 || !item2 || !item3) ? DarkElfChange2.CLASSES[i][2] : DarkElfChange2.CLASSES[i][3]);
                }
                else if (!item1 || !item2 || !item3) {
                    suffix = DarkElfChange2.CLASSES[i][4];
                }
                else {
                    suffix = DarkElfChange2.CLASSES[i][5];
                    takeItems(player, DarkElfChange2.CLASSES[i][6], -1L);
                    takeItems(player, DarkElfChange2.CLASSES[i][7], -1L);
                    takeItems(player, DarkElfChange2.CLASSES[i][8], -1L);
                    playSound(player, QuestSound.ITEMSOUND_QUEST_FANFARE_2);
                    player.setClassId(DarkElfChange2.CLASSES[i][0]);
                    player.setBaseClass(DarkElfChange2.CLASSES[i][0]);
                    player.broadcastUserInfo();
                }
                event = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, suffix);
            }
        }
        return event;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        String htmltext = getNoQuestMsg(player);
        if (player.isSubClassActive()) {
            return htmltext;
        }
        final ClassId cid = player.getClassId();
        if (cid.getRace() == Race.DARK_ELF) {
            switch (cid) {
                case PALUS_KNIGHT: {
                    htmltext = "30474-01.html";
                    break;
                }
                case SHILLIEN_ORACLE: {
                    htmltext = "30474-08.html";
                    break;
                }
                case ASSASSIN: {
                    htmltext = "30474-12.html";
                    break;
                }
                case DARK_WIZARD: {
                    htmltext = "30474-19.html";
                    break;
                }
                default: {
                    if (cid.level() == 0) {
                        htmltext = "30474-55.html";
                        break;
                    }
                    if (cid.level() >= 2) {
                        htmltext = "30474-54.html";
                        break;
                    }
                    htmltext = "30474-56.html";
                    break;
                }
            }
        }
        else {
            htmltext = "30474-56.html";
        }
        return htmltext;
    }
    
    public static DarkElfChange2 provider() {
        return new DarkElfChange2();
    }
    
    static {
        DarkElfChange2.NPCS = new int[] { 30195, 30699, 30474, 30862, 30910, 31285 };
        DarkElfChange2.MARK_OF_CHALLENGER = 2627;
        DarkElfChange2.MARK_OF_DUTY = 2633;
        DarkElfChange2.MARK_OF_SEEKER = 2673;
        DarkElfChange2.MARK_OF_SCHOLAR = 2674;
        DarkElfChange2.MARK_OF_PILGRIM = 2721;
        DarkElfChange2.MARK_OF_DUELIST = 2762;
        DarkElfChange2.MARK_OF_SEARCHER = 2809;
        DarkElfChange2.MARK_OF_REFORMER = 2821;
        DarkElfChange2.MARK_OF_MAGUS = 2840;
        DarkElfChange2.MARK_OF_FATE = 3172;
        DarkElfChange2.MARK_OF_SAGITTARIUS = 3293;
        DarkElfChange2.MARK_OF_WITCHCRAFT = 3307;
        DarkElfChange2.MARK_OF_SUMMONER = 3336;
        DarkElfChange2.CLASSES = new int[][] { { 33, 32, 26, 27, 28, 29, DarkElfChange2.MARK_OF_DUTY, DarkElfChange2.MARK_OF_FATE, DarkElfChange2.MARK_OF_WITCHCRAFT }, { 34, 32, 30, 31, 32, 33, DarkElfChange2.MARK_OF_CHALLENGER, DarkElfChange2.MARK_OF_FATE, DarkElfChange2.MARK_OF_DUELIST }, { 43, 42, 34, 35, 36, 37, DarkElfChange2.MARK_OF_PILGRIM, DarkElfChange2.MARK_OF_FATE, DarkElfChange2.MARK_OF_REFORMER }, { 36, 35, 38, 39, 40, 41, DarkElfChange2.MARK_OF_SEEKER, DarkElfChange2.MARK_OF_FATE, DarkElfChange2.MARK_OF_SEARCHER }, { 37, 35, 42, 43, 44, 45, DarkElfChange2.MARK_OF_SEEKER, DarkElfChange2.MARK_OF_FATE, DarkElfChange2.MARK_OF_SAGITTARIUS }, { 40, 39, 46, 47, 48, 49, DarkElfChange2.MARK_OF_SCHOLAR, DarkElfChange2.MARK_OF_FATE, DarkElfChange2.MARK_OF_MAGUS }, { 41, 39, 50, 51, 52, 53, DarkElfChange2.MARK_OF_SCHOLAR, DarkElfChange2.MARK_OF_FATE, DarkElfChange2.MARK_OF_SUMMONER } };
    }
}
