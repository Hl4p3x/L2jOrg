// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.MacroType;

public class MacroCmd
{
    private final int _entry;
    private final MacroType _type;
    private final int _d1;
    private final int _d2;
    private final String _cmd;
    
    public MacroCmd(final int entry, final MacroType type, final int d1, final int d2, final String cmd) {
        this._entry = entry;
        this._type = type;
        this._d1 = d1;
        this._d2 = d2;
        this._cmd = cmd;
    }
    
    public int getEntry() {
        return this._entry;
    }
    
    public MacroType getType() {
        return this._type;
    }
    
    public int getD1() {
        return this._d1;
    }
    
    public int getD2() {
        return this._d2;
    }
    
    public String getCmd() {
        return this._cmd;
    }
}
