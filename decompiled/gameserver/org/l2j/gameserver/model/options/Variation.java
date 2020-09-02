// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.options;

import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

public final class Variation
{
    private static final Logger LOGGER;
    private final int _mineralId;
    private final Map<VariationWeaponType, OptionDataGroup[]> _effects;
    
    public Variation(final int mineralId) {
        this._effects = new HashMap<VariationWeaponType, OptionDataGroup[]>();
        this._mineralId = mineralId;
    }
    
    public int getMineralId() {
        return this._mineralId;
    }
    
    public void setEffectGroup(final VariationWeaponType type, final int order, final OptionDataGroup group) {
        final OptionDataGroup[] effects = this._effects.computeIfAbsent(type, k -> new OptionDataGroup[2]);
        effects[order] = group;
    }
    
    public Options getRandomEffect(final VariationWeaponType type, final int order) {
        final OptionDataGroup[] effects = this._effects.get(type);
        if (effects == null || effects[order] == null) {
            Variation.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/options/VariationWeaponType;I)Ljava/lang/String;, type, order));
            return null;
        }
        return effects[order].getRandomEffect();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(Variation.class.getSimpleName());
    }
}
