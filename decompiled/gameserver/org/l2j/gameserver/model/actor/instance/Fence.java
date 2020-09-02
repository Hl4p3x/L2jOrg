// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.data.xml.FenceDataManager;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.DeleteObject;
import org.l2j.gameserver.network.serverpackets.ExColosseumFenceInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.enums.FenceState;
import org.l2j.gameserver.model.WorldObject;

public final class Fence extends WorldObject
{
    private final int _xMin;
    private final int _xMax;
    private final int _yMin;
    private final int _yMax;
    private final String _name;
    private final int _width;
    private final int _length;
    private FenceState _state;
    private int[] _heightFences;
    
    public Fence(final int x, final int y, final String name, final int width, final int length, final int height, final FenceState state) {
        super(IdFactory.getInstance().getNextId());
        this._xMin = x - width / 2;
        this._xMax = x + width / 2;
        this._yMin = y - length / 2;
        this._yMax = y + length / 2;
        this._name = name;
        this._width = width;
        this._length = length;
        this._state = state;
        if (height > 1) {
            this._heightFences = new int[height - 1];
            for (int i = 0; i < this._heightFences.length; ++i) {
                this._heightFences[i] = IdFactory.getInstance().getNextId();
            }
        }
    }
    
    @Override
    public int getId() {
        return this.getObjectId();
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return false;
    }
    
    @Override
    public void sendInfo(final Player activeChar) {
        activeChar.sendPacket(new ExColosseumFenceInfo(this));
        if (this._heightFences != null) {
            for (final int objId : this._heightFences) {
                activeChar.sendPacket(new ExColosseumFenceInfo(objId, this.getX(), this.getY(), this.getZ(), this._width, this._length, this._state));
            }
        }
    }
    
    @Override
    public boolean decayMe() {
        if (this._heightFences != null) {
            final DeleteObject[] deleteObjects = new DeleteObject[this._heightFences.length];
            for (int i = 0; i < this._heightFences.length; ++i) {
                deleteObjects[i] = new DeleteObject(this._heightFences[i]);
            }
            World.getInstance().forEachVisibleObject(this, Player.class, player -> player.sendPacket((ServerPacket[])deleteObjects));
        }
        return super.decayMe();
    }
    
    public boolean deleteMe() {
        this.decayMe();
        FenceDataManager.getInstance().removeFence(this);
        return false;
    }
    
    public FenceState getState() {
        return this._state;
    }
    
    public void setState(final FenceState type) {
        this._state = type;
        this.broadcastInfo();
    }
    
    public int getWidth() {
        return this._width;
    }
    
    public int getLength() {
        return this._length;
    }
    
    public int getXMin() {
        return this._xMin;
    }
    
    public int getYMin() {
        return this._yMin;
    }
    
    public int getXMax() {
        return this._xMax;
    }
    
    public int getYMax() {
        return this._yMax;
    }
}
