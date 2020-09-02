// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.DwarfBlacksmithChange1;

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class DwarfBlacksmithChange1 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static final int SHADOW_ITEM_EXCHANGE_COUPON_D_GRADE = 8869;
    private static final int FINAL_PASS_CERTIFICATE = 1635;
    private static final int ARTISAN = 56;
    
    private DwarfBlacksmithChange1() {
        this.addStartNpc(DwarfBlacksmithChange1.NPCS);
        this.addTalkId(DwarfBlacksmithChange1.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "30499-01.htm":
            case "30499-02.htm":
            case "30499-03.htm":
            case "30499-04.htm":
            case "30504-01.htm":
            case "30504-02.htm":
            case "30504-03.htm":
            case "30504-04.htm":
            case "30595-01.htm":
            case "30595-02.htm":
            case "30595-03.htm":
            case "30595-04.htm":
            case "32093-01.htm":
            case "32093-02.htm":
            case "32093-03.htm":
            case "32093-04.htm": {
                htmltext = event;
                break;
            }
            case "56": {
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
            htmltext = "30499-12.htm";
        }
        else if (classId == 56 && player.getClassId() == ClassId.DWARVEN_FIGHTER) {
            if (player.getLevel() < 20) {
                if (hasQuestItems(player, 1635)) {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
                else {
                    htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
                }
            }
            else if (hasQuestItems(player, 1635)) {
                takeItems(player, 1635, -1L);
                player.setClassId(56);
                player.setBaseClass(56);
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
        if (player.isInCategory(CategoryType.WARSMITH_GROUP)) {
            htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        else {
            htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        }
        return htmltext;
    }
    
    public static DwarfBlacksmithChange1 provider() {
        return new DwarfBlacksmithChange1();
    }
    
    static {
        DwarfBlacksmithChange1.NPCS = new int[] { 30499, 30504, 30595 };
    }
}
