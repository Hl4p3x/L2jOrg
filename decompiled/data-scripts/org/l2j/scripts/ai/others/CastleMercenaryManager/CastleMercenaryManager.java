// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.CastleMercenaryManager;

import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class CastleMercenaryManager extends AbstractNpcAI
{
    private static final int[] NPCS;
    
    private CastleMercenaryManager() {
        this.addStartNpc(CastleMercenaryManager.NPCS);
        this.addTalkId(CastleMercenaryManager.NPCS);
        this.addFirstTalkId(CastleMercenaryManager.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        final StringTokenizer st = new StringTokenizer(event, " ");
        final String nextToken = st.nextToken();
        switch (nextToken) {
            case "limit": {
                final Castle castle = npc.getCastle();
                final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
                if (castle.getName().equalsIgnoreCase("Aden")) {
                    html.setHtml(this.getHtml(player, "mercmanager-aden-limit.html"));
                }
                else if (castle.getName().equalsIgnoreCase("Rune")) {
                    html.setHtml(this.getHtml(player, "mercmanager-rune-limit.html"));
                }
                else {
                    html.setHtml(this.getHtml(player, "mercmanager-limit.html"));
                }
                html.replace("%feud_name%", String.valueOf(1001000 + castle.getId()));
                player.sendPacket(new ServerPacket[] { (ServerPacket)html });
                break;
            }
            case "buy": {
                final int listId = Integer.parseInt(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, npc.getId(), st.nextToken()));
                ((Merchant)npc).showBuyWindow(player, listId, false);
                break;
            }
            case "main": {
                htmltext = this.onFirstTalk(npc, player);
                break;
            }
            case "mercmanager-01.html": {
                htmltext = event;
                break;
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        String htmltext;
        if (player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || (player.getClanId() == npc.getCastle().getOwnerId() && player.hasClanPrivilege(ClanPrivilege.CS_MERCENARIES))) {
            htmltext = (npc.getCastle().getSiege().isInProgress() ? "mercmanager-siege.html" : "mercmanager.html");
        }
        else {
            htmltext = "mercmanager-no.html";
        }
        return htmltext;
    }
    
    public static AbstractNpcAI provider() {
        return new CastleMercenaryManager();
    }
    
    static {
        NPCS = new int[] { 35102, 35144, 35186, 35228, 35276, 35318, 35365, 35511, 35557 };
    }
}
