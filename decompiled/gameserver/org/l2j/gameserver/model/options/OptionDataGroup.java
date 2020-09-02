// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.options;

import java.util.Iterator;
import org.l2j.commons.util.Rnd;
import java.util.List;

public final class OptionDataGroup
{
    private final List<OptionDataCategory> _categories;
    
    public OptionDataGroup(final List<OptionDataCategory> categories) {
        this._categories = categories;
    }
    
    Options getRandomEffect() {
        Options result = null;
        do {
            double random = Rnd.nextDouble() * 100.0;
            for (final OptionDataCategory category : this._categories) {
                if (category.getChance() >= random) {
                    result = category.getRandomOptions();
                    break;
                }
                random -= category.getChance();
            }
        } while (result == null);
        return result;
    }
}
