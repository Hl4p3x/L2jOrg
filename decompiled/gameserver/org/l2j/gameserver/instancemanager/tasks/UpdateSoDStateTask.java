// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager.tasks;

import org.l2j.gameserver.instancemanager.GraciaSeedsManager;

public final class UpdateSoDStateTask implements Runnable
{
    @Override
    public void run() {
        final GraciaSeedsManager manager = GraciaSeedsManager.getInstance();
        manager.setSoDState(1, true);
        manager.updateSodState();
    }
}
