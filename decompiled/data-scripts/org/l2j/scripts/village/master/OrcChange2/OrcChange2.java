// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.OrcChange2;

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class OrcChange2 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
    private static final int MARK_OF_CHALLENGER = 2627;
    private static final int MARK_OF_PILGRIM = 2721;
    private static final int MARK_OF_DUELIST = 2762;
    private static final int MARK_OF_WARSPIRIT = 2879;
    private static final int MARK_OF_GLORY = 3203;
    private static final int MARK_OF_CHAMPION = 3276;
    private static final int MARK_OF_LORD = 3390;
    private static final int DESTROYER = 46;
    private static final int TYRANT = 48;
    private static final int OVERLORD = 51;
    private static final int WARCRYER = 52;
    
    private OrcChange2() {
        this.addStartNpc(OrcChange2.NPCS);
        this.addTalkId(OrcChange2.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "30513-03.htm":
            case "30513-04.htm":
            case "30513-05.htm":
            case "30513-07.htm":
            case "30513-08.htm":
            case "30513-09.htm":
            case "30513-10.htm":
            case "30513-11.htm":
            case "30513-12.htm":
            case "30513-13.htm":
            case "30513-14.htm":
            case "30513-15.htm":
            case "30513-16.htm": {
                htmltext = event;
                break;
            }
            case "46":
            case "48":
            case "51":
            case "52": {
                htmltext = this.ClassChangeRequested(player, Integer.valueOf(event));
                break;
            }
        }
        return htmltext;
    }
    
    private String ClassChangeRequested(final Player player, final int classId) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
            htmltext = "30513-19.htm";
        }
        else if (classId == 46 && player.getClassId() == ClassId.ORC_RAIDER) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2627, 3203, 3276 })) {
                    htmltext = "30513-20.htm";
                }
                else {
                    htmltext = "30513-21.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2627, 3203, 3276 })) {
                takeItems(player, -1, new int[] { 2627, 3203, 3276 });
                player.setClassId(46);
                player.setBaseClass(46);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30513-22.htm";
            }
            else {
                htmltext = "30513-23.htm";
            }
        }
        else if (classId == 48 && player.getClassId() == ClassId.ORC_MONK) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2627, 3203, 2762 })) {
                    htmltext = "30513-24.htm";
                }
                else {
                    htmltext = "30513-25.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2627, 3203, 2762 })) {
                takeItems(player, -1, new int[] { 2627, 3203, 2762 });
                player.setClassId(48);
                player.setBaseClass(48);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30513-26.htm";
            }
            else {
                htmltext = "30513-27.htm";
            }
        }
        else if (classId == 51 && player.getClassId() == ClassId.ORC_SHAMAN) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2721, 3203, 3390 })) {
                    htmltext = "30513-28.htm";
                }
                else {
                    htmltext = "30513-29.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2721, 3203, 3390 })) {
                takeItems(player, -1, new int[] { 2721, 3203, 3390 });
                player.setClassId(51);
                player.setBaseClass(51);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30513-30.htm";
            }
            else {
                htmltext = "30513-31.htm";
            }
        }
        else if (classId == 52 && player.getClassId() == ClassId.ORC_SHAMAN) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2721, 3203, 2879 })) {
                    htmltext = "30513-32.htm";
                }
                else {
                    htmltext = "30513-33.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2721, 3203, 2879 })) {
                takeItems(player, -1, new int[] { 2721, 3203, 2879 });
                player.setClassId(52);
                player.setBaseClass(52);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30513-34.htm";
            }
            else {
                htmltext = "30513-35.htm";
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.isInCategory(CategoryType.ORC_MALL_CLASS) || player.isInCategory(CategoryType.ORC_FALL_CLASS))) {
            htmltext = "30513-01.htm";
        }
        else if (player.isInCategory(CategoryType.ORC_MALL_CLASS) || player.isInCategory(CategoryType.ORC_FALL_CLASS)) {
            final ClassId classId = player.getClassId();
            if (classId == ClassId.ORC_RAIDER || classId == ClassId.DESTROYER) {
                htmltext = "30513-02.htm";
            }
            else if (classId == ClassId.ORC_MONK || classId == ClassId.TYRANT) {
                htmltext = "30513-06.htm";
            }
            else if (classId == ClassId.ORC_SHAMAN || classId == ClassId.OVERLORD || classId == ClassId.WARCRYER) {
                htmltext = "30513-10.htm";
            }
            else {
                htmltext = "30513-17.htm";
            }
        }
        else {
            htmltext = "30513-18.htm";
        }
        return htmltext;
    }
    
    public static OrcChange2 provider() {
        return new OrcChange2();
    }
    
    static {
        OrcChange2.NPCS = new int[] { 30513, 30681, 30704, 30865, 30913, 31288 };
    }
}
