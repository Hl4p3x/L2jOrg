// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import java.util.List;

public class WalkRoute
{
    private final String _name;
    private final List<NpcWalkerNode> _nodeList;
    private final boolean _repeatWalk;
    private final byte _repeatType;
    private boolean _stopAfterCycle;
    
    public WalkRoute(final String name, final List<NpcWalkerNode> route, final boolean repeat, final boolean once, final byte repeatType) {
        this._name = name;
        this._nodeList = route;
        this._repeatType = repeatType;
        this._repeatWalk = (this._repeatType >= 0 && this._repeatType <= 2 && repeat);
    }
    
    public String getName() {
        return this._name;
    }
    
    public List<NpcWalkerNode> getNodeList() {
        return this._nodeList;
    }
    
    public NpcWalkerNode getLastNode() {
        return this._nodeList.get(this._nodeList.size() - 1);
    }
    
    public boolean repeatWalk() {
        return this._repeatWalk;
    }
    
    public boolean doOnce() {
        return this._stopAfterCycle;
    }
    
    public byte getRepeatType() {
        return this._repeatType;
    }
    
    public int getNodesCount() {
        return this._nodeList.size();
    }
}
