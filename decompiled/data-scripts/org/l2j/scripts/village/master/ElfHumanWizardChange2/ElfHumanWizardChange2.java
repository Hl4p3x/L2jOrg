// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.ElfHumanWizardChange2;

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class ElfHumanWizardChange2 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
    private static final int MARK_OF_SCHOLAR = 2674;
    private static final int MARK_OF_TRUST = 2734;
    private static final int MARK_OF_MAGUS = 2840;
    private static final int MARK_OF_WITCHCRAFT = 3307;
    private static final int MARK_OF_SUMMONER = 3336;
    private static final int MARK_OF_LIFE = 3140;
    private static final int SORCERER = 12;
    private static final int NECROMANCER = 13;
    private static final int WARLOCK = 14;
    private static final int SPELLSINGER = 27;
    private static final int ELEMENTAL_SUMMONER = 28;
    
    private ElfHumanWizardChange2() {
        this.addStartNpc(ElfHumanWizardChange2.NPCS);
        this.addTalkId(ElfHumanWizardChange2.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "30115-02.htm":
            case "30115-03.htm":
            case "30115-04.htm":
            case "30115-05.htm":
            case "30115-06.htm":
            case "30115-07.htm":
            case "30115-08.htm":
            case "30115-09.htm":
            case "30115-10.htm":
            case "30115-11.htm":
            case "30115-12.htm":
            case "30115-13.htm":
            case "30115-14.htm":
            case "30115-15.htm":
            case "30115-16.htm":
            case "30115-17.htm":
            case "30115-18.htm": {
                htmltext = event;
                break;
            }
            case "12":
            case "13":
            case "14":
            case "27":
            case "28": {
                htmltext = this.ClassChangeRequested(player, Integer.valueOf(event));
                break;
            }
        }
        return htmltext;
    }
    
    private String ClassChangeRequested(final Player player, final int classId) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
            htmltext = "30115-21.htm";
        }
        else if (classId == 12 && player.getClassId() == ClassId.WIZARD) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2674, 2734, 2840 })) {
                    htmltext = "30115-22.htm";
                }
                else {
                    htmltext = "30115-23.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2674, 2734, 2840 })) {
                takeItems(player, -1, new int[] { 2674, 2734, 2840 });
                player.setClassId(12);
                player.setBaseClass(12);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30115-24.htm";
            }
            else {
                htmltext = "30115-25.htm";
            }
        }
        else if (classId == 13 && player.getClassId() == ClassId.WIZARD) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2674, 2734, 3307 })) {
                    htmltext = "30115-26.htm";
                }
                else {
                    htmltext = "30115-27.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2674, 2734, 3307 })) {
                takeItems(player, -1, new int[] { 2674, 2734, 3307 });
                player.setClassId(13);
                player.setBaseClass(13);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30115-28.htm";
            }
            else {
                htmltext = "30115-29.htm";
            }
        }
        else if (classId == 14 && player.getClassId() == ClassId.WIZARD) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2674, 2734, 3336 })) {
                    htmltext = "30115-30.htm";
                }
                else {
                    htmltext = "30115-31.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2674, 2734, 3336 })) {
                takeItems(player, -1, new int[] { 2674, 2734, 3336 });
                player.setClassId(14);
                player.setBaseClass(14);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30115-32.htm";
            }
            else {
                htmltext = "30115-33.htm";
            }
        }
        else if (classId == 27 && player.getClassId() == ClassId.ELVEN_WIZARD) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2674, 3140, 2840 })) {
                    htmltext = "30115-34.htm";
                }
                else {
                    htmltext = "30115-35.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2674, 3140, 2840 })) {
                takeItems(player, -1, new int[] { 2674, 3140, 2840 });
                player.setClassId(27);
                player.setBaseClass(27);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30115-36.htm";
            }
            else {
                htmltext = "30115-37.htm";
            }
        }
        else if (classId == 28 && player.getClassId() == ClassId.ELVEN_WIZARD) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 2674, 3140, 3336 })) {
                    htmltext = "30115-38.htm";
                }
                else {
                    htmltext = "30115-39.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 2674, 3140, 3336 })) {
                takeItems(player, -1, new int[] { 2674, 3140, 3336 });
                player.setClassId(28);
                player.setBaseClass(28);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30115-40.htm";
            }
            else {
                htmltext = "30115-41.htm";
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.WIZARD_GROUP) && player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.isInCategory(CategoryType.HUMAN_MALL_CLASS) || player.isInCategory(CategoryType.ELF_MALL_CLASS))) {
            htmltext = "30115-01.htm";
        }
        else if (player.isInCategory(CategoryType.WIZARD_GROUP) && (player.isInCategory(CategoryType.HUMAN_MALL_CLASS) || player.isInCategory(CategoryType.ELF_MALL_CLASS))) {
            final ClassId classId = player.getClassId();
            if (classId == ClassId.WIZARD || classId == ClassId.SORCERER || classId == ClassId.NECROMANCER || classId == ClassId.WARLOCK) {
                htmltext = "30115-02.htm";
            }
            else if (classId == ClassId.ELVEN_WIZARD || classId == ClassId.SPELLSINGER || classId == ClassId.ELEMENTAL_SUMMONER) {
                htmltext = "30115-12.htm";
            }
            else {
                htmltext = "30115-19.htm";
            }
        }
        else {
            htmltext = "30115-20.htm";
        }
        return htmltext;
    }
    
    public static ElfHumanWizardChange2 provider() {
        return new ElfHumanWizardChange2();
    }
    
    static {
        ElfHumanWizardChange2.NPCS = new int[] { 30115, 30174, 30176, 30694, 30854, 31587 };
    }
}
