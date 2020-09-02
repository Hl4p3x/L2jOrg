// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

public final class BuffSkillHolder
{
    private final int _id;
    private final int _price;
    private final String _type;
    private final String _description;
    
    public BuffSkillHolder(final int id, final int price, final String type, final String description) {
        this._id = id;
        this._price = price;
        this._type = type;
        this._description = description;
    }
    
    public int getId() {
        return this._id;
    }
    
    public int getPrice() {
        return this._price;
    }
    
    public final String getType() {
        return this._type;
    }
    
    public final String getDescription() {
        return this._description;
    }
}
