// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.ElfHumanWizardChange1;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class ElfHumanWizardChange1 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
    private static final int MARK_OF_FAITH = 1201;
    private static final int ETERNITY_DIAMOND = 1230;
    private static final int LEAF_OF_ORACLE = 1235;
    private static final int BEAD_OF_SEASON = 1292;
    private static final int WIZARD = 11;
    private static final int CLERIC = 15;
    private static final int ELVEN_WIZARD = 26;
    private static final int ORACLE = 29;
    
    private ElfHumanWizardChange1() {
        this.addStartNpc(ElfHumanWizardChange1.NPCS);
        this.addTalkId(ElfHumanWizardChange1.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "30037-01.htm":
            case "30037-02.htm":
            case "30037-03.htm":
            case "30037-04.htm":
            case "30037-05.htm":
            case "30037-06.htm":
            case "30037-07.htm":
            case "30037-08.htm":
            case "30037-09.htm":
            case "30037-10.htm":
            case "30037-11.htm":
            case "30037-12.htm":
            case "30037-13.htm":
            case "30037-14.htm":
            case "30070-01.htm":
            case "30070-02.htm":
            case "30070-03.htm":
            case "30070-04.htm":
            case "30070-05.htm":
            case "30070-06.htm":
            case "30070-07.htm":
            case "30070-08.htm":
            case "30070-09.htm":
            case "30070-10.htm":
            case "30070-11.htm":
            case "30070-12.htm":
            case "30070-13.htm":
            case "30070-14.htm":
            case "30289-01.htm":
            case "30289-02.htm":
            case "30289-03.htm":
            case "30289-04.htm":
            case "30289-05.htm":
            case "30289-06.htm":
            case "30289-07.htm":
            case "30289-08.htm":
            case "30289-09.htm":
            case "30289-10.htm":
            case "30289-11.htm":
            case "30289-12.htm":
            case "30289-13.htm":
            case "30289-14.htm":
            case "32095-01.htm":
            case "32095-02.htm":
            case "32095-03.htm":
            case "32095-04.htm":
            case "32095-05.htm":
            case "32095-06.htm":
            case "32095-07.htm":
            case "32095-08.htm":
            case "32095-09.htm":
            case "32095-10.htm":
            case "32095-11.htm":
            case "32095-12.htm":
            case "32095-13.htm":
            case "32095-14.htm":
            case "32098-01.htm":
            case "32098-02.htm":
            case "32098-03.htm":
            case "32098-04.htm":
            case "32098-05.htm":
            case "32098-06.htm":
            case "32098-07.htm":
            case "32098-08.htm":
            case "32098-09.htm":
            case "32098-10.htm":
            case "32098-11.htm":
            case "32098-12.htm":
            case "32098-13.htm":
            case "32098-14.htm": {
                htmltext = event;
                break;
            }
            case "11":
            case "15":
            case "26":
            case "29": {
                htmltext = this.ClassChangeRequested(player, npc, Integer.valueOf(event));
                break;
            }
        }
        return htmltext;
    }
    
    private String ClassChangeRequested(final Player player, final Npc npc, final int classId) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)) {
            htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        else if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
            htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        else if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
            htmltext = "30037-34.htm";
        }
        else if (classId == 11 && player.getClassId() == ClassId.MAGE) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1292)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1292)) {
                takeItems(player, 1292, -1L);
                player.setClassId(11);
                player.setBaseClass(11);
                player.broadcastUserInfo();
                giveItems(player, 8869, 15L);
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        else if (classId == 15 && player.getClassId() == ClassId.MAGE) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1201)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1201)) {
                takeItems(player, 1201, -1L);
                player.setClassId(15);
                player.setBaseClass(15);
                player.broadcastUserInfo();
                giveItems(player, 8869, 15L);
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        else if (classId == 26 && player.getClassId() == ClassId.ELVEN_MAGE) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1230)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1230)) {
                takeItems(player, 1230, -1L);
                player.setClassId(26);
                player.setBaseClass(26);
                player.broadcastUserInfo();
                giveItems(player, 8869, 15L);
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        else if (classId == 29 && player.getClassId() == ClassId.ELVEN_MAGE) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1235)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1235)) {
                takeItems(player, 1235, -1L);
                player.setClassId(29);
                player.setBaseClass(29);
                player.broadcastUserInfo();
                giveItems(player, 8869, 15L);
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        String htmltext = null;
        final Race playerRace = player.getRace();
        if (player.isInCategory(CategoryType.MAGE_GROUP) && (playerRace == Race.HUMAN || playerRace == Race.ELF)) {
            if (playerRace == Race.HUMAN) {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        else {
            htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        return htmltext;
    }
    
    public static ElfHumanWizardChange1 provider() {
        return new ElfHumanWizardChange1();
    }
    
    static {
        ElfHumanWizardChange1.NPCS = new int[] { 30037, 30070, 30289 };
    }
}
