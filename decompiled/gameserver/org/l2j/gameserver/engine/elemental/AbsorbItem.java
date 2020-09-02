// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.elemental;

public class AbsorbItem
{
    private final int id;
    private final int experience;
    
    AbsorbItem(final int itemId, final int experience) {
        this.id = itemId;
        this.experience = experience;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getExperience() {
        return this.experience;
    }
}
