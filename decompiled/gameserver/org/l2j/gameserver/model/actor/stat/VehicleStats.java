// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Vehicle;

public class VehicleStats extends CreatureStats
{
    private float _moveSpeed;
    private int _rotationSpeed;
    
    public VehicleStats(final Vehicle activeChar) {
        super(activeChar);
        this._moveSpeed = 0.0f;
        this._rotationSpeed = 0;
    }
    
    @Override
    public double getMoveSpeed() {
        return this._moveSpeed;
    }
    
    public final void setMoveSpeed(final float speed) {
        this._moveSpeed = speed;
    }
    
    public final double getRotationSpeed() {
        return this._rotationSpeed;
    }
    
    public final void setRotationSpeed(final int speed) {
        this._rotationSpeed = speed;
    }
}
