// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

public final class ConditionDistance extends Condition
{
    public ConditionDistance(final InstanceTemplate template, final StatsSet parameters, final boolean onlyLeader, final boolean showMessageAndHtml) {
        super(template, parameters, onlyLeader, showMessageAndHtml);
        this.setSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED, (message, player) -> message.addString(player.getName()));
    }
    
    public boolean test(final Player player, final Npc npc) {
        final int distance = this.getParameters().getInt("distance", 1000);
        return MathUtil.isInsideRadius3D(player, npc, distance);
    }
}
