// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.templates;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.DoorOpenType;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public class DoorTemplate extends CreatureTemplate implements IIdentifiable
{
    private final int _doorId;
    private final int[] _nodeX;
    private final int[] _nodeY;
    private final int _nodeZ;
    private final int _height;
    private final int _posX;
    private final int _posY;
    private final int _posZ;
    private final int _emmiter;
    private final int _childDoorId;
    private final String _name;
    private final String _groupName;
    private final boolean _showHp;
    private final boolean _isWall;
    private final byte _masterDoorClose;
    private final byte _masterDoorOpen;
    private final boolean _isTargetable;
    private final boolean _default_status;
    private final int _closeTime;
    private final int _level;
    private final DoorOpenType _openType;
    private final boolean _checkCollision;
    private final boolean _isAttackableDoor;
    private final boolean _stealth;
    private final boolean _isInverted;
    private int _openTime;
    private int _randomTime;
    
    public DoorTemplate(final StatsSet set) {
        super(set);
        this._doorId = set.getInt("id");
        this._name = set.getString("name");
        this._height = set.getInt("height", 150);
        this._nodeZ = set.getInt("nodeZ");
        this._nodeX = new int[4];
        this._nodeY = new int[4];
        for (int i = 0; i < 4; ++i) {
            this._nodeX[i] = set.getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
            this._nodeY[i] = set.getInt(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, i));
        }
        this._posX = set.getInt("x");
        this._posY = set.getInt("y");
        this._posZ = Math.min(set.getInt("z"), this._nodeZ);
        this._emmiter = set.getInt("emmiterId", 0);
        this._showHp = set.getBoolean("showHp", true);
        this._isWall = set.getBoolean("isWall", false);
        this._groupName = set.getString("group", null);
        this._childDoorId = set.getInt("childId", -1);
        String masterevent = set.getString("masterClose", "act_nothing");
        this._masterDoorClose = (byte)(masterevent.equals("act_open") ? 1 : (masterevent.equals("act_close") ? -1 : 0));
        masterevent = set.getString("masterOpen", "act_nothing");
        this._masterDoorOpen = (byte)(masterevent.equals("act_open") ? 1 : (masterevent.equals("act_close") ? -1 : 0));
        this._isTargetable = set.getBoolean("targetable", true);
        this._default_status = set.getString("default", "close").equals("open");
        this._closeTime = set.getInt("closeTime", -1);
        this._level = set.getInt("level", 0);
        this._openType = set.getEnum("openMethod", DoorOpenType.class, DoorOpenType.NONE);
        this._checkCollision = set.getBoolean("isCheckCollision", true);
        if (this._openType == DoorOpenType.BY_TIME) {
            this._openTime = set.getInt("openTime");
            this._randomTime = set.getInt("randomTime", -1);
        }
        this._isAttackableDoor = set.getBoolean("attackable", false);
        this._stealth = set.getBoolean("stealth", false);
        this._isInverted = set.getBoolean("isInverted", false);
    }
    
    @Override
    public int getId() {
        return this._doorId;
    }
    
    public String getName() {
        return this._name;
    }
    
    public int[] getNodeX() {
        return this._nodeX;
    }
    
    public int[] getNodeY() {
        return this._nodeY;
    }
    
    public int getNodeZ() {
        return this._nodeZ;
    }
    
    public int getHeight() {
        return this._height;
    }
    
    public int getX() {
        return this._posX;
    }
    
    public int getY() {
        return this._posY;
    }
    
    public int getZ() {
        return this._posZ;
    }
    
    public int getEmmiter() {
        return this._emmiter;
    }
    
    public int getChildDoorId() {
        return this._childDoorId;
    }
    
    public String getGroupName() {
        return this._groupName;
    }
    
    public boolean isShowHp() {
        return this._showHp;
    }
    
    public boolean isWall() {
        return this._isWall;
    }
    
    public byte getMasterDoorOpen() {
        return this._masterDoorOpen;
    }
    
    public byte getMasterDoorClose() {
        return this._masterDoorClose;
    }
    
    public boolean isTargetable() {
        return this._isTargetable;
    }
    
    public boolean isOpenByDefault() {
        return this._default_status;
    }
    
    public int getOpenTime() {
        return this._openTime;
    }
    
    public int getRandomTime() {
        return this._randomTime;
    }
    
    public int getCloseTime() {
        return this._closeTime;
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public DoorOpenType getOpenType() {
        return this._openType;
    }
    
    public boolean isCheckCollision() {
        return this._checkCollision;
    }
    
    public boolean isAttackable() {
        return this._isAttackableDoor;
    }
    
    public boolean isStealth() {
        return this._stealth;
    }
    
    public boolean isInverted() {
        return this._isInverted;
    }
}
