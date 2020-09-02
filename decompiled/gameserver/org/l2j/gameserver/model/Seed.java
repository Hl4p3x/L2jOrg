// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.item.ItemEngine;

public final class Seed
{
    private final int _seedId;
    private final int _cropId;
    private final int _level;
    private final int _matureId;
    private final int _reward1;
    private final int _reward2;
    private final int _castleId;
    private final boolean _isAlternative;
    private final int _limitSeeds;
    private final int _limitCrops;
    private final long _seedReferencePrice;
    private final long _cropReferencePrice;
    
    public Seed(final StatsSet set) {
        this._cropId = set.getInt("id");
        this._seedId = set.getInt("seedId");
        this._level = set.getInt("level");
        this._matureId = set.getInt("mature_Id");
        this._reward1 = set.getInt("reward1");
        this._reward2 = set.getInt("reward2");
        this._castleId = set.getInt("castleId");
        this._isAlternative = set.getBoolean("alternative");
        this._limitCrops = set.getInt("limit_crops");
        this._limitSeeds = set.getInt("limit_seed");
        ItemTemplate item = ItemEngine.getInstance().getTemplate(this._cropId);
        this._cropReferencePrice = ((item != null) ? item.getReferencePrice() : 1L);
        item = ItemEngine.getInstance().getTemplate(this._seedId);
        this._seedReferencePrice = ((item != null) ? item.getReferencePrice() : 1L);
    }
    
    public final int getCastleId() {
        return this._castleId;
    }
    
    public final int getSeedId() {
        return this._seedId;
    }
    
    public final int getCropId() {
        return this._cropId;
    }
    
    public final int getMatureId() {
        return this._matureId;
    }
    
    public final int getReward(final int type) {
        return (type == 1) ? this._reward1 : this._reward2;
    }
    
    public final int getLevel() {
        return this._level;
    }
    
    public final boolean isAlternative() {
        return this._isAlternative;
    }
    
    public final int getSeedLimit() {
        return this._limitSeeds * Config.RATE_DROP_MANOR;
    }
    
    public final int getCropLimit() {
        return this._limitCrops * Config.RATE_DROP_MANOR;
    }
    
    public final long getSeedReferencePrice() {
        return this._seedReferencePrice;
    }
    
    public final long getSeedMaxPrice() {
        return this._seedReferencePrice * 10L;
    }
    
    public final int getSeedMinPrice() {
        return (int)(this._seedReferencePrice * 0.6);
    }
    
    public final long getCropReferencePrice() {
        return this._cropReferencePrice;
    }
    
    public final long getCropMaxPrice() {
        return this._cropReferencePrice * 10L;
    }
    
    public final int getCropMinPrice() {
        return (int)(this._cropReferencePrice * 0.6);
    }
    
    @Override
    public final String toString() {
        return invokedynamic(makeConcatWithConstants:(IIIIIIIZII)Ljava/lang/String;, this._seedId, this._level, this._cropId, this._matureId, this._reward1, this._reward2, this._castleId, this._isAlternative, this._limitSeeds, this._limitCrops);
    }
}
