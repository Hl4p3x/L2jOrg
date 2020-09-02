// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Table;

@Table("item_variation")
public class ItemVariationData
{
    private int mineralId;
    private int option1;
    private int option2;
    
    public int getMineralId() {
        return this.mineralId;
    }
    
    public int getOption1() {
        return this.option1;
    }
    
    public int getOption2() {
        return this.option1;
    }
}
