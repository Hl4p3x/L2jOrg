// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.returns;

public abstract class AbstractEventReturn
{
    private final boolean _override;
    private final boolean _abort;
    
    public AbstractEventReturn(final boolean override, final boolean abort) {
        this._override = override;
        this._abort = abort;
    }
    
    public boolean override() {
        return this._override;
    }
    
    public boolean abort() {
        return this._abort;
    }
}
