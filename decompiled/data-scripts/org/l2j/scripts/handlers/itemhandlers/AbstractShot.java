// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayeableChargeShots;
import org.l2j.gameserver.model.events.EventDispatcher;
import java.util.Collection;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public abstract class AbstractShot implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        final Player player = playable.getActingPlayer();
        final List<ItemSkillHolder> skills = (List<ItemSkillHolder>)item.getSkills(ItemSkillType.NORMAL);
        if (Util.isNullOrEmpty((Collection)skills)) {
            AbstractShot.LOGGER.warn("item {} is missing skills!", (Object)item);
            return false;
        }
        if (!this.canUse(player)) {
            return false;
        }
        player.chargeShot(this.getShotType(), this.getBonus(player));
        player.sendPacket(this.getEnabledShotsMessage());
        EventDispatcher.getInstance().notifyEventAsync((IBaseEvent)new OnPlayeableChargeShots((Playable)player, this.getShotType(), this.isBlessed()), new ListenersContainer[] { (ListenersContainer)player });
        final Creature creature;
        skills.forEach(holder -> Broadcast.toSelfAndKnownPlayersInRadius(creature, (ServerPacket)new MagicSkillUse(creature, (WorldObject)creature, holder.getSkillId(), holder.getLevel(), 0, 0), 600));
        return true;
    }
    
    protected abstract boolean isBlessed();
    
    protected abstract double getBonus(final Player player);
    
    protected abstract boolean canUse(final Player player);
    
    protected abstract ShotType getShotType();
    
    protected abstract SystemMessageId getEnabledShotsMessage();
}
