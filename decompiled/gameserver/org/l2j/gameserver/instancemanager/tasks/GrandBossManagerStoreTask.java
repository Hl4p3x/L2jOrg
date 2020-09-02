// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager.tasks;

import org.l2j.gameserver.instancemanager.GrandBossManager;

public class GrandBossManagerStoreTask implements Runnable
{
    @Override
    public void run() {
        GrandBossManager.getInstance().storeMe();
    }
}
