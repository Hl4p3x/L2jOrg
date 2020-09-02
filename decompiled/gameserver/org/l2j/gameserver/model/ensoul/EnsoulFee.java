// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.ensoul;

import java.util.ArrayList;
import java.util.List;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.item.type.CrystalType;

public class EnsoulFee
{
    private final CrystalType _type;
    private final ItemHolder[] _ensoulFee;
    private final ItemHolder[] _resoulFees;
    private final List<ItemHolder> _removalFee;
    
    public EnsoulFee(final CrystalType type) {
        this._ensoulFee = new ItemHolder[3];
        this._resoulFees = new ItemHolder[3];
        this._removalFee = new ArrayList<ItemHolder>();
        this._type = type;
    }
    
    public CrystalType getCrystalType() {
        return this._type;
    }
    
    public void setEnsoul(final int index, final ItemHolder item) {
        this._ensoulFee[index] = item;
    }
    
    public void setResoul(final int index, final ItemHolder item) {
        this._resoulFees[index] = item;
    }
    
    public void addRemovalFee(final ItemHolder itemHolder) {
        this._removalFee.add(itemHolder);
    }
    
    public ItemHolder getEnsoul(final int index) {
        return this._ensoulFee[index];
    }
    
    public ItemHolder getResoul(final int index) {
        return this._resoulFees[index];
    }
    
    public List<ItemHolder> getRemovalFee() {
        return this._removalFee;
    }
}
