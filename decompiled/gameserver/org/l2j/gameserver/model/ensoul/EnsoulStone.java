// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.ensoul;

import java.util.ArrayList;
import java.util.List;

public class EnsoulStone
{
    private final int _id;
    private final int _slotType;
    private final List<Integer> _options;
    
    public EnsoulStone(final int id, final int slotType) {
        this._options = new ArrayList<Integer>();
        this._id = id;
        this._slotType = slotType;
    }
    
    public int getId() {
        return this._id;
    }
    
    public int getSlotType() {
        return this._slotType;
    }
    
    public List<Integer> getOptions() {
        return this._options;
    }
    
    public void addOption(final int option) {
        this._options.add(option);
    }
}
