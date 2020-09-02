// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.ElfHumanClericChange2;

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class ElfHumanClericChange2 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
    private static final int MARK_OF_PILGRIM = 2721;
    private static final int MARK_OF_TRUST = 2734;
    private static final int MARK_OF_HEALER = 2820;
    private static final int MARK_OF_REFORMER = 2821;
    private static final int MARK_OF_LIFE = 3140;
    private static final int BISHOP = 16;
    private static final int PROPHET = 17;
    private static final int ELDER = 30;
    
    private ElfHumanClericChange2() {
        this.addStartNpc(ElfHumanClericChange2.NPCS);
        this.addTalkId(ElfHumanClericChange2.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "30120-02.htm":
            case "30120-03.htm":
            case "30120-04.htm":
            case "30120-05.htm":
            case "30120-06.htm":
            case "30120-07.htm":
            case "30120-08.htm":
            case "30120-10.htm":
            case "30120-11.htm":
            case "30120-12.htm": {
                htmltext = event;
                break;
            }
            case "16":
            case "17":
            case "30": {
                htmltext = this.ClassChangeRequested(player, Integer.valueOf(event));
                break;
            }
        }
        return htmltext;
    }
    
    private String ClassChangeRequested(final Player player, final int classId) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
            htmltext = "30120-15.htm";
        }
        else if (classId == 16 && player.getClassId() == ClassId.CLERIC) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2721, 2734, 2820 })) {
                    htmltext = "30120-16.htm";
                }
                else {
                    htmltext = "30120-17.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2721, 2734, 2820 })) {
                takeItems(player, -1, new int[] { 2721, 2734, 2820 });
                player.setClassId(16);
                player.setBaseClass(16);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30120-18.htm";
            }
            else {
                htmltext = "30120-19.htm";
            }
        }
        else if (classId == 17 && player.getClassId() == ClassId.CLERIC) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2721, 2734, 2821 })) {
                    htmltext = "30120-20.htm";
                }
                else {
                    htmltext = "30120-21.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2721, 2734, 2821 })) {
                takeItems(player, -1, new int[] { 2721, 2734, 2821 });
                player.setClassId(17);
                player.setBaseClass(17);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30120-22.htm";
            }
            else {
                htmltext = "30120-23.htm";
            }
        }
        else if (classId == 30 && player.getClassId() == ClassId.ORACLE) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2721, 3140, 2820 })) {
                    htmltext = "30120-24.htm";
                }
                else {
                    htmltext = "30120-25.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2721, 3140, 2820 })) {
                takeItems(player, -1, new int[] { 2721, 3140, 2820 });
                player.setClassId(30);
                player.setBaseClass(30);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30120-26.htm";
            }
            else {
                htmltext = "30120-27.htm";
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.CLERIC_GROUP) && player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.isInCategory(CategoryType.HUMAN_CALL_CLASS) || player.isInCategory(CategoryType.ELF_CALL_CLASS))) {
            htmltext = "30120-01.htm";
        }
        else if (player.isInCategory(CategoryType.CLERIC_GROUP) && (player.isInCategory(CategoryType.HUMAN_CALL_CLASS) || player.isInCategory(CategoryType.ELF_CALL_CLASS))) {
            final ClassId classId = player.getClassId();
            if (classId == ClassId.CLERIC || classId == ClassId.BISHOP || classId == ClassId.PROPHET) {
                htmltext = "30120-02.htm";
            }
            else if (classId == ClassId.ORACLE || classId == ClassId.ELDER) {
                htmltext = "30120-09.htm";
            }
            else {
                htmltext = "30120-13.htm";
            }
        }
        else {
            htmltext = "30120-14.htm";
        }
        return htmltext;
    }
    
    public static ElfHumanClericChange2 provider() {
        return new ElfHumanClericChange2();
    }
    
    static {
        ElfHumanClericChange2.NPCS = new int[] { 30120, 30191, 30857, 30905, 31279 };
    }
}
