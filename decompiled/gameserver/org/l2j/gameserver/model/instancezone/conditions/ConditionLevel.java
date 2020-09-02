// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

public final class ConditionLevel extends Condition
{
    private final int _min;
    private final int _max;
    
    public ConditionLevel(final InstanceTemplate template, final StatsSet parameters, final boolean onlyLeader, final boolean showMessageAndHtml) {
        super(template, parameters, onlyLeader, showMessageAndHtml);
        this._min = Math.min(85, parameters.getInt("min", 1));
        this._max = Math.min(85, parameters.getInt("max", Integer.MAX_VALUE));
        this.setSystemMessage(SystemMessageId.C1_S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY, (msg, player) -> msg.addString(player.getName()));
    }
    
    @Override
    protected boolean test(final Player player, final Npc npc) {
        return player.getLevel() >= this._min && player.getLevel() <= this._max;
    }
}
