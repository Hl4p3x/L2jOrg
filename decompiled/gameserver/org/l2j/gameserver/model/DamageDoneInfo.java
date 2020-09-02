// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.actor.instance.Player;

public final class DamageDoneInfo
{
    private final Player _attacker;
    private long _damage;
    
    public DamageDoneInfo(final Player attacker) {
        this._damage = 0L;
        this._attacker = attacker;
    }
    
    public Player getAttacker() {
        return this._attacker;
    }
    
    public void addDamage(final long damage) {
        this._damage += damage;
    }
    
    public long getDamage() {
        return this._damage;
    }
    
    @Override
    public final boolean equals(final Object obj) {
        return this == obj || (obj instanceof DamageDoneInfo && ((DamageDoneInfo)obj).getAttacker() == this._attacker);
    }
    
    @Override
    public final int hashCode() {
        return this._attacker.getObjectId();
    }
}
