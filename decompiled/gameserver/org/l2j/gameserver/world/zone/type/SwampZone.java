// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.OnEventTrigger;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.world.zone.Zone;

public class SwampZone extends Zone
{
    private double moveBonus;
    private int castleId;
    private Castle castle;
    private int eventId;
    
    public SwampZone(final int id) {
        super(id);
        this.moveBonus = 0.5;
        this.castleId = 0;
        this.castle = null;
        this.eventId = 0;
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        switch (name) {
            case "move_bonus": {
                this.moveBonus = Double.parseDouble(value);
                break;
            }
            case "castleId": {
                this.castleId = Integer.parseInt(value);
                break;
            }
            case "eventId": {
                this.eventId = Integer.parseInt(value);
                break;
            }
            default: {
                super.setParameter(name, value);
                break;
            }
        }
    }
    
    private Castle getCastle() {
        if (this.castleId > 0 && this.castle == null) {
            this.castle = CastleManager.getInstance().getCastleById(this.castleId);
        }
        return this.castle;
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        if (this.getCastle() != null) {
            if (!this.getCastle().getSiege().isInProgress() || !this.isEnabled()) {
                return;
            }
            final Player player = creature.getActingPlayer();
            if (player != null && player.isInSiege() && player.getSiegeState() == 2) {
                return;
            }
        }
        creature.setInsideZone(ZoneType.SWAMP, true);
        if (GameUtils.isPlayer(creature)) {
            if (this.eventId > 0) {
                creature.sendPacket(new OnEventTrigger(this.eventId, true));
            }
            creature.getActingPlayer().broadcastUserInfo();
        }
    }
    
    @Override
    protected void onExit(final Creature creature) {
        if (creature.isInsideZone(ZoneType.SWAMP)) {
            creature.setInsideZone(ZoneType.SWAMP, false);
            if (GameUtils.isPlayer(creature)) {
                if (this.eventId > 0) {
                    creature.sendPacket(new OnEventTrigger(this.eventId, false));
                }
                creature.getActingPlayer().broadcastUserInfo();
            }
        }
    }
    
    public double getMoveBonus() {
        return this.moveBonus;
    }
}
