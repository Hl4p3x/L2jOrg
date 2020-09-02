// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.data.database.data.PlayerVariableData;

public class CharSelectInfoPackage
{
    private final int[][] _paperdoll;
    private final PlayerVariableData _vars;
    private String _name;
    private int _objectId;
    private long _exp;
    private long _sp;
    private int _clanId;
    private int _race;
    private int _classId;
    private int _baseClassId;
    private long _deleteTimer;
    private long _lastAccess;
    private int _face;
    private int _hairStyle;
    private int _hairColor;
    private int _sex;
    private int _level;
    private int _maxHp;
    private double _currentHp;
    private int _maxMp;
    private double _currentMp;
    private int _reputation;
    private int _pkKills;
    private int _pvpKills;
    private VariationInstance _augmentation;
    private int _x;
    private int _y;
    private int _z;
    private String _htmlPrefix;
    private int _vitalityPoints;
    private int _accessLevel;
    private boolean _isNoble;
    
    public CharSelectInfoPackage(final int objectId, final String name) {
        this._objectId = 0;
        this._exp = 0L;
        this._sp = 0L;
        this._clanId = 0;
        this._race = 0;
        this._classId = 0;
        this._baseClassId = 0;
        this._deleteTimer = 0L;
        this._lastAccess = 0L;
        this._face = 0;
        this._hairStyle = 0;
        this._hairColor = 0;
        this._sex = 0;
        this._level = 1;
        this._maxHp = 0;
        this._currentHp = 0.0;
        this._maxMp = 0;
        this._currentMp = 0.0;
        this._reputation = 0;
        this._pkKills = 0;
        this._pvpKills = 0;
        this._x = 0;
        this._y = 0;
        this._z = 0;
        this._htmlPrefix = null;
        this._vitalityPoints = 0;
        this._accessLevel = 0;
        this.setObjectId(objectId);
        this._name = name;
        this._paperdoll = PlayerInventory.restoreVisibleInventory(objectId);
        final PlayerVariableData vars = ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).findById(objectId);
        if (vars == null) {
            this._vars = PlayerVariableData.init(objectId);
        }
        else {
            this._vars = vars;
        }
    }
    
    public int getObjectId() {
        return this._objectId;
    }
    
    public void setObjectId(final int objectId) {
        this._objectId = objectId;
    }
    
    public int getAccessLevel() {
        return this._accessLevel;
    }
    
    public void setAccessLevel(final int level) {
        this._accessLevel = level;
    }
    
    public int getClanId() {
        return this._clanId;
    }
    
    public void setClanId(final int clanId) {
        this._clanId = clanId;
    }
    
    public int getClassId() {
        return this._classId;
    }
    
    public void setClassId(final int classId) {
        this._classId = classId;
    }
    
    public int getBaseClassId() {
        return this._baseClassId;
    }
    
    public void setBaseClassId(final int baseClassId) {
        this._baseClassId = baseClassId;
    }
    
    public double getCurrentHp() {
        return this._currentHp;
    }
    
    public void setCurrentHp(final double currentHp) {
        this._currentHp = currentHp;
    }
    
    public double getCurrentMp() {
        return this._currentMp;
    }
    
    public void setCurrentMp(final double currentMp) {
        this._currentMp = currentMp;
    }
    
    public long getDeleteTimer() {
        return this._deleteTimer;
    }
    
    public void setDeleteTimer(final long deleteTimer) {
        this._deleteTimer = deleteTimer;
    }
    
    public long getLastAccess() {
        return this._lastAccess;
    }
    
    public void setLastAccess(final long lastAccess) {
        this._lastAccess = lastAccess;
    }
    
    public long getExp() {
        return this._exp;
    }
    
    public void setExp(final long exp) {
        this._exp = exp;
    }
    
    public int getFace() {
        return this._vars.getVisualFaceId();
    }
    
    public void setFace(final int face) {
        this._face = face;
    }
    
    public int getHairColor() {
        return this._vars.getVisualHairColorId();
    }
    
    public void setHairColor(final int hairColor) {
        this._hairColor = hairColor;
    }
    
    public int getHairStyle() {
        return this._vars.getVisualHairId();
    }
    
    public void setHairStyle(final int hairStyle) {
        this._hairStyle = hairStyle;
    }
    
    public int getPaperdollObjectId(final InventorySlot slot) {
        return this._paperdoll[slot.getId()][0];
    }
    
    public int getPaperdollItemId(final int slot) {
        return this._paperdoll[slot][1];
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public void setLevel(final int level) {
        this._level = level;
    }
    
    public int getMaxHp() {
        return this._maxHp;
    }
    
    public void setMaxHp(final int maxHp) {
        this._maxHp = maxHp;
    }
    
    public int getMaxMp() {
        return this._maxMp;
    }
    
    public void setMaxMp(final int maxMp) {
        this._maxMp = maxMp;
    }
    
    public String getName() {
        return this._name;
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public int getRace() {
        return this._race;
    }
    
    public void setRace(final int race) {
        this._race = race;
    }
    
    public int getSex() {
        return this._sex;
    }
    
    public void setSex(final int sex) {
        this._sex = sex;
    }
    
    public long getSp() {
        return this._sp;
    }
    
    public void setSp(final long sp) {
        this._sp = sp;
    }
    
    public int getEnchantEffect(final int slot) {
        return this._paperdoll[slot][2];
    }
    
    public int getReputation() {
        return this._reputation;
    }
    
    public void setReputation(final int reputation) {
        this._reputation = reputation;
    }
    
    public VariationInstance getAugmentation() {
        return this._augmentation;
    }
    
    public void setAugmentation(final VariationInstance augmentation) {
        this._augmentation = augmentation;
    }
    
    public int getPkKills() {
        return this._pkKills;
    }
    
    public void setPkKills(final int PkKills) {
        this._pkKills = PkKills;
    }
    
    public int getPvPKills() {
        return this._pvpKills;
    }
    
    public void setPvPKills(final int PvPKills) {
        this._pvpKills = PvPKills;
    }
    
    public int getX() {
        return this._x;
    }
    
    public void setX(final int x) {
        this._x = x;
    }
    
    public int getY() {
        return this._y;
    }
    
    public void setY(final int y) {
        this._y = y;
    }
    
    public int getZ() {
        return this._z;
    }
    
    public void setZ(final int z) {
        this._z = z;
    }
    
    public String getHtmlPrefix() {
        return this._htmlPrefix;
    }
    
    public void setHtmlPrefix(final String s) {
        this._htmlPrefix = s;
    }
    
    public int getVitalityPoints() {
        return this._vitalityPoints;
    }
    
    public void setVitalityPoints(final int points) {
        this._vitalityPoints = points;
    }
    
    public boolean isHairAccessoryEnabled() {
        return this._vars.isHairAccessoryEnabled();
    }
    
    public int getVitalityItemsUsed() {
        return this._vars.getVitalityItemsUsed();
    }
    
    public boolean isNoble() {
        return this._isNoble;
    }
    
    public void setNoble(final boolean noble) {
        this._isNoble = noble;
    }
}
