// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

public class TemplateChanceHolder
{
    private final int _templateId;
    private final int _minChance;
    private final int _maxChance;
    
    public TemplateChanceHolder(final int templateId, final int minChance, final int maxChance) {
        this._templateId = templateId;
        this._minChance = minChance;
        this._maxChance = maxChance;
    }
    
    public final int getTemplateId() {
        return this._templateId;
    }
    
    public final boolean calcChance(final int chance) {
        return this._maxChance > chance && chance >= this._minChance;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, this._templateId, this._minChance, this._minChance);
    }
}
