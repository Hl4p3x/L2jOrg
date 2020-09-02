// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import java.util.Objects;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneExit;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneEnter;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class PeaceZone extends Zone
{
    public PeaceZone(final int id) {
        super(id);
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        if (!this.isEnabled()) {
            return;
        }
        if (GameUtils.isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            if (player.getSiegeState() != 0 && Config.PEACE_ZONE_MODE == 1) {
                return;
            }
        }
        if (Config.PEACE_ZONE_MODE != 2) {
            creature.setInsideZone(ZoneType.PEACE, true);
            if (GameUtils.isPlayer(creature)) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPeaceZoneEnter(creature.getActingPlayer(), this), creature);
            }
        }
        if (!this.getAllowStore()) {
            creature.setInsideZone(ZoneType.NO_STORE, true);
        }
    }
    
    @Override
    protected void onExit(final Creature creature) {
        if (Config.PEACE_ZONE_MODE != 2) {
            creature.setInsideZone(ZoneType.PEACE, false);
            if (GameUtils.isPlayer(creature)) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPeaceZoneExit(creature.getActingPlayer()), creature);
            }
        }
        if (!this.getAllowStore()) {
            creature.setInsideZone(ZoneType.NO_STORE, false);
        }
    }
    
    @Override
    public void setEnabled(final boolean state) {
        super.setEnabled(state);
        if (state) {
            this.forEachPlayer(player -> {
                this.revalidateInZone(player);
                if (Objects.nonNull(player.getPet())) {
                    this.revalidateInZone(player.getPet());
                }
                player.getServitors().values().forEach(x$0 -> this.revalidateInZone(x$0));
            });
        }
        else {
            this.forEachCreature(x$0 -> this.removeCreature(x$0));
        }
    }
}
