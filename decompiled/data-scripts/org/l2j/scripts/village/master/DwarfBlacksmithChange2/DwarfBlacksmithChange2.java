// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.DwarfBlacksmithChange2;

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class DwarfBlacksmithChange2 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
    private static final int MARK_OF_MAESTRO = 2867;
    private static final int MARK_OF_GUILDSMAN = 3119;
    private static final int MARK_OF_PROSPERITY = 3238;
    private static final int WARSMITH = 57;
    
    private DwarfBlacksmithChange2() {
        this.addStartNpc(DwarfBlacksmithChange2.NPCS);
        this.addTalkId(DwarfBlacksmithChange2.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "30512-03.htm":
            case "30512-04.htm":
            case "30512-05.htm": {
                htmltext = event;
                break;
            }
            case "57": {
                htmltext = this.ClassChangeRequested(player, Integer.valueOf(event));
                break;
            }
        }
        return htmltext;
    }
    
    private String ClassChangeRequested(final Player player, final int classId) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
            htmltext = "30512-08.htm";
        }
        else if (classId == 57 && player.getClassId() == ClassId.ARTISAN) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 3119, 3238, 2867 })) {
                    htmltext = "30512-09.htm";
                }
                else {
                    htmltext = "30512-10.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 3119, 3238, 2867 })) {
                takeItems(player, -1, new int[] { 3119, 3238, 2867 });
                player.setClassId(57);
                player.setBaseClass(57);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30512-11.htm";
            }
            else {
                htmltext = "30512-12.htm";
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) {
            htmltext = "30512-01.htm";
        }
        else if (player.isInCategory(CategoryType.WARSMITH_GROUP)) {
            final ClassId classId = player.getClassId();
            if (classId == ClassId.ARTISAN || classId == ClassId.WARSMITH) {
                htmltext = "30512-02.htm";
            }
            else {
                htmltext = "30512-06.htm";
            }
        }
        else {
            htmltext = "30512-07.htm";
        }
        return htmltext;
    }
    
    public static DwarfBlacksmithChange2 provider() {
        return new DwarfBlacksmithChange2();
    }
    
    static {
        DwarfBlacksmithChange2.NPCS = new int[] { 30512, 30677, 30687, 30847, 30897, 31272 };
    }
}
