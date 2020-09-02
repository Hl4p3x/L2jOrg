// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.item.instance.Item;
import java.util.function.Predicate;
import java.util.ArrayList;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.ItemInfo;
import java.util.List;

public class PreparedMultisellListHolder extends MultisellListHolder
{
    private final boolean _inventoryOnly;
    private int _npcObjectId;
    private double _taxRate;
    private List<ItemInfo> _itemInfos;
    
    public PreparedMultisellListHolder(final MultisellListHolder list, final boolean inventoryOnly, final ItemContainer inventory, final Npc npc, final double ingredientMultiplier, final double productMultiplier) {
        super(list.getId(), list.isChanceMultisell(), list.isApplyTaxes(), list.isMaintainEnchantment(), list.getIngredientMultiplier(), list.getProductMultiplier(), list._entries, list._npcsAllowed);
        this._inventoryOnly = inventoryOnly;
        if (npc != null) {
            this._npcObjectId = npc.getObjectId();
            this._taxRate = npc.getCastleTaxRate(TaxType.BUY);
        }
        if (inventoryOnly) {
            this._entries = new ArrayList<MultisellEntryHolder>();
            this._itemInfos = new ArrayList<ItemInfo>();
            inventory.getItems(item -> !item.isEquipped() && (item.isArmor() || item.isWeapon()), (Predicate<Item>[])new Predicate[0]).forEach(item -> list.getEntries().stream().filter(e -> e.getIngredients().stream().anyMatch(i -> i.getId() == item.getId())).forEach(e -> {
                this._entries.add(e);
                this._itemInfos.add(new ItemInfo(item));
            }));
        }
    }
    
    public ItemInfo getItemEnchantment(final int index) {
        return (this._itemInfos != null) ? this._itemInfos.get(index) : null;
    }
    
    public double getTaxRate() {
        return this.isApplyTaxes() ? this._taxRate : 0.0;
    }
    
    public boolean isInventoryOnly() {
        return this._inventoryOnly;
    }
    
    public final boolean checkNpcObjectId(final int npcObjectId) {
        return this._npcObjectId == 0 || this._npcObjectId == npcObjectId;
    }
    
    public long getIngredientCount(final ItemHolder ingredient) {
        return (ingredient.getId() == 57) ? Math.round(ingredient.getCount() * this.getIngredientMultiplier() * (1.0 + this.getTaxRate())) : Math.round(ingredient.getCount() * this.getIngredientMultiplier());
    }
    
    public long getProductCount(final ItemChanceHolder product) {
        return Math.round(product.getCount() * this.getProductMultiplier());
    }
}
