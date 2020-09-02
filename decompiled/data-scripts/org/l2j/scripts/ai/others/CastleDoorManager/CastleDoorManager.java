// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.CastleDoorManager;

import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.entity.Castle;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class CastleDoorManager extends AbstractNpcAI
{
    private static final int[] DOORMENS_OUTTER;
    private static final int[] DOORMENS_INNER;
    
    private CastleDoorManager() {
        this.addStartNpc(CastleDoorManager.DOORMENS_OUTTER);
        this.addStartNpc(CastleDoorManager.DOORMENS_INNER);
        this.addTalkId(CastleDoorManager.DOORMENS_OUTTER);
        this.addTalkId(CastleDoorManager.DOORMENS_INNER);
        this.addFirstTalkId(CastleDoorManager.DOORMENS_OUTTER);
        this.addFirstTalkId(CastleDoorManager.DOORMENS_INNER);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final StringTokenizer st = new StringTokenizer(event, " ");
        final String action = st.nextToken();
        String htmltext = null;
        final String s = action;
        switch (s) {
            case "manageDoors": {
                if (!this.isOwningClan(player, npc) || !st.hasMoreTokens()) {
                    htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getHtmlName(npc));
                    break;
                }
                if (npc.getCastle().getSiege().isInProgress()) {
                    htmltext = "CastleDoorManager-siege.html";
                    break;
                }
                final Castle castle = npc.getCastle();
                final boolean open = st.nextToken().equals("1");
                final String doorName1 = npc.getParameters().getString("DoorName1", (String)null);
                final String doorName2 = npc.getParameters().getString("DoorName2", (String)null);
                castle.openCloseDoor(player, doorName1, open);
                castle.openCloseDoor(player, doorName2, open);
                break;
            }
            case "teleport": {
                if (this.isOwningClan(player, npc) && st.hasMoreTokens()) {
                    final int param = Integer.parseInt(st.nextToken());
                    if (param == 1) {
                        final int x = npc.getParameters().getInt("pos_x01");
                        final int y = npc.getParameters().getInt("pos_y01");
                        final int z = npc.getParameters().getInt("pos_z01");
                        player.teleToLocation(x, y, z);
                    }
                    else {
                        final int x = npc.getParameters().getInt("pos_x02");
                        final int y = npc.getParameters().getInt("pos_y02");
                        final int z = npc.getParameters().getInt("pos_z02");
                        player.teleToLocation(x, y, z);
                    }
                    break;
                }
                htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getHtmlName(npc));
                break;
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return (this.isOwningClan(player, npc) && player.hasClanPrivilege(ClanPrivilege.CS_OPEN_DOOR)) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getHtmlName(npc)) : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getHtmlName(npc));
    }
    
    private String getHtmlName(final Npc npc) {
        return Util.contains(CastleDoorManager.DOORMENS_INNER, npc.getId()) ? "CastleDoorManager-Inner" : "CastleDoorManager-Outter";
    }
    
    private boolean isOwningClan(final Player player, final Npc npc) {
        return player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || (npc.getCastle().getOwnerId() == player.getClanId() && player.getClanId() != 0);
    }
    
    public static AbstractNpcAI provider() {
        return new CastleDoorManager();
    }
    
    static {
        DOORMENS_OUTTER = new int[] { 35096, 35138, 35180, 35222, 35267, 35312, 35356, 35503, 35548 };
        DOORMENS_INNER = new int[] { 35097, 35139, 35181, 35223, 35268, 35269, 35270, 35271, 35313, 35357, 35358, 35359, 35360, 35504, 35505, 35549, 35550, 35551, 35552 };
    }
}
