// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.variables;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.l2j.gameserver.model.interfaces.IDeletable;
import org.l2j.gameserver.model.interfaces.IStorable;
import org.l2j.gameserver.model.interfaces.IRestorable;
import org.l2j.gameserver.model.StatsSet;

public abstract class AbstractVariables extends StatsSet implements IRestorable, IStorable, IDeletable
{
    private final AtomicBoolean _hasChanges;
    
    public AbstractVariables() {
        super(new ConcurrentHashMap<String, Object>());
        this._hasChanges = new AtomicBoolean(false);
    }
    
    @Override
    public final StatsSet set(final String name, final boolean value) {
        this._hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }
    
    @Override
    public final StatsSet set(final String name, final double value) {
        this._hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }
    
    @Override
    public final StatsSet set(final String name, final Enum<?> value) {
        this._hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }
    
    @Override
    public final StatsSet set(final String name, final int value) {
        this._hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }
    
    @Override
    public final StatsSet set(final String name, final long value) {
        this._hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }
    
    @Override
    public final StatsSet set(final String name, final String value) {
        this._hasChanges.compareAndSet(false, true);
        return super.set(name, value);
    }
    
    public final StatsSet set(final String name, final String value, final boolean markAsChanged) {
        if (markAsChanged) {
            this._hasChanges.compareAndSet(false, true);
        }
        return super.set(name, value);
    }
    
    public boolean hasVariable(final String name) {
        return this.getSet().keySet().contains(name);
    }
    
    public final boolean hasChanges() {
        return this._hasChanges.get();
    }
    
    public final boolean compareAndSetChanges(final boolean expect, final boolean update) {
        return this._hasChanges.compareAndSet(expect, update);
    }
    
    @Override
    public final void remove(final String name) {
        this._hasChanges.compareAndSet(false, true);
        this.getSet().remove(name);
    }
}
