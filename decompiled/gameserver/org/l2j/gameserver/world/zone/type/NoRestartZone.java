// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.Zone;

public class NoRestartZone extends Zone
{
    private int restartAllowedTime;
    private int restartTime;
    private boolean enabled;
    
    public NoRestartZone(final int id) {
        super(id);
        this.restartAllowedTime = 0;
        this.restartTime = 0;
        this.enabled = true;
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        if (name.equalsIgnoreCase("default_enabled")) {
            this.enabled = Boolean.parseBoolean(value);
        }
        else if (name.equalsIgnoreCase("restartAllowedTime")) {
            this.restartAllowedTime = Integer.parseInt(value) * 1000;
        }
        else if (name.equalsIgnoreCase("restartTime")) {
            this.restartTime = Integer.parseInt(value) * 1000;
        }
        else if (!name.equalsIgnoreCase("instanceId")) {
            super.setParameter(name, value);
        }
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        if (!this.enabled) {
            return;
        }
        if (GameUtils.isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_RESTART, true);
        }
    }
    
    @Override
    protected void onExit(final Creature creature) {
        if (!this.enabled) {
            return;
        }
        if (GameUtils.isPlayer(creature)) {
            creature.setInsideZone(ZoneType.NO_RESTART, false);
        }
    }
    
    @Override
    public void onPlayerLoginInside(final Player player) {
        if (!this.enabled) {
            return;
        }
        if (System.currentTimeMillis() - player.getLastAccess() > this.restartTime && System.currentTimeMillis() - player.getLastAccess() > this.restartAllowedTime) {
            player.teleToLocation(TeleportWhereType.TOWN);
        }
    }
}
