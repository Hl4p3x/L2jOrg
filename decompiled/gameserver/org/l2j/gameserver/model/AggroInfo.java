// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.actor.Creature;

public final class AggroInfo
{
    private final Creature _attacker;
    private int _hate;
    private int _damage;
    
    public AggroInfo(final Creature pAttacker) {
        this._hate = 0;
        this._damage = 0;
        this._attacker = pAttacker;
    }
    
    public Creature getAttacker() {
        return this._attacker;
    }
    
    public int getHate() {
        return this._hate;
    }
    
    public int checkHate(final Creature owner) {
        if (this._attacker.isAlikeDead() || !this._attacker.isSpawned() || !owner.isInSurroundingRegion(this._attacker)) {
            this._hate = 0;
        }
        return this._hate;
    }
    
    public void addHate(final int value) {
        this._hate = (int)Math.min(this._hate + (long)value, 999999999L);
    }
    
    public void stopHate() {
        this._hate = 0;
    }
    
    public int getDamage() {
        return this._damage;
    }
    
    public void addDamage(final int value) {
        this._damage = (int)Math.min(this._damage + (long)value, 999999999L);
    }
    
    @Override
    public final boolean equals(final Object obj) {
        return this == obj || (obj instanceof AggroInfo && ((AggroInfo)obj).getAttacker() == this._attacker);
    }
    
    @Override
    public final int hashCode() {
        return this._attacker.getObjectId();
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/Creature;II)Ljava/lang/String;, this._attacker, this._hate, this._damage);
    }
}
