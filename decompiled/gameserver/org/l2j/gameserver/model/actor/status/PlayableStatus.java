// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.status;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;

public class PlayableStatus extends CreatureStatus
{
    public PlayableStatus(final Playable playable) {
        super(playable);
    }
    
    @Override
    public Playable getOwner() {
        return (Playable)super.getOwner();
    }
}
