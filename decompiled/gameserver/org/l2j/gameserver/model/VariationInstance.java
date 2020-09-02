// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Objects;
import org.l2j.gameserver.data.xml.impl.AugmentationEngine;
import org.l2j.gameserver.model.options.Options;

public final class VariationInstance
{
    private final int _mineralId;
    private final Options _option1;
    private final Options _option2;
    
    public VariationInstance(final int mineralId, final int option1Id, final int option2Id) {
        this._mineralId = mineralId;
        this._option1 = AugmentationEngine.getInstance().getOptions(option1Id);
        this._option2 = AugmentationEngine.getInstance().getOptions(option2Id);
        if (this._option1 == null || this._option2 == null) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, option1Id, option1Id));
        }
    }
    
    public VariationInstance(final int mineralId, final Options op1, final Options op2) {
        Objects.requireNonNull(op1);
        Objects.requireNonNull(op2);
        this._mineralId = mineralId;
        this._option1 = op1;
        this._option2 = op2;
    }
    
    public int getMineralId() {
        return this._mineralId;
    }
    
    public int getOption1Id() {
        return this._option1.getId();
    }
    
    public int getOption2Id() {
        return this._option2.getId();
    }
    
    public void applyBonus(final Player player) {
        this._option1.apply(player);
        this._option2.apply(player);
    }
    
    public void removeBonus(final Player player) {
        this._option1.remove(player);
        this._option2.remove(player);
    }
}
