// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayeableChargeShots;
import org.l2j.gameserver.model.events.EventDispatcher;
import java.util.Iterator;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Collection;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.ItemSkillType;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import java.util.List;
import java.util.Objects;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public abstract class AbstractBeastShot implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        final Player owner = playable.getActingPlayer();
        if (!owner.hasSummon()) {
            owner.sendPacket(SystemMessageId.SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            return false;
        }
        final Summon pet = playable.getPet();
        if (Objects.nonNull(pet) && pet.isDead()) {
            owner.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_SERVITOR_SAD_ISN_T_IT);
            return false;
        }
        final List<Summon> aliveServitors = playable.getServitors().values().stream().filter(Predicate.not(Creature::isDead)).collect((Collector<? super Object, ?, List<Summon>>)Collectors.toList());
        if (Objects.isNull(pet) && aliveServitors.isEmpty()) {
            owner.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_SERVITOR_SAD_ISN_T_IT);
            return false;
        }
        final List<ItemSkillHolder> skills = (List<ItemSkillHolder>)item.getSkills(ItemSkillType.NORMAL);
        if (Util.isNullOrEmpty((Collection)skills)) {
            AbstractBeastShot.LOGGER.warn("item {} is missing skills!", (Object)item);
            return false;
        }
        short shotConsumption = 0;
        final ShotType shotType = this.getShotType();
        if (Objects.nonNull(pet) && !pet.isChargedShot(shotType)) {
            shotConsumption += pet.getSoulShotsPerHit();
        }
        for (final Summon servitor : aliveServitors) {
            if (!servitor.isChargedShot(shotType)) {
                shotConsumption += servitor.getSoulShotsPerHit();
            }
        }
        if (item.getCount() < shotConsumption) {
            return false;
        }
        if (Objects.nonNull(pet)) {
            this.chargeShot(owner, skills, shotType, pet);
        }
        aliveServitors.forEach(s -> this.chargeShot(owner, skills, shotType, s));
        return true;
    }
    
    private void chargeShot(final Player owner, final List<ItemSkillHolder> skills, final ShotType shotType, final Summon s) {
        this.sendUsesMessage(owner);
        s.chargeShot(shotType, this.getBonus(s));
        EventDispatcher.getInstance().notifyEventAsync((IBaseEvent)new OnPlayeableChargeShots((Playable)s, shotType, this.isBlessed()), new ListenersContainer[] { (ListenersContainer)owner });
        skills.forEach(holder -> Broadcast.toSelfAndKnownPlayersInRadius((Creature)owner, (ServerPacket)new MagicSkillUse((Creature)s, (WorldObject)s, holder.getSkillId(), holder.getLevel(), 0, 0), 600));
    }
    
    protected abstract boolean isBlessed();
    
    protected abstract double getBonus(final Summon summon);
    
    protected abstract ShotType getShotType();
    
    protected abstract void sendUsesMessage(final Player player);
}
