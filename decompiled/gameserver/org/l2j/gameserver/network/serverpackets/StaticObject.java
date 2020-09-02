// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;

public class StaticObject extends ServerPacket
{
    private final int _staticObjectId;
    private final int _objectId;
    private final int _type;
    private final boolean _isTargetable;
    private final int _meshIndex;
    private final boolean _isClosed;
    private final boolean _isEnemy;
    private final int _maxHp;
    private final int _currentHp;
    private final boolean _showHp;
    private final int _damageGrade;
    
    public StaticObject(final StaticWorldObject staticObject) {
        this._staticObjectId = staticObject.getId();
        this._objectId = staticObject.getObjectId();
        this._type = 0;
        this._isTargetable = true;
        this._meshIndex = staticObject.getMeshIndex();
        this._isClosed = false;
        this._isEnemy = false;
        this._maxHp = 0;
        this._currentHp = 0;
        this._showHp = false;
        this._damageGrade = 0;
    }
    
    public StaticObject(final Door door, final boolean targetable) {
        this._staticObjectId = door.getId();
        this._objectId = door.getObjectId();
        this._type = 1;
        this._isTargetable = (door.isTargetable() || targetable);
        this._meshIndex = door.getMeshIndex();
        this._isClosed = !door.isOpen();
        this._isEnemy = door.isEnemy();
        this._maxHp = door.getMaxHp();
        this._currentHp = (int)door.getCurrentHp();
        this._showHp = door.getIsShowHp();
        this._damageGrade = door.getDamage();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.STATIC_OBJECT_INFO);
        this.writeInt(this._staticObjectId);
        this.writeInt(this._objectId);
        this.writeInt(this._type);
        this.writeInt((int)(this._isTargetable ? 1 : 0));
        this.writeInt(this._meshIndex);
        this.writeInt((int)(this._isClosed ? 1 : 0));
        this.writeInt((int)(this._isEnemy ? 1 : 0));
        this.writeInt(this._currentHp);
        this.writeInt(this._maxHp);
        this.writeInt((int)(this._showHp ? 1 : 0));
        this.writeInt(this._damageGrade);
    }
}
