// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.actor.Summon;

public class BlessedBeastSpiritShot extends BeastSpiritShot
{
    @Override
    protected boolean isBlessed() {
        return true;
    }
    
    @Override
    protected double getBonus(final Summon summon) {
        return super.getBonus(summon) * 2.0;
    }
}
