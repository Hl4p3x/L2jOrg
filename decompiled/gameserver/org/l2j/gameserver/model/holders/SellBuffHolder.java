// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

public final class SellBuffHolder
{
    private final int _skillId;
    private long _price;
    
    public SellBuffHolder(final int skillId, final long price) {
        this._skillId = skillId;
        this._price = price;
    }
    
    public final int getSkillId() {
        return this._skillId;
    }
    
    public final long getPrice() {
        return this._price;
    }
    
    public final void setPrice(final int price) {
        this._price = price;
    }
}
