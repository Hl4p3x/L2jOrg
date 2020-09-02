// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.base;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.LevelData;

public final class SubClass
{
    private static final byte _maxLevel;
    private static final int MAX_VITALITY_POINTS = 140000;
    private static final int MIN_VITALITY_POINTS = 0;
    private ClassId _class;
    private long _exp;
    private long _sp;
    private byte _level;
    private int _classIndex;
    private int _vitalityPoints;
    private boolean _dualClass;
    
    public SubClass() {
        this._exp = LevelData.getInstance().getExpForLevel(Config.BASE_SUBCLASS_LEVEL);
        this._sp = 0L;
        this._level = Config.BASE_SUBCLASS_LEVEL;
        this._classIndex = 1;
        this._vitalityPoints = 0;
        this._dualClass = false;
    }
    
    public ClassId getClassDefinition() {
        return this._class;
    }
    
    public int getClassId() {
        return this._class.getId();
    }
    
    public void setClassId(final int classId) {
        this._class = ClassId.getClassId(classId);
    }
    
    public long getExp() {
        return this._exp;
    }
    
    public void setExp(long expValue) {
        if (!this._dualClass && expValue > LevelData.getInstance().getMaxExp() - 1L) {
            expValue = LevelData.getInstance().getExpForLevel(SubClass._maxLevel + 1) - 1L;
        }
        this._exp = expValue;
    }
    
    public long getSp() {
        return this._sp;
    }
    
    public void setSp(final long spValue) {
        this._sp = spValue;
    }
    
    public byte getLevel() {
        return this._level;
    }
    
    public void setLevel(byte levelValue) {
        if (!this._dualClass && levelValue > SubClass._maxLevel) {
            levelValue = SubClass._maxLevel;
        }
        else if (levelValue < Config.BASE_SUBCLASS_LEVEL) {
            levelValue = Config.BASE_SUBCLASS_LEVEL;
        }
        this._level = levelValue;
    }
    
    public int getVitalityPoints() {
        return Math.min(Math.max(this._vitalityPoints, 0), 140000);
    }
    
    public void setVitalityPoints(final int value) {
        this._vitalityPoints = Math.min(Math.max(value, 0), 140000);
    }
    
    public int getClassIndex() {
        return this._classIndex;
    }
    
    public void setClassIndex(final int classIndex) {
        this._classIndex = classIndex;
    }
    
    public boolean isDualClass() {
        return this._dualClass;
    }
    
    public void setIsDualClass(final boolean dualClass) {
        this._dualClass = dualClass;
    }
    
    static {
        _maxLevel = ((Config.MAX_SUBCLASS_LEVEL < LevelData.getInstance().getMaxLevel()) ? Config.MAX_SUBCLASS_LEVEL : ((byte)(LevelData.getInstance().getMaxLevel() - 1)));
    }
}
