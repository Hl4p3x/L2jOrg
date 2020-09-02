// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

public final class ConditionReenter extends Condition
{
    public ConditionReenter(final InstanceTemplate template, final StatsSet parameters, final boolean onlyLeader, final boolean showMessageAndHtml) {
        super(template, parameters, onlyLeader, showMessageAndHtml);
        this.setSystemMessage(SystemMessageId.C1_MAY_NOT_RE_ENTER_YET, (message, player) -> message.addString(player.getName()));
    }
    
    @Override
    protected boolean test(final Player player, final Npc npc) {
        final int instanceId = this.getParameters().getInt("instanceId", this.getInstanceTemplate().getId());
        return System.currentTimeMillis() > InstanceManager.getInstance().getInstanceTime(player, instanceId);
    }
}
