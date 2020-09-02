// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

public final class ConditionParty extends Condition
{
    public ConditionParty(final InstanceTemplate template, final StatsSet parameters, final boolean onlyLeader, final boolean showMessageAndHtml) {
        super(template, parameters, true, showMessageAndHtml);
        this.setSystemMessage(SystemMessageId.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
    }
    
    public boolean test(final Player player, final Npc npc) {
        return player.isInParty();
    }
}
