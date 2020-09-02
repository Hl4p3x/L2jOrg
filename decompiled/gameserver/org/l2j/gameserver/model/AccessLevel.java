// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.data.xml.impl.AdminData;

public class AccessLevel
{
    AccessLevel _childsAccessLevel;
    private int _accessLevel;
    private String _name;
    private int _child;
    private int _nameColor;
    private int _titleColor;
    private boolean _isGm;
    private boolean _allowPeaceAttack;
    private boolean _allowFixedRes;
    private boolean _allowTransaction;
    private boolean _allowAltG;
    private boolean _giveDamage;
    private boolean _takeAggro;
    private boolean _gainExp;
    
    public AccessLevel(final StatsSet set) {
        this._childsAccessLevel = null;
        this._accessLevel = 0;
        this._name = null;
        this._child = 0;
        this._nameColor = 0;
        this._titleColor = 0;
        this._isGm = false;
        this._allowPeaceAttack = false;
        this._allowFixedRes = false;
        this._allowTransaction = false;
        this._allowAltG = false;
        this._giveDamage = false;
        this._takeAggro = false;
        this._gainExp = false;
        this._accessLevel = set.getInt("level");
        this._name = set.getString("name");
        this._nameColor = Integer.decode(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, set.getString("nameColor", "FFFFFF")));
        this._titleColor = Integer.decode(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, set.getString("titleColor", "FFFFFF")));
        this._child = set.getInt("childAccess", 0);
        this._isGm = set.getBoolean("isGM", false);
        this._allowPeaceAttack = set.getBoolean("allowPeaceAttack", false);
        this._allowFixedRes = set.getBoolean("allowFixedRes", false);
        this._allowTransaction = set.getBoolean("allowTransaction", true);
        this._allowAltG = set.getBoolean("allowAltg", false);
        this._giveDamage = set.getBoolean("giveDamage", true);
        this._takeAggro = set.getBoolean("takeAggro", true);
        this._gainExp = set.getBoolean("gainExp", true);
    }
    
    public AccessLevel() {
        this._childsAccessLevel = null;
        this._accessLevel = 0;
        this._name = null;
        this._child = 0;
        this._nameColor = 0;
        this._titleColor = 0;
        this._isGm = false;
        this._allowPeaceAttack = false;
        this._allowFixedRes = false;
        this._allowTransaction = false;
        this._allowAltG = false;
        this._giveDamage = false;
        this._takeAggro = false;
        this._gainExp = false;
        this._accessLevel = 0;
        this._name = "User";
        this._nameColor = Integer.decode("0xFFFFFF");
        this._titleColor = Integer.decode("0xFFFFFF");
        this._child = 0;
        this._isGm = false;
        this._allowPeaceAttack = false;
        this._allowFixedRes = false;
        this._allowTransaction = true;
        this._allowAltG = false;
        this._giveDamage = true;
        this._takeAggro = true;
        this._gainExp = true;
    }
    
    public int getLevel() {
        return this._accessLevel;
    }
    
    public String getName() {
        return this._name;
    }
    
    public int getNameColor() {
        return this._nameColor;
    }
    
    public int getTitleColor() {
        return this._titleColor;
    }
    
    public boolean isGm() {
        return this._isGm;
    }
    
    public boolean allowPeaceAttack() {
        return this._allowPeaceAttack;
    }
    
    public boolean allowFixedRes() {
        return this._allowFixedRes;
    }
    
    public boolean allowTransaction() {
        return this._allowTransaction;
    }
    
    public boolean allowAltG() {
        return this._allowAltG;
    }
    
    public boolean canGiveDamage() {
        return this._giveDamage;
    }
    
    public boolean canTakeAggro() {
        return this._takeAggro;
    }
    
    public boolean canGainExp() {
        return this._gainExp;
    }
    
    public boolean hasChildAccess(final AccessLevel accessLevel) {
        if (this._childsAccessLevel == null) {
            if (this._child <= 0) {
                return false;
            }
            this._childsAccessLevel = AdminData.getInstance().getAccessLevel(this._child);
        }
        return this._childsAccessLevel != null && (this._childsAccessLevel.getLevel() == accessLevel.getLevel() || this._childsAccessLevel.hasChildAccess(accessLevel));
    }
}
