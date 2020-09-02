// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.actor.Creature;

public class WarpedSpaceHolder
{
    private final Creature _creature;
    private final int _range;
    
    public WarpedSpaceHolder(final Creature creature, final int range) {
        this._creature = creature;
        this._range = range;
    }
    
    public Creature getCreature() {
        return this._creature;
    }
    
    public int getRange() {
        return this._range;
    }
}
