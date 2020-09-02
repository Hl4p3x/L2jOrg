// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

public final class ConditionItem extends Condition
{
    private final int _itemId;
    private final long _count;
    private final boolean _take;
    
    public ConditionItem(final InstanceTemplate template, final StatsSet parameters, final boolean onlyLeader, final boolean showMessageAndHtml) {
        super(template, parameters, onlyLeader, showMessageAndHtml);
        this._itemId = parameters.getInt("id");
        this._count = parameters.getLong("count");
        this._take = parameters.getBoolean("take", false);
        this.setSystemMessage(SystemMessageId.C1_S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED, (msg, player) -> msg.addString(player.getName()));
    }
    
    @Override
    protected boolean test(final Player player, final Npc npc) {
        return player.getInventory().getInventoryItemCount(this._itemId, -1) >= this._count;
    }
    
    @Override
    protected void onSuccess(final Player player) {
        if (this._take) {
            player.destroyItemByItemId("InstanceConditionDestroy", this._itemId, this._count, null, true);
        }
    }
}
