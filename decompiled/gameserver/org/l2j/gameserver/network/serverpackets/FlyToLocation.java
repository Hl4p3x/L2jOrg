// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.Creature;

public final class FlyToLocation extends ServerPacket
{
    private final int _destX;
    private final int _destY;
    private final int _destZ;
    private final int _chaObjId;
    private final int _chaX;
    private final int _chaY;
    private final int _chaZ;
    private final FlyType _type;
    private int _flySpeed;
    private int _flyDelay;
    private int _animationSpeed;
    
    public FlyToLocation(final Creature cha, final int destX, final int destY, final int destZ, final FlyType type) {
        this._chaObjId = cha.getObjectId();
        this._chaX = cha.getX();
        this._chaY = cha.getY();
        this._chaZ = cha.getZ();
        this._destX = destX;
        this._destY = destY;
        this._destZ = destZ;
        this._type = type;
    }
    
    public FlyToLocation(final Creature cha, final int destX, final int destY, final int destZ, final FlyType type, final int flySpeed, final int flyDelay, final int animationSpeed) {
        this._chaObjId = cha.getObjectId();
        this._chaX = cha.getX();
        this._chaY = cha.getY();
        this._chaZ = cha.getZ();
        this._destX = destX;
        this._destY = destY;
        this._destZ = destZ;
        this._type = type;
        this._flySpeed = flySpeed;
        this._flyDelay = flyDelay;
        this._animationSpeed = animationSpeed;
    }
    
    public FlyToLocation(final Creature cha, final ILocational dest, final FlyType type) {
        this(cha, dest.getX(), dest.getY(), dest.getZ(), type);
    }
    
    public FlyToLocation(final Creature cha, final ILocational dest, final FlyType type, final int flySpeed, final int flyDelay, final int animationSpeed) {
        this(cha, dest.getX(), dest.getY(), dest.getZ(), type, flySpeed, flyDelay, animationSpeed);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.FLY_TO_LOCATION);
        this.writeInt(this._chaObjId);
        this.writeInt(this._destX);
        this.writeInt(this._destY);
        this.writeInt(this._destZ);
        this.writeInt(this._chaX);
        this.writeInt(this._chaY);
        this.writeInt(this._chaZ);
        this.writeInt(this._type.ordinal());
        this.writeInt(this._flySpeed);
        this.writeInt(this._flyDelay);
        this.writeInt(this._animationSpeed);
    }
    
    public enum FlyType
    {
        THROW_UP, 
        THROW_HORIZONTAL, 
        DUMMY, 
        CHARGE, 
        PUSH_HORIZONTAL, 
        JUMP_EFFECTED, 
        NOT_USED, 
        PUSH_DOWN_HORIZONTAL, 
        WARP_BACK, 
        WARP_FORWARD;
    }
}
