// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager.tasks;

import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;

public final class PenaltyRemoveTask implements Runnable
{
    private final int _objectId;
    
    public PenaltyRemoveTask(final int id) {
        this._objectId = id;
    }
    
    @Override
    public void run() {
        HandysBlockCheckerManager.getInstance().removePenalty(this._objectId);
    }
}
