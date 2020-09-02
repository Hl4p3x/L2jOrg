// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

public class TeleportBookmark extends Location
{
    private final int _id;
    private int _icon;
    private String _name;
    private String _tag;
    
    public TeleportBookmark(final int id, final int x, final int y, final int z, final int icon, final String tag, final String name) {
        super(x, y, z);
        this._id = id;
        this._icon = icon;
        this._name = name;
        this._tag = tag;
    }
    
    public String getName() {
        return this._name;
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public int getId() {
        return this._id;
    }
    
    public int getIcon() {
        return this._icon;
    }
    
    public void setIcon(final int icon) {
        this._icon = icon;
    }
    
    public String getTag() {
        return this._tag;
    }
    
    public void setTag(final String tag) {
        this._tag = tag;
    }
}
