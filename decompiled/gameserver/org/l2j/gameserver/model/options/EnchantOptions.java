// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.options;

public class EnchantOptions
{
    private final int _level;
    private final int[] _options;
    
    public EnchantOptions(final int level) {
        this._level = level;
        this._options = new int[3];
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public int[] getOptions() {
        return this._options;
    }
    
    public void setOption(final byte index, final int option) {
        if (this._options.length > index) {
            this._options[index] = option;
        }
    }
}
