// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.OrcChange1;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class OrcChange1 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
    private static final int MARK_OF_RAIDER = 1592;
    private static final int KHAVATARI_TOTEM = 1615;
    private static final int MASK_OF_MEDIUM = 1631;
    
    private OrcChange1() {
        this.addStartNpc(OrcChange1.NPCS);
        this.addTalkId(OrcChange1.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "30500-01.htm":
            case "30500-02.htm":
            case "30500-03.htm":
            case "30500-04.htm":
            case "30500-05.htm":
            case "30500-06.htm":
            case "30500-07.htm":
            case "30500-08.htm":
            case "30505-01.htm":
            case "30505-02.htm":
            case "30505-03.htm":
            case "30505-04.htm":
            case "30505-05.htm":
            case "30505-06.htm":
            case "30505-07.htm":
            case "30505-08.htm":
            case "30508-01.htm":
            case "30508-02.htm":
            case "30508-03.htm":
            case "30508-04.htm":
            case "30508-05.htm":
            case "30508-06.htm":
            case "30508-07.htm":
            case "30508-08.htm":
            case "32097-01.htm":
            case "32097-02.htm":
            case "32097-03.htm":
            case "32097-04.htm":
            case "32097-05.htm":
            case "32097-06.htm":
            case "32097-07.htm":
            case "32097-08.htm": {
                htmltext = event;
                break;
            }
            case "45":
            case "47":
            case "50": {
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
            htmltext = "30500-24.htm";
        }
        else if (classId == 45 && player.getClassId() == ClassId.ORC_FIGHTER) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1592)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1592)) {
                takeItems(player, 1592, -1L);
                player.setClassId(45);
                player.setBaseClass(45);
                player.broadcastUserInfo();
                giveItems(player, 8869, 15L);
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        else if (classId == 47 && player.getClassId() == ClassId.ORC_FIGHTER) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1615)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1615)) {
                takeItems(player, 1615, -1L);
                player.setClassId(47);
                player.setBaseClass(47);
                player.broadcastUserInfo();
                giveItems(player, 8869, 15L);
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        else if (classId == 50 && player.getClassId() == ClassId.ORC_MAGE) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1631)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1631)) {
                takeItems(player, 1631, -1L);
                player.setClassId(50);
                player.setBaseClass(50);
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
        if (player.getRace() == Race.ORC) {
            if (player.isInCategory(CategoryType.FIGHTER_GROUP)) {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
            else if (player.isInCategory(CategoryType.MAGE_GROUP)) {
                htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
            }
        }
        else {
            htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        return htmltext;
    }
    
    public static OrcChange1 provider() {
        return new OrcChange1();
    }
    
    static {
        OrcChange1.NPCS = new int[] { 30500, 30505, 30508 };
    }
}
