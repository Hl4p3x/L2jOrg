// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.options;

import java.util.Iterator;
import org.l2j.commons.util.Rnd;
import java.util.Map;

public final class OptionDataCategory
{
    private final Map<Options, Double> _options;
    private final double _chance;
    
    public OptionDataCategory(final Map<Options, Double> options, final double chance) {
        this._options = options;
        this._chance = chance;
    }
    
    Options getRandomOptions() {
        Options result = null;
        do {
            double random = Rnd.nextDouble() * 100.0;
            for (final Map.Entry<Options, Double> entry : this._options.entrySet()) {
                if (entry.getValue() >= random) {
                    result = entry.getKey();
                    break;
                }
                random -= entry.getValue();
            }
        } while (result == null);
        return result;
    }
    
    public double getChance() {
        return this._chance;
    }
}
