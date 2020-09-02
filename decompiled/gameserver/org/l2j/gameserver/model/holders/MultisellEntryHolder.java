// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.item.ItemTemplate;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.engine.item.ItemEngine;
import java.util.Collections;
import java.util.List;

public class MultisellEntryHolder
{
    private final boolean _stackable;
    private final List<ItemChanceHolder> _ingredients;
    private final List<ItemChanceHolder> _products;
    
    public MultisellEntryHolder(final List<ItemChanceHolder> ingredients, final List<ItemChanceHolder> products) {
        this._ingredients = Collections.unmodifiableList((List<? extends ItemChanceHolder>)ingredients);
        this._products = Collections.unmodifiableList((List<? extends ItemChanceHolder>)products);
        this._stackable = products.stream().map(i -> ItemEngine.getInstance().getTemplate(i.getId())).filter(Objects::nonNull).allMatch(ItemTemplate::isStackable);
    }
    
    public final List<ItemChanceHolder> getIngredients() {
        return this._ingredients;
    }
    
    public final List<ItemChanceHolder> getProducts() {
        return this._products;
    }
    
    public final boolean isStackable() {
        return this._stackable;
    }
}
