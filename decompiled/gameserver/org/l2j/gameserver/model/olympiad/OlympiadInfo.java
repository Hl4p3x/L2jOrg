// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

public class OlympiadInfo
{
    private final String _name;
    private final String _clan;
    private final int _clanId;
    private final int _classId;
    private final int _dmg;
    private final int _curPoints;
    private final int _diffPoints;
    
    public OlympiadInfo(final String name, final String clan, final int clanId, final int classId, final int dmg, final int curPoints, final int diffPoints) {
        this._name = name;
        this._clan = clan;
        this._clanId = clanId;
        this._classId = classId;
        this._dmg = dmg;
        this._curPoints = curPoints;
        this._diffPoints = diffPoints;
    }
    
    public String getName() {
        return this._name;
    }
    
    public String getClanName() {
        return this._clan;
    }
    
    public int getClanId() {
        return this._clanId;
    }
    
    public int getClassId() {
        return this._classId;
    }
    
    public int getDamage() {
        return this._dmg;
    }
    
    public int getCurrentPoints() {
        return this._curPoints;
    }
    
    public int getDiffPoints() {
        return this._diffPoints;
    }
}
