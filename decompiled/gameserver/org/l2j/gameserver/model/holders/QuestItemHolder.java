// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.holders;

public class QuestItemHolder extends ItemHolder
{
    private final int _chance;
    
    public QuestItemHolder(final int id, final int chance) {
        this(id, chance, 1L);
    }
    
    public QuestItemHolder(final int id, final int chance, final long count) {
        super(id, count);
        this._chance = chance;
    }
    
    public int getChance() {
        return this._chance;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IJI)Ljava/lang/String;, this.getClass().getSimpleName(), this.getId(), this.getCount(), this._chance);
    }
}
