// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.request.AbstractRequest;

public class PetItemRequest extends AbstractRequest
{
    private final Item _item;
    
    public PetItemRequest(final Player player, final Item item) {
        super(player);
        this._item = item;
    }
    
    public Item getItem() {
        return this._item;
    }
    
    @Override
    public boolean isUsingItem(final int objectId) {
        return this._item.getObjectId() == objectId;
    }
}
