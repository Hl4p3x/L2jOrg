// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.world.zone.Zone;

public class TaxZone extends Zone
{
    private int domainId;
    private Castle castle;
    
    public TaxZone(final int id) {
        super(id);
    }
    
    @Override
    public void setParameter(final String name, final String value) {
        if (name.equalsIgnoreCase("domainId")) {
            this.domainId = Integer.parseInt(value);
        }
        else {
            super.setParameter(name, value);
        }
    }
    
    @Override
    protected void onEnter(final Creature creature) {
        creature.setInsideZone(ZoneType.TAX, true);
        if (GameUtils.isNpc(creature)) {
            ((Npc)creature).setTaxZone(this);
        }
    }
    
    @Override
    protected void onExit(final Creature creature) {
        creature.setInsideZone(ZoneType.TAX, false);
        if (GameUtils.isNpc(creature)) {
            ((Npc)creature).setTaxZone(null);
        }
    }
    
    public Castle getCastle() {
        if (this.castle == null) {
            this.castle = CastleManager.getInstance().getCastleById(this.domainId);
        }
        return this.castle;
    }
}
