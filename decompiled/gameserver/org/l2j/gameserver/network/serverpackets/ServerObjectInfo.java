// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;

public final class ServerObjectInfo extends ServerPacket
{
    private final Npc _activeChar;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;
    private final int _idTemplate;
    private final boolean _isAttackable;
    private final double _collisionHeight;
    private final double _collisionRadius;
    private final String _name;
    
    public ServerObjectInfo(final Npc activeChar, final Creature actor) {
        this._activeChar = activeChar;
        this._idTemplate = this._activeChar.getTemplate().getDisplayId();
        this._isAttackable = this._activeChar.isAutoAttackable(actor);
        this._collisionHeight = this._activeChar.getCollisionHeight();
        this._collisionRadius = this._activeChar.getCollisionRadius();
        this._x = this._activeChar.getX();
        this._y = this._activeChar.getY();
        this._z = this._activeChar.getZ();
        this._heading = this._activeChar.getHeading();
        this._name = (this._activeChar.getTemplate().isUsingServerSideName() ? this._activeChar.getTemplate().getName() : "");
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SERVER_OBJECT_INFO);
        this.writeInt(this._activeChar.getObjectId());
        this.writeInt(this._idTemplate + 1000000);
        this.writeString((CharSequence)this._name);
        this.writeInt((int)(this._isAttackable ? 1 : 0));
        this.writeInt(this._x);
        this.writeInt(this._y);
        this.writeInt(this._z);
        this.writeInt(this._heading);
        this.writeDouble(1.0);
        this.writeDouble(1.0);
        this.writeDouble(this._collisionRadius);
        this.writeDouble(this._collisionHeight);
        this.writeInt((int)(this._isAttackable ? this._activeChar.getCurrentHp() : 0.0));
        this.writeInt(this._isAttackable ? this._activeChar.getMaxHp() : 0);
        this.writeInt(1);
        this.writeInt(0);
    }
}
