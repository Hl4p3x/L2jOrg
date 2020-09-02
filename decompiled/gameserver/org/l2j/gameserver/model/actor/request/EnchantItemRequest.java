// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.request;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;

public final class EnchantItemRequest extends AbstractRequest
{
    private volatile int enchantingItemObjectId;
    private volatile int enchantingScrollObjectId;
    private volatile int supportItemObjectId;
    
    public EnchantItemRequest(final Player player, final int enchantingScrollObjectId) {
        super(player);
        this.enchantingScrollObjectId = enchantingScrollObjectId;
    }
    
    public Item getEnchantingItem() {
        return this.player.getInventory().getItemByObjectId(this.enchantingItemObjectId);
    }
    
    public void setEnchantingItem(final int objectId) {
        this.enchantingItemObjectId = objectId;
    }
    
    public Item getEnchantingScroll() {
        return this.player.getInventory().getItemByObjectId(this.enchantingScrollObjectId);
    }
    
    public void setEnchantingScroll(final int objectId) {
        this.enchantingScrollObjectId = objectId;
    }
    
    public Item getSupportItem() {
        return this.getPlayer().getInventory().getItemByObjectId(this.supportItemObjectId);
    }
    
    public void setSupportItem(final int objectId) {
        this.supportItemObjectId = objectId;
    }
    
    @Override
    public boolean isItemRequest() {
        return true;
    }
    
    @Override
    public boolean canWorkWith(final AbstractRequest request) {
        return !request.isItemRequest();
    }
    
    @Override
    public boolean isUsingItem(final int objectId) {
        return objectId > 0 && (objectId == this.enchantingItemObjectId || objectId == this.enchantingScrollObjectId || objectId == this.supportItemObjectId);
    }
}
