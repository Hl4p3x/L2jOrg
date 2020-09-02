// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.buylist;

import java.util.HashSet;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map;

public final class ProductList
{
    private final int _listId;
    private final Map<Integer, Product> _products;
    private Set<Integer> _allowedNpcs;
    
    public ProductList(final int listId) {
        this._products = new LinkedHashMap<Integer, Product>();
        this._allowedNpcs = null;
        this._listId = listId;
    }
    
    public int getListId() {
        return this._listId;
    }
    
    public Collection<Product> getProducts() {
        return this._products.values();
    }
    
    public Product getProductByItemId(final int itemId) {
        return this._products.get(itemId);
    }
    
    public void addProduct(final Product product) {
        this._products.put(product.getItemId(), product);
    }
    
    public void addAllowedNpc(final int npcId) {
        if (this._allowedNpcs == null) {
            this._allowedNpcs = new HashSet<Integer>();
        }
        this._allowedNpcs.add(npcId);
    }
    
    public boolean isNpcAllowed(final int npcId) {
        return this._allowedNpcs != null && this._allowedNpcs.contains(npcId);
    }
}
