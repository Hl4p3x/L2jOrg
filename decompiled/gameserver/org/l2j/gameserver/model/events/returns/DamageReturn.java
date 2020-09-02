// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.returns;

public class DamageReturn extends TerminateReturn
{
    private final double _damage;
    
    public DamageReturn(final boolean terminate, final boolean override, final boolean abort, final double damage) {
        super(terminate, override, abort);
        this._damage = damage;
    }
    
    public double getDamage() {
        return this._damage;
    }
}
