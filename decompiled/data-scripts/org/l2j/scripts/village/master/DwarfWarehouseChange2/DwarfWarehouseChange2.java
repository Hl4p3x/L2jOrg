// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.DwarfWarehouseChange2;

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class DwarfWarehouseChange2 extends AbstractNpcAI
{
    private static int[] NPCS;
    private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
    private static final int MARK_OF_SEARCHER = 2809;
    private static final int MARK_OF_GUILDSMAN = 3119;
    private static final int MARK_OF_PROSPERITY = 3238;
    private static final int BOUNTY_HUNTER = 55;
    
    private DwarfWarehouseChange2() {
        this.addStartNpc(DwarfWarehouseChange2.NPCS);
        this.addTalkId(DwarfWarehouseChange2.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "30511-03.htm":
            case "30511-04.htm":
            case "30511-05.htm": {
                htmltext = event;
                break;
            }
            case "55": {
                htmltext = this.ClassChangeRequested(player, Integer.valueOf(event));
                break;
            }
        }
        return htmltext;
    }
    
    private String ClassChangeRequested(final Player player, final int classId) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP)) {
            htmltext = "30511-08.htm";
        }
        else if (classId == 55 && player.getClassId() == ClassId.SCAVENGER) {
            if (player.getLevel() < 40) {
                if (hasQuestItems(player, new int[] { 3119, 3238, 2809 })) {
                    htmltext = "30511-09.htm";
                }
                else {
                    htmltext = "30511-10.htm";
                }
            }
            else if (hasQuestItems(player, new int[] { 3119, 3238, 2809 })) {
                takeItems(player, -1, new int[] { 3119, 3238, 2809 });
                player.setClassId(55);
                player.setBaseClass(55);
                player.broadcastUserInfo();
                giveItems(player, 8870, 15L);
                htmltext = "30511-11.htm";
            }
            else {
                htmltext = "30511-12.htm";
            }
        }
        return htmltext;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        String htmltext = null;
        if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && player.isInCategory(CategoryType.BOUNTY_HUNTER_GROUP)) {
            htmltext = "30511-01.htm";
        }
        else if (player.isInCategory(CategoryType.BOUNTY_HUNTER_GROUP)) {
            final ClassId classId = player.getClassId();
            if (classId == ClassId.SCAVENGER || classId == ClassId.BOUNTY_HUNTER) {
                htmltext = "30511-02.htm";
            }
            else {
                htmltext = "30511-06.htm";
            }
        }
        else {
            htmltext = "30511-07.htm";
        }
        return htmltext;
    }
    
    public static DwarfWarehouseChange2 provider() {
        return new DwarfWarehouseChange2();
    }
    
    static {
        DwarfWarehouseChange2.NPCS = new int[] { 30511, 30676, 30685, 30845, 30894 };
    }
}
