// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;

public class SpecialCamera extends ServerPacket
{
    private final int _id;
    private final int _force;
    private final int _angle1;
    private final int _angle2;
    private final int _time;
    private final int _duration;
    private final int _relYaw;
    private final int _relPitch;
    private final int _isWide;
    private final int _relAngle;
    private final int _unk;
    
    public SpecialCamera(final Creature creature, final int force, final int angle1, final int angle2, final int time, final int range, final int duration, final int relYaw, final int relPitch, final int isWide, final int relAngle) {
        this(creature, force, angle1, angle2, time, duration, range, relYaw, relPitch, isWide, relAngle, 0);
    }
    
    public SpecialCamera(final Creature creature, final Creature talker, final int force, final int angle1, final int angle2, final int time, final int duration, final int relYaw, final int relPitch, final int isWide, final int relAngle) {
        this(creature, force, angle1, angle2, time, duration, 0, relYaw, relPitch, isWide, relAngle, 0);
    }
    
    public SpecialCamera(final Creature creature, final int force, final int angle1, final int angle2, final int time, final int range, final int duration, final int relYaw, final int relPitch, final int isWide, final int relAngle, final int unk) {
        this._id = creature.getObjectId();
        this._force = force;
        this._angle1 = angle1;
        this._angle2 = angle2;
        this._time = time;
        this._duration = duration;
        this._relYaw = relYaw;
        this._relPitch = relPitch;
        this._isWide = isWide;
        this._relAngle = relAngle;
        this._unk = unk;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SPECIAL_CAMERA);
        this.writeInt(this._id);
        this.writeInt(this._force);
        this.writeInt(this._angle1);
        this.writeInt(this._angle2);
        this.writeInt(this._time);
        this.writeInt(this._duration);
        this.writeInt(this._relYaw);
        this.writeInt(this._relPitch);
        this.writeInt(this._isWide);
        this.writeInt(this._relAngle);
        this.writeInt(this._unk);
    }
}
