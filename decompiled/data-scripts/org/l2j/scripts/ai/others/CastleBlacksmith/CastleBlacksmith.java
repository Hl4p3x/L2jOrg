// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.CastleBlacksmith;

import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class CastleBlacksmith extends AbstractNpcAI
{
    private static final int[] NPCS;
    
    private CastleBlacksmith() {
        this.addStartNpc(CastleBlacksmith.NPCS);
        this.addTalkId(CastleBlacksmith.NPCS);
        this.addFirstTalkId(CastleBlacksmith.NPCS);
    }
    
    private boolean hasRights(final Player player, final Npc npc) {
        final boolean isMyLord = player.isClanLeader() && player.getClan().getCastleId() == ((npc.getCastle() != null) ? npc.getCastle().getId() : -1);
        return player.canOverrideCond(PcCondOverride.CASTLE_CONDITIONS) || isMyLord || (player.getClanId() == npc.getCastle().getOwnerId() && player.hasClanPrivilege(ClanPrivilege.CS_MANOR_ADMIN));
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        return (event.equalsIgnoreCase(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId())) && this.hasRights(player, npc)) ? event : null;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return this.hasRights(player, npc) ? invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId()) : "no.html";
    }
    
    public static AbstractNpcAI provider() {
        return new CastleBlacksmith();
    }
    
    static {
        NPCS = new int[] { 35098, 35140, 35182, 35224, 35272, 35314, 35361, 35507, 35553 };
    }
}
