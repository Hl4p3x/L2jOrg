// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.ElfHumanFighterChange2;

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class ElfHumanFighterChange2 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
    private static final int MARK_OF_CHALLENGER = 2627;
    private static final int MARK_OF_DUTY = 2633;
    private static final int MARK_OF_SEEKER = 2673;
    private static final int MARK_OF_TRUST = 2734;
    private static final int MARK_OF_DUELIST = 2762;
    private static final int MARK_OF_SEARCHER = 2809;
    private static final int MARK_OF_HEALER = 2820;
    private static final int MARK_OF_LIFE = 3140;
    private static final int MARK_OF_CHAMPION = 3276;
    private static final int MARK_OF_SAGITTARIUS = 3293;
    private static final int MARK_OF_WITCHCRAFT = 3307;
    private static final int GLADIATOR = 2;
    private static final int WARLORD = 3;
    private static final int PALADIN = 5;
    private static final int DARK_AVENGER = 6;
    private static final int TREASURE_HUNTER = 8;
    private static final int HAWKEYE = 9;
    private static final int TEMPLE_KNIGHT = 20;
    private static final int SWORDSINGER = 21;
    private static final int PLAINS_WALKER = 23;
    private static final int SILVER_RANGER = 24;
    
    private ElfHumanFighterChange2() {
        this.addStartNpc(ElfHumanFighterChange2.NPCS);
        this.addTalkId(ElfHumanFighterChange2.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "30109-02.htm":
            case "30109-03.htm":
            case "30109-04.htm":
            case "30109-05.htm":
            case "30109-06.htm":
            case "30109-07.htm":
            case "30109-08.htm":
            case "30109-09.htm":
            case "30109-10.htm":
            case "30109-11.htm":
            case "30109-12.htm":
            case "30109-13.htm":
            case "30109-14.htm":
            case "30109-15.htm":
            case "30109-16.htm":
            case "30109-17.htm":
            case "30109-18.htm":
            case "30109-19.htm":
            case "30109-20.htm":
            case "30109-21.htm":
            case "30109-22.htm":
            case "30109-23.htm":
            case "30109-24.htm":
            case "30109-25.htm":
            case "30109-26.htm":
            case "30109-27.htm":
            case "30109-28.htm":
            case "30109-29.htm":
            case "30109-30.htm":
            case "30109-31.htm":
            case "30109-32.htm":
            case "30109-33.htm":
            case "30109-34.htm":
            case "30109-35.htm":
            case "30109-36.htm": {
                htmltext = event;
                break;
            }
            case "2":
            case "3":
            case "5":
            case "6":
            case "8":
            case "9":
            case "20":
            case "21":
            case "23":
            case "24": {
                htmltext = this.ClassChangeRequested(player, Integer.valueOf(event));
                break;
            }
        }
        return htmltext;
    }
    
    private String ClassChangeRequested(final Player player, final int classId) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
            htmltext = "30109-39.htm";
        }
        else if (classId == 2 && player.getClassId() == ClassId.WARRIOR) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2627, 2734, 2762 })) {
                    htmltext = "30109-40.htm";
                }
                else {
                    htmltext = "30109-41.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2627, 2734, 2762 })) {
                takeItems(player, -1, new int[] { 2627, 2734, 2762 });
                player.setClassId(2);
                player.setBaseClass(2);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30109-42.htm";
            }
            else {
                htmltext = "30109-43.htm";
            }
        }
        else if (classId == 3 && player.getClassId() == ClassId.WARRIOR) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2627, 2734, 3276 })) {
                    htmltext = "30109-44.htm";
                }
                else {
                    htmltext = "30109-45.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2627, 2734, 3276 })) {
                takeItems(player, -1, new int[] { 2627, 2734, 3276 });
                player.setClassId(3);
                player.setBaseClass(3);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30109-46.htm";
            }
            else {
                htmltext = "30109-47.htm";
            }
        }
        else if (classId == 5 && player.getClassId() == ClassId.KNIGHT) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2633, 2734, 2820 })) {
                    htmltext = "30109-48.htm";
                }
                else {
                    htmltext = "30109-49.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2633, 2734, 2820 })) {
                takeItems(player, -1, new int[] { 2633, 2734, 2820 });
                player.setClassId(5);
                player.setBaseClass(5);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30109-50.htm";
            }
            else {
                htmltext = "30109-51.htm";
            }
        }
        else if (classId == 6 && player.getClassId() == ClassId.KNIGHT) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2633, 2734, 3307 })) {
                    htmltext = "30109-52.htm";
                }
                else {
                    htmltext = "30109-53.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2633, 2734, 3307 })) {
                takeItems(player, -1, new int[] { 2633, 2734, 3307 });
                player.setClassId(6);
                player.setBaseClass(6);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30109-54.htm";
            }
            else {
                htmltext = "30109-55.htm";
            }
        }
        else if (classId == 8 && player.getClassId() == ClassId.ROGUE) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2673, 2734, 2809 })) {
                    htmltext = "30109-56.htm";
                }
                else {
                    htmltext = "30109-57.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2673, 2734, 2809 })) {
                takeItems(player, -1, new int[] { 2673, 2734, 2809 });
                player.setClassId(8);
                player.setBaseClass(8);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30109-58.htm";
            }
            else {
                htmltext = "30109-59.htm";
            }
        }
        else if (classId == 9 && player.getClassId() == ClassId.ROGUE) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2673, 2734, 3293 })) {
                    htmltext = "30109-60.htm";
                }
                else {
                    htmltext = "30109-61.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2673, 2734, 3293 })) {
                takeItems(player, -1, new int[] { 2673, 2734, 3293 });
                player.setClassId(9);
                player.setBaseClass(9);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30109-62.htm";
            }
            else {
                htmltext = "30109-63.htm";
            }
        }
        else if (classId == 20 && player.getClassId() == ClassId.ELVEN_KNIGHT) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2633, 3140, 2820 })) {
                    htmltext = "30109-64.htm";
                }
                else {
                    htmltext = "30109-65.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2633, 3140, 2820 })) {
                takeItems(player, -1, new int[] { 2633, 3140, 2820 });
                player.setClassId(20);
                player.setBaseClass(20);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30109-66.htm";
            }
            else {
                htmltext = "30109-67.htm";
            }
        }
        else if (classId == 21 && player.getClassId() == ClassId.ELVEN_KNIGHT) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2627, 3140, 2762 })) {
                    htmltext = "30109-68.htm";
                }
                else {
                    htmltext = "30109-69.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2627, 3140, 2762 })) {
                takeItems(player, -1, new int[] { 2627, 3140, 2762 });
                player.setClassId(21);
                player.setBaseClass(21);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30109-70.htm";
            }
            else {
                htmltext = "30109-71.htm";
            }
        }
        else if (classId == 23 && player.getClassId() == ClassId.ELVEN_SCOUT) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2673, 3140, 2809 })) {
                    htmltext = "30109-72.htm";
                }
                else {
                    htmltext = "30109-73.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2673, 3140, 2809 })) {
                takeItems(player, -1, new int[] { 2673, 3140, 2809 });
                player.setClassId(23);
                player.setBaseClass(23);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30109-74.htm";
            }
            else {
                htmltext = "30109-75.htm";
            }
        }
        else if (classId == 24 && player.getClassId() == ClassId.ELVEN_SCOUT) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2673, 3140, 3293 })) {
                    htmltext = "30109-76.htm";
                }
                else {
                    htmltext = "30109-77.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2673, 3140, 3293 })) {
                takeItems(player, -1, new int[] { 2673, 3140, 3293 });
                player.setClassId(24);
                player.setBaseClass(24);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30109-78.htm";
            }
            else {
                htmltext = "30109-79.htm";
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.FIGHTER_GROUP) && player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.isInCategory(CategoryType.HUMAN_FALL_CLASS) || player.isInCategory(CategoryType.ELF_FALL_CLASS))) {
            htmltext = "30109-01.htm";
        }
        else if (player.isInCategory(CategoryType.FIGHTER_GROUP) && (player.isInCategory(CategoryType.HUMAN_FALL_CLASS) || player.isInCategory(CategoryType.ELF_FALL_CLASS))) {
            final ClassId classId = player.getClassId();
            if (classId == ClassId.WARRIOR || classId == ClassId.GLADIATOR || classId == ClassId.WARLORD) {
                htmltext = "30109-02.htm";
            }
            else if (classId == ClassId.KNIGHT || classId == ClassId.PALADIN || classId == ClassId.DARK_AVENGER) {
                htmltext = "30109-09.htm";
            }
            else if (classId == ClassId.ROGUE || classId == ClassId.TREASURE_HUNTER || classId == ClassId.HAWKEYE) {
                htmltext = "30109-16.htm";
            }
            else if (classId == ClassId.ELVEN_KNIGHT || classId == ClassId.TEMPLE_KNIGHT || classId == ClassId.SWORDSINGER) {
                htmltext = "30109-23.htm";
            }
            else if (classId == ClassId.ELVEN_SCOUT || classId == ClassId.PLAINS_WALKER || classId == ClassId.SILVER_RANGER) {
                htmltext = "30109-30.htm";
            }
            else {
                htmltext = "30109-37.htm";
            }
        }
        else {
            htmltext = "30109-38.htm";
        }
        return htmltext;
    }
    
    public static ElfHumanFighterChange2 provider() {
        return new ElfHumanFighterChange2();
    }
    
    static {
        ElfHumanFighterChange2.NPCS = new int[] { 30109, 30187, 30689, 30849, 30900, 31276 };
    }
}
