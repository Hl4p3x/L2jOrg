// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.ElfHumanFighterChange1;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class ElfHumanFighterChange1 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
    private static final int MEDALLION_OF_WARRIOR = 1145;
    private static final int SWORD_OF_RITUAL = 1161;
    private static final int BEZIQUES_RECOMMENDATION = 1190;
    private static final int ELVEN_KNIGHT_BROOCH = 1204;
    private static final int REISAS_RECOMMENDATION = 1217;
    private static final int WARRIOR = 1;
    private static final int KNIGHT = 4;
    private static final int ROGUE = 7;
    private static final int ELVEN_KNIGHT = 19;
    private static final int ELVEN_SCOUT = 22;
    
    private ElfHumanFighterChange1() {
        this.addStartNpc(ElfHumanFighterChange1.NPCS);
        this.addTalkId(ElfHumanFighterChange1.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "30066-01.htm":
            case "30066-02.htm":
            case "30066-03.htm":
            case "30066-04.htm":
            case "30066-05.htm":
            case "30066-06.htm":
            case "30066-07.htm":
            case "30066-08.htm":
            case "30066-09.htm":
            case "30066-10.htm":
            case "30066-11.htm":
            case "30066-12.htm":
            case "30066-13.htm":
            case "30066-14.htm":
            case "30066-15.htm":
            case "30066-16.htm":
            case "30066-17.htm":
            case "30288-01.htm":
            case "30288-02.htm":
            case "30288-03.htm":
            case "30288-04.htm":
            case "30288-05.htm":
            case "30288-06.htm":
            case "30288-07.htm":
            case "30288-08.htm":
            case "30288-09.htm":
            case "30288-10.htm":
            case "30288-11.htm":
            case "30288-12.htm":
            case "30288-13.htm":
            case "30288-14.htm":
            case "30288-15.htm":
            case "30288-16.htm":
            case "30288-17.htm":
            case "30373-01.htm":
            case "30373-02.htm":
            case "30373-03.htm":
            case "30373-04.htm":
            case "30373-05.htm":
            case "30373-06.htm":
            case "30373-07.htm":
            case "30373-08.htm":
            case "30373-09.htm":
            case "30373-10.htm":
            case "30373-11.htm":
            case "30373-12.htm":
            case "30373-13.htm":
            case "30373-14.htm":
            case "30373-15.htm":
            case "30373-16.htm":
            case "30373-17.htm":
            case "32094-01.htm":
            case "32094-02.htm":
            case "32094-03.htm":
            case "32094-04.htm":
            case "32094-05.htm":
            case "32094-06.htm":
            case "32094-07.htm":
            case "32094-08.htm":
            case "32094-09.htm":
            case "32094-10.htm":
            case "32094-11.htm":
            case "32094-12.htm":
            case "32094-13.htm":
            case "32094-14.htm":
            case "32094-15.htm":
            case "32094-16.htm":
            case "32094-17.htm": {
                htmltext = event;
                break;
            }
            case "1":
            case "4":
            case "7":
            case "19":
            case "22": {
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
            htmltext = "30066-41.htm";
        }
        else if (classId == 1 && player.getClassId() == ClassId.FIGHTER) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1145)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1145)) {
                takeItems(player, 1145, -1L);
                player.setClassId(1);
                player.setBaseClass(1);
                player.broadcastUserInfo();
                giveItems(player, 8869, 15L);
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        else if (classId == 4 && player.getClassId() == ClassId.FIGHTER) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1161)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1161)) {
                takeItems(player, 1161, -1L);
                player.setClassId(4);
                player.setBaseClass(4);
                player.broadcastUserInfo();
                giveItems(player, 8869, 15L);
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        else if (classId == 7 && player.getClassId() == ClassId.FIGHTER) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1190)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1190)) {
                takeItems(player, 1190, -1L);
                player.setClassId(7);
                player.setBaseClass(7);
                player.broadcastUserInfo();
                giveItems(player, 8869, 15L);
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        else if (classId == 19 && player.getClassId() == ClassId.ELVEN_FIGHTER) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1204)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1204)) {
                takeItems(player, 1204, -1L);
                player.setClassId(19);
                player.setBaseClass(19);
                player.broadcastUserInfo();
                giveItems(player, 8869, 15L);
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        else if (classId == 22 && player.getClassId() == ClassId.ELVEN_FIGHTER) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1217)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1217)) {
                takeItems(player, 1217, -1L);
                player.setClassId(22);
                player.setBaseClass(22);
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
        if (player.isInCategory(CategoryType.FIGHTER_GROUP) && (playerRace == Race.HUMAN || playerRace == Race.ELF)) {
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
    
    public static ElfHumanFighterChange1 provider() {
        return new ElfHumanFighterChange1();
    }
    
    static {
        ElfHumanFighterChange1.NPCS = new int[] { 30066, 30288, 30373 };
    }
}
