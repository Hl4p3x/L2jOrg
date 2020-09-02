// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

public enum CompetitionType
{
    CLASSED("classed"), 
    NON_CLASSED("non-classed"), 
    OTHER("other");
    
    private final String _name;
    
    private CompetitionType(final String name) {
        this._name = name;
    }
    
    @Override
    public final String toString() {
        return this._name;
    }
}
