// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.base.PlayerState;

public class ConditionPlayerChaotic extends ConditionPlayerState
{
    private static ConditionPlayerChaotic CHAOTIC;
    private static ConditionPlayerChaotic NO_CHAOTIC;
    
    private ConditionPlayerChaotic(final boolean isChaotic) {
        super(PlayerState.CHAOTIC, isChaotic);
    }
    
    public static ConditionPlayerChaotic of(final boolean chaotic) {
        return chaotic ? ConditionPlayerChaotic.CHAOTIC : ConditionPlayerChaotic.NO_CHAOTIC;
    }
    
    static {
        ConditionPlayerChaotic.CHAOTIC = new ConditionPlayerChaotic(true);
        ConditionPlayerChaotic.NO_CHAOTIC = new ConditionPlayerChaotic(false);
    }
}
