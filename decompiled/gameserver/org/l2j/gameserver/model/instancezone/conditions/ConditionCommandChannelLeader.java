// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.model.AbstractPlayerGroup;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

public final class ConditionCommandChannelLeader extends Condition
{
    public ConditionCommandChannelLeader(final InstanceTemplate template, final StatsSet parameters, final boolean onlyLeader, final boolean showMessageAndHtml) {
        super(template, parameters, true, showMessageAndHtml);
        this.setSystemMessage(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
    }
    
    public boolean test(final Player player, final Npc npc) {
        final AbstractPlayerGroup group = player.getCommandChannel();
        return group != null && group.isLeader(player);
    }
}
