// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events.returns;

public class TerminateReturn extends AbstractEventReturn
{
    private final boolean _terminate;
    
    public TerminateReturn(final boolean terminate, final boolean override, final boolean abort) {
        super(override, abort);
        this._terminate = terminate;
    }
    
    public boolean terminate() {
        return this._terminate;
    }
}
