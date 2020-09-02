// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

import org.l2j.commons.util.Util;
import java.util.Collections;
import org.l2j.gameserver.model.StatsSet;
import java.util.List;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public class MultisellListHolder implements IIdentifiable
{
    protected final IntSet _npcsAllowed;
    private final int _listId;
    private final boolean _isChanceMultisell;
    private final boolean _applyTaxes;
    private final boolean _maintainEnchantment;
    private final double _ingredientMultiplier;
    private final double _productMultiplier;
    protected List<MultisellEntryHolder> _entries;
    
    public MultisellListHolder(final int listId, final boolean isChanceMultisell, final boolean applyTaxes, final boolean maintainEnchantment, final double ingredientMultiplier, final double productMultiplier, final List<MultisellEntryHolder> entries, final IntSet npcsAllowed) {
        this._listId = listId;
        this._isChanceMultisell = isChanceMultisell;
        this._applyTaxes = applyTaxes;
        this._maintainEnchantment = maintainEnchantment;
        this._ingredientMultiplier = ingredientMultiplier;
        this._productMultiplier = productMultiplier;
        this._entries = entries;
        this._npcsAllowed = npcsAllowed;
    }
    
    public MultisellListHolder(final StatsSet set) {
        this._listId = set.getInt("listId");
        this._isChanceMultisell = set.getBoolean("isChanceMultisell", false);
        this._applyTaxes = set.getBoolean("applyTaxes", false);
        this._maintainEnchantment = set.getBoolean("maintainEnchantment", false);
        this._ingredientMultiplier = set.getDouble("ingredientMultiplier", 1.0);
        this._productMultiplier = set.getDouble("productMultiplier", 1.0);
        this._entries = Collections.unmodifiableList((List<? extends MultisellEntryHolder>)set.getList("entries", (Class<? extends T>)MultisellEntryHolder.class, Collections.emptyList()));
        this._npcsAllowed = set.getObject("allowNpc", IntSet.class);
    }
    
    public List<MultisellEntryHolder> getEntries() {
        return this._entries;
    }
    
    @Override
    public final int getId() {
        return this._listId;
    }
    
    public final boolean isChanceMultisell() {
        return this._isChanceMultisell;
    }
    
    public final boolean isApplyTaxes() {
        return this._applyTaxes;
    }
    
    public final boolean isMaintainEnchantment() {
        return this._maintainEnchantment;
    }
    
    public final double getIngredientMultiplier() {
        return this._ingredientMultiplier;
    }
    
    public final double getProductMultiplier() {
        return this._productMultiplier;
    }
    
    public final boolean isNpcAllowed(final int npcId) {
        return Util.falseIfNullOrElse((Object)this._npcsAllowed, list -> list.contains(npcId));
    }
}
