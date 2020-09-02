// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.DarkElfChange1;

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.enums.Race;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class DarkElfChange1 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static int GAZE_OF_ABYSS;
    private static int IRON_HEART;
    private static int JEWEL_OF_DARKNESS;
    private static int ORB_OF_ABYSS;
    private static int SHADOW_WEAPON_COUPON_DGRADE;
    private static int[][] CLASSES;
    
    private DarkElfChange1() {
        this.addStartNpc(DarkElfChange1.NPCS);
        this.addTalkId(DarkElfChange1.NPCS);
    }
    
    public String onAdvEvent(String event, final Npc npc, final Player player) {
        if (Util.isDigit(event)) {
            final int i = Integer.valueOf(event);
            final ClassId cid = player.getClassId();
            if (cid.getRace() == Race.DARK_ELF && cid.getId() == DarkElfChange1.CLASSES[i][1]) {
                final boolean item = hasQuestItems(player, DarkElfChange1.CLASSES[i][6]);
                int suffix;
                if (player.getLevel() < 20) {
                    suffix = (item ? DarkElfChange1.CLASSES[i][3] : DarkElfChange1.CLASSES[i][2]);
                }
                else if (!item) {
                    suffix = DarkElfChange1.CLASSES[i][4];
                }
                else {
                    suffix = DarkElfChange1.CLASSES[i][5];
                    giveItems(player, DarkElfChange1.SHADOW_WEAPON_COUPON_DGRADE, 15L);
                    takeItems(player, DarkElfChange1.CLASSES[i][6], -1L);
                    player.setClassId(DarkElfChange1.CLASSES[i][0]);
                    player.setBaseClass(DarkElfChange1.CLASSES[i][0]);
                    playSound(player, QuestSound.ITEMSOUND_QUEST_FANFARE_2);
                    player.broadcastUserInfo();
                }
                event = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, npc.getId(), suffix);
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
                case DARK_FIGHTER: {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                    break;
                }
                case DARK_MAGE: {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                    break;
                }
                default: {
                    if (cid.level() == 1) {
                        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                    }
                    if (cid.level() >= 2) {
                        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                    }
                    break;
                }
            }
        }
        else {
            htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        return htmltext;
    }
    
    public static DarkElfChange1 provider() {
        return new DarkElfChange1();
    }
    
    static {
        DarkElfChange1.NPCS = new int[] { 30290, 30297, 30462 };
        DarkElfChange1.GAZE_OF_ABYSS = 1244;
        DarkElfChange1.IRON_HEART = 1252;
        DarkElfChange1.JEWEL_OF_DARKNESS = 1261;
        DarkElfChange1.ORB_OF_ABYSS = 1270;
        DarkElfChange1.SHADOW_WEAPON_COUPON_DGRADE = 8869;
        DarkElfChange1.CLASSES = new int[][] { { 32, 31, 15, 16, 17, 18, DarkElfChange1.GAZE_OF_ABYSS }, { 35, 31, 19, 20, 21, 22, DarkElfChange1.IRON_HEART }, { 39, 38, 23, 24, 25, 26, DarkElfChange1.JEWEL_OF_DARKNESS }, { 42, 38, 27, 28, 29, 30, DarkElfChange1.ORB_OF_ABYSS } };
    }
}
