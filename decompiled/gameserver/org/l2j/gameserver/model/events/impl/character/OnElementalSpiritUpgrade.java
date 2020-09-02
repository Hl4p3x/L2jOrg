// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnElementalSpiritUpgrade implements IBaseEvent
{
    private final ElementalSpirit spirit;
    private final Player player;
    
    public OnElementalSpiritUpgrade(final Player player, final ElementalSpirit spirit) {
        this.player = player;
        this.spirit = spirit;
    }
    
    public ElementalSpirit getSpirit() {
        return this.spirit;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    @Override
    public EventType getType() {
        return EventType.ON_ELEMENTAL_SPIRIT_UPGRADE;
    }
}
