// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Map;
import org.l2j.gameserver.model.conditions.ICondition;
import java.util.List;

public class ExtendDropDataHolder
{
    private final int _id;
    private final List<ExtendDropItemHolder> _items;
    private final List<ICondition> _conditions;
    private final Map<Long, SystemMessageId> _systemMessages;
    
    public ExtendDropDataHolder(final StatsSet set) {
        this._id = set.getInt("id");
        this._items = set.getList("items", ExtendDropItemHolder.class);
        this._conditions = set.getList("conditions", ICondition.class);
        this._systemMessages = set.getMap("systemMessages", Long.class, SystemMessageId.class);
    }
    
    public void reward(final Player player, final Npc npc) {
        if (this._conditions.isEmpty() || this._conditions.stream().allMatch(cond -> cond.test(player, npc))) {
            final long currentAmount;
            boolean sendMessage;
            long newAmount;
            SystemMessageId systemMessageId;
            this._items.forEach(i -> {
                currentAmount = player.getExtendDropCount(this._id);
                if (Rnd.nextDouble() < i.getChance() && currentAmount < i.getMaxCount()) {
                    sendMessage = true;
                    newAmount = currentAmount + i.getCount();
                    if (this._systemMessages != null) {
                        systemMessageId = this._systemMessages.get(newAmount);
                        if (systemMessageId != null) {
                            sendMessage = false;
                            player.sendPacket(systemMessageId);
                        }
                    }
                    player.addItem("ExtendDrop", i.getId(), i.getCount(), player, sendMessage);
                    player.updateExtendDrop(this._id, newAmount);
                }
            });
        }
    }
}
