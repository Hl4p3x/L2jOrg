// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.actor.instance.Player;

public enum SkillConditionAlignment
{
    LAWFUL, 
    CHAOTIC;
    
    public boolean test(final Player player) {
        boolean b = false;
        switch (this) {
            case LAWFUL: {
                b = (player.getReputation() >= 0);
                break;
            }
            case CHAOTIC: {
                b = (player.getReputation() < 0);
                break;
            }
            default: {
                throw new IncompatibleClassChangeError();
            }
        }
        return b;
    }
}
