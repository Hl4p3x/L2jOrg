// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public class TeleportLocation
{
    private int _teleId;
    private int _locX;
    private int _locY;
    private int _locZ;
    private int _price;
    private boolean _forNoble;
    private int _itemId;
    
    public int getTeleId() {
        return this._teleId;
    }
    
    public void setTeleId(final int id) {
        this._teleId = id;
    }
    
    public int getLocX() {
        return this._locX;
    }
    
    public void setLocX(final int locX) {
        this._locX = locX;
    }
    
    public int getLocY() {
        return this._locY;
    }
    
    public void setLocY(final int locY) {
        this._locY = locY;
    }
    
    public int getLocZ() {
        return this._locZ;
    }
    
    public void setLocZ(final int locZ) {
        this._locZ = locZ;
    }
    
    public int getPrice() {
        return this._price;
    }
    
    public void setPrice(final int price) {
        this._price = price;
    }
    
    public boolean getIsForNoble() {
        return this._forNoble;
    }
    
    public void setIsForNoble(final boolean val) {
        this._forNoble = val;
    }
    
    public int getItemId() {
        return this._itemId;
    }
    
    public void setItemId(final int val) {
        this._itemId = val;
    }
}
