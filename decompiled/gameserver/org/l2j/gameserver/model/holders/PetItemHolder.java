// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.item.instance.Item;

public class PetItemHolder
{
    private final Item _item;
    
    public PetItemHolder(final Item item) {
        this._item = item;
    }
    
    public Item getItem() {
        return this._item;
    }
}
