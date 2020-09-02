// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.CastleWarehouse;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class CastleWarehouse extends AbstractNpcAI
{
    private static final int[] NPCS;
    private static final int BLOOD_OATH = 9910;
    private static final int BLOOD_ALLIANCE = 9911;
    
    private CastleWarehouse() {
        this.addStartNpc(CastleWarehouse.NPCS);
        this.addTalkId(CastleWarehouse.NPCS);
        this.addFirstTalkId(CastleWarehouse.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = event;
        final boolean isMyLord = player.isClanLeader() && player.getClan().getCastleId() == ((npc.getCastle() != null) ? npc.getCastle().getId() : -1);
        switch (event) {
            case "warehouse-01.html":
            case "warehouse-02.html":
            case "warehouse-03.html": {
                break;
            }
            case "warehouse-04.html": {
                htmltext = (isMyLord ? this.getHtml(player, "warehouse-04.html").replace("%blood%", Integer.toString(player.getClan().getBloodAllianceCount())) : "warehouse-no.html");
                break;
            }
            case "Receive": {
                if (!isMyLord) {
                    htmltext = "warehouse-no.html";
                    break;
                }
                if (player.getClan().getBloodAllianceCount() == 0) {
                    htmltext = "warehouse-05.html";
                    break;
                }
                giveItems(player, 9911, (long)player.getClan().getBloodAllianceCount());
                player.getClan().resetBloodAllianceCount();
                htmltext = "warehouse-06.html";
                break;
            }
            case "Exchange": {
                if (!isMyLord) {
                    htmltext = "warehouse-no.html";
                    break;
                }
                if (!hasQuestItems(player, 9911)) {
                    htmltext = "warehouse-08.html";
                    break;
                }
                takeItems(player, 9911, 1L);
                giveItems(player, 9910, 30L);
                htmltext = "warehouse-07.html";
                break;
            }
            default: {
                htmltext = null;
                break;
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return "warehouse-01.html";
    }
    
    public static AbstractNpcAI provider() {
        return new CastleWarehouse();
    }
    
    static {
        NPCS = new int[] { 35099, 35141, 35183, 35225, 35273, 35315, 35362, 35508, 35554 };
    }
}
