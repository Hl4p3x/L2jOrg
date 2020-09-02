// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.CastleSiegeManager;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class CastleSiegeManager extends AbstractNpcAI
{
    private static final int[] SIEGE_MANAGER;
    
    private CastleSiegeManager() {
        this.addFirstTalkId(CastleSiegeManager.SIEGE_MANAGER);
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        String htmltext = null;
        if (player.isClanLeader() && player.getClanId() == npc.getCastle().getOwnerId()) {
            if (this.isInSiege(npc)) {
                htmltext = "CastleSiegeManager.html";
            }
            else {
                htmltext = "CastleSiegeManager-01.html";
            }
        }
        else if (this.isInSiege(npc)) {
            htmltext = "CastleSiegeManager-02.html";
        }
        else {
            npc.getCastle().getSiege().listRegisterClan(player);
        }
        return htmltext;
    }
    
    private boolean isInSiege(final Npc npc) {
        return npc.getCastle().getSiege().isInProgress();
    }
    
    public static AbstractNpcAI provider() {
        return new CastleSiegeManager();
    }
    
    static {
        SIEGE_MANAGER = new int[] { 35104, 35146, 35188, 35232, 35278, 35320, 35367, 35513, 35559 };
    }
}
