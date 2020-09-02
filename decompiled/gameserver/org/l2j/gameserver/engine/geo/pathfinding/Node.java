// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo.pathfinding;

import org.l2j.gameserver.engine.geo.geodata.GeoLocation;

public class Node
{
    private GeoLocation _loc;
    private Node _parent;
    private Node _child;
    private double _cost;
    
    public Node() {
        this._cost = -1000.0;
    }
    
    public void setLoc(final int x, final int y, final int z) {
        this._loc = new GeoLocation(x, y, z);
    }
    
    public GeoLocation getLoc() {
        return this._loc;
    }
    
    public Node getParent() {
        return this._parent;
    }
    
    public void setParent(final Node parent) {
        this._parent = parent;
    }
    
    public Node getChild() {
        return this._child;
    }
    
    public void setChild(final Node child) {
        this._child = child;
    }
    
    public double getCost() {
        return this._cost;
    }
    
    public void setCost(final double cost) {
        this._cost = cost;
    }
    
    public void free() {
        this._loc = null;
        this._parent = null;
        this._child = null;
        this._cost = -1000.0;
    }
}
