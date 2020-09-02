// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item.container;

import org.l2j.gameserver.model.actor.Creature;
import java.util.Iterator;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.enums.ItemLocation;
import org.l2j.gameserver.model.actor.instance.Player;

public class PlayerRefund extends ItemContainer
{
    private final Player _owner;
    
    public PlayerRefund(final Player owner) {
        this._owner = owner;
    }
    
    @Override
    public String getName() {
        return "Refund";
    }
    
    @Override
    public Player getOwner() {
        return this._owner;
    }
    
    public ItemLocation getBaseLocation() {
        return ItemLocation.REFUND;
    }
    
    @Override
    protected void addItem(final Item item) {
        super.addItem(item);
        try {
            if (this.getSize() > 12) {
                final Item removedItem = (Item)this.items.remove(0);
                if (removedItem != null) {
                    ItemEngine.getInstance().destroyItem("ClearRefund", removedItem, this.getOwner(), null);
                    removedItem.updateDatabase(true);
                }
            }
        }
        catch (Exception e) {
            PlayerRefund.LOGGER.error("addItem()", (Throwable)e);
        }
    }
    
    public void refreshWeight() {
    }
    
    @Override
    public void deleteMe() {
        try {
            for (final Item item : this.items.values()) {
                ItemEngine.getInstance().destroyItem("ClearRefund", item, this.getOwner(), null);
                item.updateDatabase(true);
            }
        }
        catch (Exception e) {
            PlayerRefund.LOGGER.error("deleteMe()", (Throwable)e);
        }
        this.items.clear();
    }
    
    @Override
    public void restore() {
    }
}
