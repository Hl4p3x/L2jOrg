// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.DwarfWarehouseChange1;

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class DwarfWarehouseChange1 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
    private static final int RING_OF_RAVEN = 1642;
    private static final int SCAVENGER = 54;
    
    private DwarfWarehouseChange1() {
        this.addStartNpc(DwarfWarehouseChange1.NPCS);
        this.addTalkId(DwarfWarehouseChange1.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "30498-01.htm":
            case "30498-02.htm":
            case "30498-03.htm":
            case "30498-04.htm":
            case "30503-01.htm":
            case "30503-02.htm":
            case "30503-03.htm":
            case "30503-04.htm":
            case "30594-01.htm":
            case "30594-02.htm":
            case "30594-03.htm":
            case "30594-04.htm":
            case "32092-01.htm":
            case "32092-02.htm":
            case "32092-03.htm":
            case "32092-04.htm": {
                htmltext = event;
                break;
            }
            case "54": {
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
            htmltext = "30498-12.htm";
        }
        else if (classId == 54 && player.getClassId() == ClassId.DWARVEN_FIGHTER) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1642)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1642)) {
                takeItems(player, 1642, -1L);
                player.setClassId(54);
                player.setBaseClass(54);
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
        if (player.isInCategory(CategoryType.BOUNTY_HUNTER_GROUP)) {
            htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        else {
            htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        return htmltext;
    }
    
    public static DwarfWarehouseChange1 provider() {
        return new DwarfWarehouseChange1();
    }
    
    static {
        DwarfWarehouseChange1.NPCS = new int[] { 30498, 30503, 30594 };
    }
}
