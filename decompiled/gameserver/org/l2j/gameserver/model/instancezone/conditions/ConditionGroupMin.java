// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.network.serverpackets.SystemMessage;
import java.util.List;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

public final class ConditionGroupMin extends Condition
{
    public ConditionGroupMin(final InstanceTemplate template, final StatsSet parameters, final boolean onlyLeader, final boolean showMessageAndHtml) {
        super(template, parameters, true, showMessageAndHtml);
        this.setSystemMessage(SystemMessageId.YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANCED_ZONE, (msg, player) -> msg.addInt(this.getLimit()));
    }
    
    @Override
    protected boolean test(final Player player, final Npc npc, final List<Player> group) {
        return group.size() >= this.getLimit();
    }
    
    public int getLimit() {
        return this.getParameters().getInt("limit");
    }
}
