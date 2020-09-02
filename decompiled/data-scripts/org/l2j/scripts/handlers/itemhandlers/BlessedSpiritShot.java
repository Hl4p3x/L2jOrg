// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.actor.instance.Player;

public class BlessedSpiritShot extends SpiritShot
{
    @Override
    protected double getBonus(final Player player) {
        return super.getBonus(player) * 2.0;
    }
    
    @Override
    protected boolean isBlessed() {
        return true;
    }
}
