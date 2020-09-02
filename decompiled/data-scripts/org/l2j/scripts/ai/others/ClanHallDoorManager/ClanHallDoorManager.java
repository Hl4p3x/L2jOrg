// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.ClanHallDoorManager;

import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.model.ClanPrivilege;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class ClanHallDoorManager extends AbstractNpcAI
{
    private static final int[] DOOR_MANAGERS;
    
    private ClanHallDoorManager() {
        this.addStartNpc(ClanHallDoorManager.DOOR_MANAGERS);
        this.addTalkId(ClanHallDoorManager.DOOR_MANAGERS);
        this.addFirstTalkId(ClanHallDoorManager.DOOR_MANAGERS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final StringTokenizer st = new StringTokenizer(event, " ");
        final String action = st.nextToken();
        final ClanHall clanHall = npc.getClanHall();
        String htmltext = null;
        if (clanHall != null) {
            final String s = action;
            switch (s) {
                case "index": {
                    htmltext = this.onFirstTalk(npc, player);
                    break;
                }
                case "manageDoors": {
                    if (this.isOwningClan(player, npc) && st.hasMoreTokens() && player.hasClanPrivilege(ClanPrivilege.CH_OPEN_DOOR)) {
                        final boolean open = st.nextToken().equals("1");
                        clanHall.openCloseDoors(open);
                        htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, open ? "5" : "6");
                        break;
                    }
                    htmltext = "ClanHallDoorManager-04.html";
                    break;
                }
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        String htmltext = null;
        final ClanHall clanHall = npc.getClanHall();
        if (this.isOwningClan(player, npc)) {
            htmltext = this.getHtml(player, "ClanHallDoorManager-01.html");
            htmltext = htmltext.replace("%ownerClanName%", clanHall.getOwner().getName());
        }
        else if (clanHall.getOwnerId() <= 0) {
            htmltext = "ClanHallDoorManager-02.html";
        }
        else {
            htmltext = this.getHtml(player, "ClanHallDoorManager-03.html");
            htmltext = htmltext.replace("%ownerName%", clanHall.getOwner().getLeaderName());
            htmltext = htmltext.replace("%ownerClanName%", clanHall.getOwner().getName());
        }
        return htmltext;
    }
    
    private boolean isOwningClan(final Player player, final Npc npc) {
        return npc.getClanHall().getOwnerId() == player.getClanId() && player.getClanId() != 0;
    }
    
    public static AbstractNpcAI provider() {
        return new ClanHallDoorManager();
    }
    
    static {
        DOOR_MANAGERS = new int[] { 35385, 35387, 35389, 35391, 35393, 35395, 35397, 35399, 35401, 35402, 35404, 35406, 35440, 35442, 35444, 35446, 35448, 35450, 35452, 35454, 35456, 35458, 35460, 35462, 35464, 35466, 35468, 35567, 35569, 35571, 35573, 35575, 35577, 35579, 35581, 35583, 35585, 35587, 36722, 36724, 36726, 36728, 36730, 36732, 36734, 36736, 36738, 36740 };
    }
}
